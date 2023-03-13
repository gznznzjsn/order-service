package com.gznznzjsn.orderservice.service.impl;


import com.gznznzjsn.orderservice.domain.*;
import com.gznznzjsn.orderservice.domain.exception.IllegalActionException;
import com.gznznzjsn.orderservice.domain.exception.NotEnoughResourcesException;
import com.gznznzjsn.orderservice.domain.exception.ResourceNotFoundException;
import com.gznznzjsn.orderservice.persistence.repository.AssignmentRepository;
import com.gznznzjsn.orderservice.service.AssignmentService;
import com.gznznzjsn.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final OrderService orderService;
    private final WebClient.Builder webClientBuilder;

    @Override
    @Transactional
    public Mono<Assignment> create(Assignment assignment) {
        Mono<Assignment> cachedAssignmentMono = Mono.just(assignment)
                .map(a -> Assignment.builder()
                        .id(a.getId())
                        .status(AssignmentStatus.NOT_SENT)
                        .order(a.getOrder())
                        .tasks(a.getTasks())
                        .userCommentary(a.getUserCommentary())
                        .build()).cache();
        Mono<Order> orderMono = cachedAssignmentMono
                .map(a -> a.getOrder().getId())
                .flatMap(orderService::get)
                .flatMap(order -> {
                    if (!OrderStatus.NOT_SENT.equals(order.getStatus())) {
                        return Mono.error(
                                new IllegalActionException("You can't add assignment to already sent order!")
                        );
                    }
                    return Mono.just(order);
                });
        Mono<Specialization> specializationMono = cachedAssignmentMono
                .map(Assignment::getTasks)
                .map(tasks -> {
                    tasks.forEach(task -> {
                        task.setSpecialization(Specialization.CLEANER);
                    });
                    return tasks;
                }) //todo fetch from task service instead of hardcoding
                .flatMap(tasks -> {
                            if (tasks == null || tasks.isEmpty()) {
                                return Mono.error(
                                        new NotEnoughResourcesException("You can't create assignment without tasks!")
                                );
                            }
                            return Mono.just(tasks);
                        }
                )
                .flatMap(tasks -> {
                    Specialization probableSpecialization = tasks.get(0).getSpecialization();
                    for (Task task : tasks) {
                        if (!probableSpecialization.equals(task.getSpecialization())) {
                            return Mono.error(
                                    new IllegalActionException("You can't create assignment with multiple specializations!")
                            );
                        }
                    }
                    return Mono.just(probableSpecialization);
                });//todo set tasks somehow
        cachedAssignmentMono = Mono.zip(cachedAssignmentMono, orderMono, specializationMono)
                .map(t -> {
                    Assignment a = t.getT1();
                    a.setSpecialization(t.getT3());
                    return a;
                })
                .cache();
        Mono<Assignment> createdAssignmentMono = cachedAssignmentMono
                .flatMap(assignmentRepository::save);
        Mono<Assignment> createAssignmentWithTasksMono = cachedAssignmentMono
// todo implement method   .flatMap(assignmentRepository::saveAssignmentWithTasks);
                ;
        return Mono.zip(createdAssignmentMono, createAssignmentWithTasksMono)
                .map(Tuple2::getT1);

    }

    @Override
    @Transactional
    public Flux<Assignment> sendWithOrder(Long orderId) {
        return orderService
                .send(orderId)
                .flatMapMany(o -> getAllByOrderId(o.getId()))
                .switchIfEmpty(Mono.error(
                        new NotEnoughResourcesException("You cannot send order without assignments!")
                ))
                .flatMap(a -> {
                    if (!AssignmentStatus.NOT_SENT.equals(a.getStatus())) {
                        return Mono.error(new IllegalActionException("You can't send assignment with id = " + a.getId() + ", because it's already sent!"));
                    }
//todo
//                    int totalDuration = a.getTasks().stream()
//                            .map(Task::getDuration)
//                            .reduce(0, Integer::sum);
//                    Period appropriatePeriod = periodService.eraseAppropriate(a.getOrder().getArrivalTime(), a.getSpecialization(), totalDuration);
//

                    return Mono.just(a);
                })
                .map(assignment -> {
                    assignment.setStatus(AssignmentStatus.UNDER_CONSIDERATION);
// todo                   assignment.setEmployee(appropriatePeriod.getEmployee());
//                        assignment.setStartTime(appropriatePeriod.getDate().atTime(appropriatePeriod.getStart(), 0))
                    return assignment;
                })
                .flatMap(this::update);
    }

    @Override
    @Transactional
    public Mono<Assignment> update(Assignment assignment) {
        return Mono.just(assignment)
                .flatMap(a -> get(a.getId()))
                .map(assignmentFromRepository -> {
                    if (assignment.getStatus() != null) {
                        assignmentFromRepository.setStatus(assignment.getStatus());
                    }
                    if (assignment.getStartTime() != null) {
                        assignmentFromRepository.setStartTime(assignment.getStartTime());
                    }
                    if (assignment.getFinalCost() != null) {
                        assignmentFromRepository.setFinalCost(assignment.getFinalCost());
                    }
                    if (assignment.getEmployee() != null) {
                        assignmentFromRepository.setEmployee(assignment.getEmployee());
                    }
                    if (assignment.getUserCommentary() != null) {
                        assignmentFromRepository.setUserCommentary(assignment.getUserCommentary());
                    }
                    if (assignment.getEmployeeCommentary() != null) {
                        assignmentFromRepository.setEmployeeCommentary(assignment.getEmployeeCommentary());
                    }
                    if (assignment.getTasks() != null) {
                        assignmentFromRepository.setTasks(assignment.getTasks());
                    }
                    return assignmentFromRepository;
                })
                .flatMap(assignmentRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Assignment> get(Long assignmentId) {
        return assignmentRepository
                .findById(assignmentId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Assignment with id = " + assignmentId + " doesn't exist!")
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Assignment> getAllByOrderId(Long orderId) {
        return assignmentRepository
                .findAllByOrderId(orderId);
    }

    @Override
    @Transactional
    public Mono<Assignment> accept(Assignment assignment) {
        return Mono.just(assignment)
                .flatMap(a -> get(a.getId()))
                .flatMap(existingAssignment -> {
                    if (!AssignmentStatus.UNDER_CONSIDERATION.equals(existingAssignment.getStatus())) {
                        return Mono.error(new IllegalActionException("Assignment with id = " + existingAssignment.getId() + " is not under consideration!"));
                    }
                    return Mono.just(assignment);
                })
                .map(a -> {
                            a.setStatus(AssignmentStatus.ACCEPTED);
                            return a;
                        }
                )
                .flatMap(this::update);
    }

}
