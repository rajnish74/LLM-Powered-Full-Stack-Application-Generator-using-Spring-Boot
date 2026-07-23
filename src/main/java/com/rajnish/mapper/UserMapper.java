package com.rajnish.mapper;

import com.rajnish.dto.auth.request.SignupRequest;
import com.rajnish.dto.auth.response.UserProfileResponse;
import com.rajnish.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(SignupRequest signupRequest);

    UserProfileResponse toUserProfileResponse(User user);
}
