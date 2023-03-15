package com.gznznzjsn.orderservice.web.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.gznznzjsn.orderservice.domain.OrderStatus;
import com.gznznzjsn.orderservice.web.dto.group.OnCreateOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDto(

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @Valid
        UserDto user,

        @NotNull(message = "Arrival time must be set!", groups = {OnCreateOrder.class})
        LocalDateTime arrivalTime,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        OrderStatus status,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime createdAt,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime finishedAt,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        BigDecimal totalCost,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime finishTime

) {
}
