package com.gznznzjsn.orderservice.web.dto.mapper;

import com.gznznzjsn.orderservice.domain.User;
import com.gznznzjsn.orderservice.web.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User entity);

    User toEntity(UserDto dto);

}
