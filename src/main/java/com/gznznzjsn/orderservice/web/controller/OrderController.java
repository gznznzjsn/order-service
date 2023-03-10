package com.gznznzjsn.orderservice.web.controller;

import com.gznznzjsn.orderservice.domain.User;
import com.gznznzjsn.orderservice.service.AssignmentService;
import com.gznznzjsn.orderservice.service.OrderService;
import com.gznznzjsn.orderservice.web.dto.AssignmentDto;
import com.gznznzjsn.orderservice.web.dto.OrderDto;
import com.gznznzjsn.orderservice.web.dto.group.OnCreateOrder;
import com.gznznzjsn.orderservice.web.dto.mapper.AssignmentMapper;
import com.gznznzjsn.orderservice.web.dto.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/car-service/users/{userId}/orders")
public class OrderController {

    private final AssignmentService assignmentService;
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final AssignmentMapper assignmentMapper;

    //    @PreAuthorize("@securityChecks.hasOrder(#userId,#orderId)")
    @PostMapping("/{orderId}/send")
    public Flux<AssignmentDto> sendWithAssignments(@PathVariable Long orderId, @PathVariable Long userId) {
        return Mono.just(orderId)
                .flatMapMany(assignmentService::sendWithOrder)
                .map(assignmentMapper::toDto);
    }

    //    @PreAuthorize("@securityChecks.hasUser(#userId)")
    @PostMapping
    public Mono<OrderDto> create(@Validated(OnCreateOrder.class) @RequestBody OrderDto orderDto, @PathVariable Long userId) {
        return Mono.just(orderDto)
                .map(orderMapper::toEntity)
                .map(order -> {
                    order.setUser(User.builder()
                            .id(userId)
                            .build());
                    return order;
                })
                .flatMap(orderService::create)
                .map(orderMapper::toDto);
    }

    //    @PreAuthorize("@securityChecks.hasOrder(#userId,#orderId)")
    @GetMapping("/{orderId}")
    public Mono<OrderDto> get(@PathVariable Long orderId, @PathVariable Long userId) {
        return orderService.get(orderId)
                .map(orderMapper::toDto);
    }

}
