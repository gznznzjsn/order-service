package com.gznznzjsn.orderservice.persistence.converter;

import com.gznznzjsn.orderservice.domain.*;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ReadingConverter
public class AssignmentReadConverter implements Converter<Row, Assignment> {

    @Override
    public Assignment convert(Row source) {
        return Assignment.builder()
                .id(source.get("assignment_id", Long.class))
                .order(Order.builder()
                        .id(source.get("order_id", Long.class))
                        .user(User.builder()
                                .id(source.get("order_user_id", Long.class))
                                .build())
                        .status(OrderStatus.valueOf(source.get("order_status", String.class)))
                        .arrivalTime(source.get("order_arrival_time", LocalDateTime.class))
                        .createdAt(source.get("order_created_at", LocalDateTime.class))
                        .finishedAt(source.get("order_finished_at", LocalDateTime.class))
                        .build()
                )
                .specialization(Specialization.valueOf(source.get("specialization", String.class)))
                .employee(Employee.builder()
                        .id(source.get("employee_id", Long.class))
                        .build()
                )
                .status(AssignmentStatus.valueOf(source.get("status", String.class)))
                .startTime(source.get("start_time", LocalDateTime.class))
                .finalCost(source.get("final_cost", BigDecimal.class))
                .userCommentary(source.get("user_commentary", String.class))
                .employeeCommentary(source.get("employee_commentary", String.class))
                .build();
    }
}