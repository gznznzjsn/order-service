package com.gznznzjsn.orderservice.repository;

import com.gznznzjsn.orderservice.domain.Order;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends R2dbcRepository<Order, Long> {
}
