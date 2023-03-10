package com.gznznzjsn.orderservice.web.dto.mapper;


import com.gznznzjsn.orderservice.domain.Task;
import com.gznznzjsn.orderservice.web.dto.TaskDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toEntity(TaskDto dto);

    TaskDto toDto(Task entity);

}
