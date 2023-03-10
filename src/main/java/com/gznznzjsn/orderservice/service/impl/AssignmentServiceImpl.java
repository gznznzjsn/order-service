package com.gznznzjsn.orderservice.service.impl;


import com.gznznzjsn.orderservice.domain.Assignment;
import com.gznznzjsn.orderservice.domain.AssignmentStatus;
import com.gznznzjsn.orderservice.domain.Order;
import com.gznznzjsn.orderservice.domain.Task;
import com.gznznzjsn.orderservice.domain.exception.IllegalActionException;
import com.gznznzjsn.orderservice.domain.exception.NotEnoughResourcesException;
import com.gznznzjsn.orderservice.domain.exception.ResourceNotFoundException;
import com.gznznzjsn.orderservice.repository.AssignmentRepository;
import com.gznznzjsn.orderservice.service.AssignmentService;
import com.gznznzjsn.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final OrderService orderService;
//todo    private final WebClient.Builder webClientBuilder;
//        Boolean isAcceptable = webClientBuilder.build().get().uri("http://inventory-service/api/inventory")
//                .retrieve()
//                .bodyToMono(Boolean.class)
//                .block();
//        if (Boolean.FALSE.equals(isAcceptable)) {
//            throw new NotEnoughResourcesException("We are out of order forms!)))");
//        }

    @Override
    @Transactional
    public Mono<Assignment> create(Assignment assignment) {
        Mono<Assignment> assignmentMono = Mono.just(assignment)
                .map(a -> Assignment.builder()
                        .id(a.getId())
                        .status(AssignmentStatus.NOT_SENT)
                        .order(a.getOrder())
                        .tasks(a.getTasks())
                        .userCommentary(a.getUserCommentary())
                        .build());
        Mono<Order> orderMono = assignmentMono
                .map(a -> a.getOrder().getId())
                .flatMap(orderService::get);
        Mono<List<Task>> taskListMono = assignmentMono
                .map(Assignment::getTasks);
        return Mono.empty();
//        return Mono.zip(assignmentMono, orderMono, taskListMono)
//                .flatMap(data -> Mono.zip(data.getT1(), data.getT2(), data.getT3());


//todo        Order order = orderService.get(assignmentToCreate.getOrder().getId());
//        assignmentToCreate.setOrder(order);
//        if (!OrderStatus.NOT_SENT.equals(assignmentToCreate.getOrder().getStatus())) {
//            throw new IllegalActionException("You can't add assignment to already sent order!");
//        }
//        List<Task> tasks = assignmentToCreate.getTasks();
//        if (tasks == null || tasks.isEmpty()) {
//            throw new NotEnoughResourcesException("You can't create assignment without tasks!");
//        }
//        Specialization probableSpecialization = taskService.get(tasks.get(0).getId()).getSpecialization();
//        for (Task task : tasks) {
//            if (!taskService.get(task.getId()).getSpecialization().equals(probableSpecialization)) {
//                throw new IllegalActionException("You can't create assignment with multiple specializations!");
//            }
//        }
//        assignmentToCreate.setSpecialization(probableSpecialization);
//        assignmentRepository.create(assignmentToCreate);
//        assignmentRepository.createTasks(assignmentToCreate);
//        assignmentToCreate.setTasks(taskService.getTasks(assignmentToCreate.getId()));
//        return assignmentToCreate;
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
                    assignmentFromRepository.setStatus(assignment.getStatus());
                    assignmentFromRepository.setStartTime(assignment.getStartTime());
                    assignmentFromRepository.setFinalCost(assignment.getFinalCost());
                    assignmentFromRepository.setEmployee(assignment.getEmployee());
                    assignmentFromRepository.setUserCommentary(assignment.getUserCommentary());
                    assignmentFromRepository.setEmployeeCommentary(assignment.getEmployeeCommentary());
                    assignmentFromRepository.setTasks(assignment.getTasks());
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
