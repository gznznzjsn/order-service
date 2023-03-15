package com.gznznzjsn.orderservice.web.dto.mapper;


import com.gznznzjsn.orderservice.domain.Period;
import com.gznznzjsn.orderservice.web.dto.PeriodDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class})
public interface PeriodMapper {

    Period toEntity(PeriodDto dto);

    PeriodDto toDto(Period entity);

}
