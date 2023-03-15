package com.gznznzjsn.orderservice.web.dto;

import com.gznznzjsn.orderservice.domain.Specialization;

public record EmployeeDto(

        Long id,
        String name,
        Specialization specialization

) {
}
