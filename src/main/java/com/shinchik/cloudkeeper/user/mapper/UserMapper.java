package com.shinchik.cloudkeeper.user.mapper;

import com.shinchik.cloudkeeper.user.model.User;
import com.shinchik.cloudkeeper.user.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User mapToEntity(UserDto userDto);

}
