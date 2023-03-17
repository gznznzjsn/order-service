package com.gznznzjsn.orderservice.persistence.repository;

import com.gznznzjsn.orderservice.domain.Assignment;
import com.gznznzjsn.orderservice.domain.Task;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AssignmentRepository extends R2dbcRepository<Assignment, Long> {

    @Query("""
                 SELECT assignment_id,
                   specialization         as "specialization",
                   a.status         as "status",
                   start_time          as "start_time",
                   final_cost          as "final_cost",
                   user_commentary     as "user_commentary",
                   employee_commentary as "employee_commentary",
                   order_id            as "order_id",
                   o.status         as "order_status",
                   arrival_time        as "order_arrival_time",
                   created_at          as "order_created_at",
                   finished_at         as "order_finished_at",
                   o.user_id             as "order_user_id",
                   employee_id       as "employee_id"
            FROM assignments a
                     JOIN orders o USING (order_id)
                WHERE assignment_id = $1
                """)
    Mono<Assignment> findById(Long assignmentId);

    @Query("""
                 SELECT assignment_id,
                   specialization         as "specialization",
                   a.status         as "status",
                   start_time          as "start_time",
                   final_cost          as "final_cost",
                   user_commentary     as "user_commentary",
                   employee_commentary as "employee_commentary",
                   order_id            as "order_id",
                   o.status         as "order_status",
                   arrival_time        as "order_arrival_time",
                   created_at          as "order_created_at",
                   finished_at         as "order_finished_at",
                   o.user_id             as "order_user_id",
                   employee_id       as "employee_id"
            FROM assignments a
                     JOIN orders o USING (order_id)
                WHERE order_id = $1
                """)
    Flux<Assignment> findAllByOrderId(Long orderId);

    @Query("""
            INSERT INTO assignments_tasks (assignment_id, task_id)
            VALUES($2,$1);
            """)
    Mono<Void> saveTaskForAssignment(Long taskId, Long assignmentId);

    @Query("""
            SELECT task_id
            FROM assignments_tasks
            WHERE assignment_id = $1;
            """)
    Flux<Task> findTasksByAssignmentId(Long assignmentId);


}
