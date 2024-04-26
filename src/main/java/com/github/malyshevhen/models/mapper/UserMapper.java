package com.github.malyshevhen.models.mapper;

import com.github.malyshevhen.dto.UserInfo;
import com.github.malyshevhen.dto.UserRegistrationForm;
import com.github.malyshevhen.dto.UserUpdateForm;
import com.github.malyshevhen.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUser(UserRegistrationForm registrationForm);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUser(UserUpdateForm updateForm);

    UserInfo toUserInfo(User user);
}
