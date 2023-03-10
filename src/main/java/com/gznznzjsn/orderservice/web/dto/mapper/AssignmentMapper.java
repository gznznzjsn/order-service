package com.gznznzjsn.orderservice.web.dto.mapper;


import com.gznznzjsn.orderservice.domain.Assignment;
import com.gznznzjsn.orderservice.web.dto.AssignmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class, TaskListMapper.class})
public interface AssignmentMapper {

    Assignment toEntity(AssignmentDto dto);

    @Mapping(target = "finalCost", expression = "java(com.gznznzjsn.orderservice.service.AssignmentService.calculateTotalCost(entity))")
    AssignmentDto toDto(Assignment entity);

}
