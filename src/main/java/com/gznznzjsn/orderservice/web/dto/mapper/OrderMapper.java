package com.gznznzjsn.orderservice.web.dto.mapper;


import com.gznznzjsn.orderservice.domain.Order;
import com.gznznzjsn.orderservice.web.dto.OrderDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface OrderMapper {

    Order toEntity(OrderDto dto);

    OrderDto toDto(Order entity);

}
