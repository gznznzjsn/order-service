package com.gznznzjsn.orderservice.persistence.repository;

import com.gznznzjsn.orderservice.domain.Assignment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AssignmentRepository extends R2dbcRepository<Assignment, Long> {

    Flux<Assignment> findAllByOrderId(Long orderId);

    @Query("""
            INSERT INTO assignments_tasks (assignment_id, task_id)
            VALUES($2,$1);
            """)
    Mono<Void> saveTaskForAssignment(Long taskId,Long assignmentId);

}
