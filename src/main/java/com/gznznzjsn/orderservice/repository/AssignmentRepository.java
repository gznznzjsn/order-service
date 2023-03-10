package com.gznznzjsn.orderservice.repository;

import com.gznznzjsn.orderservice.domain.Assignment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AssignmentRepository extends R2dbcRepository<Assignment, Long> {

    Flux<Assignment> findAllByOrderId(Long orderId);
}
