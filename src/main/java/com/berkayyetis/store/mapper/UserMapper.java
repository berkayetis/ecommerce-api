package com.berkayyetis.store.mapper;

import com.berkayyetis.store.dtos.UserDto;
import com.berkayyetis.store.dtos.UserRegisterRequestDto;
import com.berkayyetis.store.dtos.UserUpdateRequestDto;
import com.berkayyetis.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserRegisterRequestDto userRegisterRequestDto);
    void updateUser(UserUpdateRequestDto userUpdateRequestDto, @MappingTarget User user);
}
