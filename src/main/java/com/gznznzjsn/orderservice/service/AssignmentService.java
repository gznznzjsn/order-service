package com.gznznzjsn.orderservice.service;

import com.gznznzjsn.orderservice.domain.Assignment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface AssignmentService {

    static BigDecimal calculateTotalCost(Assignment assignment) {
        return assignment.getTasks().stream()
                .map(t -> t.getCostPerHour().multiply(BigDecimal.valueOf(t.getDuration())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    Mono<Assignment> create(Assignment assignment);

    Flux<Assignment> sendWithOrder(Long orderId);

    Mono<Assignment> update(Assignment assignment);

    Mono<Assignment> get(Long assignmentId);

    Flux<Assignment> getAllByOrderId(Long orderId);

    Mono<Assignment> accept(Assignment assignment);
}
