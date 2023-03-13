package com.gznznzjsn.orderservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("assignments")
public class Assignment {

    @Id
    @Column("assignment_id")
    private Long id;
    private Order order;
    private AssignmentStatus status;
    private Specialization specialization;
    private LocalDateTime startTime;
    private BigDecimal finalCost;
    private Employee employee;
    private List<Task> tasks;
    private String userCommentary;
    private String employeeCommentary;

}
