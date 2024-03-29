package com.gznznzjsn.orderservice.web.dto.mapper;


import com.gznznzjsn.orderservice.domain.Task;
import com.gznznzjsn.orderservice.web.dto.TaskDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TaskMapper.class})
public interface TaskListMapper {

    List<TaskDto> toDto(List<Task> entity);

    List<Task> toEntity(List<TaskDto> dto);

}
