package com.gznznzjsn.orderservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    private String id;
    private String name;
    private Integer duration;
    private BigDecimal costPerHour;
    private Specialization specialization;

}
