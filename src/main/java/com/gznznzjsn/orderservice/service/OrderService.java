package com.gznznzjsn.orderservice.service;

import com.gznznzjsn.orderservice.domain.Order;
import reactor.core.publisher.Mono;

public interface OrderService {

    Mono<Order> create(Order order);

    Mono<Order> get(Long orderId);

    Mono<Order> send(Long orderId);

    Mono<Order> update(Order order);

}
