package com.gznznzjsn.orderservice.web.controller;


import com.gznznzjsn.orderservice.domain.Order;
import com.gznznzjsn.orderservice.domain.User;
import com.gznznzjsn.orderservice.service.AssignmentService;
import com.gznznzjsn.orderservice.web.dto.AssignmentDto;
import com.gznznzjsn.orderservice.web.dto.group.OnAccept;
import com.gznznzjsn.orderservice.web.dto.group.OnCreateAssignment;
import com.gznznzjsn.orderservice.web.dto.mapper.AssignmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/car-service")
public class AssignmentController {

    private final AssignmentMapper assignmentMapper;
    private final AssignmentService assignmentService;

    //    @PreAuthorize("@securityChecks.hasAssignment(#userId,#orderId,#assignmentId)")
    @GetMapping("users/{userId}/orders/{orderId}/assignments/{assignmentId}")
    public Mono<AssignmentDto> get(@PathVariable Long userId, @PathVariable Long orderId, @PathVariable Long assignmentId) {
        return assignmentService
                .get(assignmentId)
                .map(assignmentMapper::toDto);
    }

    //    @PreAuthorize("@securityChecks.hasOrder(#userId,#orderId)")
    @PostMapping("users/{userId}/orders/{orderId}/assignments")
    public Mono<AssignmentDto> create(@Validated(OnCreateAssignment.class) @RequestBody AssignmentDto assignmentDto, @PathVariable Long orderId, @PathVariable Long userId) {
        return Mono.just(assignmentDto)
                .map(assignmentMapper::toEntity)
                .map(assignment -> {
                    assignment.setOrder(Order.builder()
                            .id(orderId)
                            .user(User.builder()
                                    .id(userId)
                                    .build())
                            .build());
                    return assignment;
                })
                .flatMap(assignmentService::create)
                .map(assignmentMapper::toDto);
    }

    //    @PreAuthorize("hasAuthority('EMPLOYEE_MANAGER')")
    @PatchMapping("assignments/{assignmentId}")
    public Mono<AssignmentDto> accept(@Validated(OnAccept.class) @RequestBody AssignmentDto assignmentDto, @PathVariable Long assignmentId) {
        return Mono.just(assignmentDto)
                .map(assignmentMapper::toEntity)
                .map(assignment -> {
                            assignment.setId(assignmentId);
                            return assignment;
                        })
                .flatMap(assignmentService::accept)
                .map(assignmentMapper::toDto);
    }

}
