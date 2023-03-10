package com.gznznzjsn.orderservice.service.impl;


import com.gznznzjsn.orderservice.domain.Order;
import com.gznznzjsn.orderservice.domain.OrderStatus;
import com.gznznzjsn.orderservice.domain.exception.IllegalActionException;
import com.gznznzjsn.orderservice.domain.exception.ResourceNotFoundException;
import com.gznznzjsn.orderservice.repository.OrderRepository;
import com.gznznzjsn.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Mono<Order> create(Order order) {
        return Mono.just(order)
                .flatMap(o -> {
                    if (o.getUser() == null) {
                        return Mono.error(new IllegalActionException("You can't create order without user!"));
                    }
                    return Mono.just(o);
                })
                .map(o -> Order.builder()
                        .status(OrderStatus.NOT_SENT)
                        .user(o.getUser())
                        .arrivalTime(order.getArrivalTime())
                        .build()
                )
                .flatMap(orderRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Order> get(Long orderId) {
        return orderRepository
                .findById(orderId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Order with id = " + orderId + " doesn't exist!")
                ));
    }

    @Override
    @Transactional
    public Mono<Order> send(Long orderId) {
        return get(orderId)
                .flatMap(order -> {
                            if (!order.getStatus().equals(OrderStatus.NOT_SENT)) {
                                return Mono.error(
                                        new IllegalActionException("You can't send order with id = " + order.getId() + ", because it's already sent!")
                                );
                            }
                            return Mono.just(order);
                        }
                )
                .map(order -> Order.builder()
                        .id(orderId)
                        .status(OrderStatus.UNDER_CONSIDERATION)
                        .build())
                .flatMap(this::update);
    }

    @Override
    @Transactional
    public Mono<Order> update(Order order) {
        return Mono.just(order)
                .flatMap(o -> get(o.getId()))
                .map(orderFromRepository -> {
                    orderFromRepository.setStatus(order.getStatus());
                    orderFromRepository.setArrivalTime(order.getArrivalTime());
                    return orderFromRepository;
                })
                .flatMap(orderRepository::save);
    }

}
