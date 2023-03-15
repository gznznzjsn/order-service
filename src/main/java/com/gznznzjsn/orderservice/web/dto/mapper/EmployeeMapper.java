package com.gznznzjsn.orderservice.web.dto.mapper;

import com.gznznzjsn.orderservice.domain.Employee;
import com.gznznzjsn.orderservice.web.dto.EmployeeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    Employee toEntity(EmployeeDto dto);

    EmployeeDto toDto(Employee entity);

}
