package com.example.demo.mapper;

import com.example.demo.dto.request.UpdateRequest;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.UserInfoResponse;
import com.example.demo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toUser(UserRequest request);
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserRequest request);
    UserInfoResponse toUserInfoResponse(User user);
    void updateMyInfo(@MappingTarget User user, UpdateRequest request);
}
