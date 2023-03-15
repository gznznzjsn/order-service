package com.gznznzjsn.orderservice.persistence.repository;

import com.gznznzjsn.orderservice.domain.Order;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface OrderRepository extends R2dbcRepository<Order, Long> {
}
