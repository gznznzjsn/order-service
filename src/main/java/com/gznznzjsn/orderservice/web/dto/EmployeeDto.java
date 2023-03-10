package com.gznznzjsn.orderservice.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmployeeDto(

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id

) {
}
