package com.gznznzjsn.orderservice.service.impl;


import com.gznznzjsn.orderservice.domain.*;
import com.gznznzjsn.orderservice.domain.exception.IllegalActionException;
import com.gznznzjsn.orderservice.domain.exception.NotEnoughResourcesException;
import com.gznznzjsn.orderservice.domain.exception.ResourceNotFoundException;
import com.gznznzjsn.orderservice.persistence.repository.AssignmentRepository;
import com.gznznzjsn.orderservice.service.AssignmentService;
import com.gznznzjsn.orderservice.service.OrderService;
import com.gznznzjsn.orderservice.web.dto.TaskDto;
import com.gznznzjsn.orderservice.web.dto.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final OrderService orderService;
    private final WebClient.Builder webClientBuilder;
    private final TaskMapper taskMapper;

    private Flux<Task> fetchTasksByAssignment(Assignment assignment) {
        return Mono.just(assignment)
                .flatMapMany(a -> webClientBuilder.build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .scheme("http")
                                .host("task-service")
                                .path("/task-api/v1/tasks")
                                .queryParam("taskId",
                                        a.getTasks().stream()
                                                .map(Task::getId)
                                                .toList())
                                .build()
                        )
                        .retrieve()
                        .bodyToFlux(TaskDto.class)
                )
                .map(taskMapper::toEntity);
    }


    @Override
    @Transactional
    public Mono<Assignment> create(Assignment assignment) {
        Mono<Tuple2<List<Task>, Specialization>> tasksAndSpecializationMono = fetchTasksByAssignment(assignment)
                .switchIfEmpty(Flux.error(new NotEnoughResourcesException("You can't create assignment without tasks!")))
                .collectList()
                .flatMap(tasks -> {
                    Specialization probableSpecialization = tasks.get(0).getSpecialization();
                    for (Task task : tasks) {
                        if (!probableSpecialization.equals(task.getSpecialization())) {
                            return Mono.error(
                                    new IllegalActionException("You can't create assignment with multiple specializations!")
                            );
                        }
                    }
                    return Mono.just(Tuples.of(tasks, probableSpecialization));
                });
        Mono<Order> orderMono = Mono.just(assignment)
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
        Mono<Assignment> cachedAssignmentMono = Mono.zip(tasksAndSpecializationMono, orderMono, Mono.just(assignment))
                .map(t -> {
                    Assignment a = t.getT3();
                    List<Task> tasks = t.getT1().getT1();
                    Specialization specialization = t.getT1().getT2();
                    return Assignment.builder()
                            .specialization(specialization)
                            .status(AssignmentStatus.NOT_SENT)
                            .order(a.getOrder())
                            .tasks(tasks)
                            .userCommentary(a.getUserCommentary())
                            .build();
                })
                .flatMap(assignmentRepository::save)
                .cache();
        return cachedAssignmentMono
                .flatMapMany(a ->
                        Flux.fromIterable(a.getTasks().stream()
                                .map(
                                        task -> Tuples.of(task.getId(), a.getId())
                                )
                                .toList()
                        )
                )
                .flatMap(t -> assignmentRepository.saveTaskForAssignment(t.getT1(), t.getT2()))
                .then(cachedAssignmentMono);
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
