package com.shinchik.cloudkeeper.user.mapper;

import com.shinchik.cloudkeeper.user.model.User;
import com.shinchik.cloudkeeper.user.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User mapToEntity(UserDto userDto);

}
