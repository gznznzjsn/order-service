package com.gznznzjsn.orderservice.web.dto.mapper;


import com.gznznzjsn.orderservice.domain.Assignment;
import com.gznznzjsn.orderservice.web.dto.AssignmentDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AssignmentMapper.class})
public interface AssignmentListMapper {

    List<Assignment> toEntity(List<AssignmentDto> dto);

    List<AssignmentDto> toDto(List<Assignment> sentAssignments);

}
