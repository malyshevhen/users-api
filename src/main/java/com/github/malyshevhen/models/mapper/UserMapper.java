package com.github.malyshevhen.models.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.github.malyshevhen.dto.UserInfo;
import com.github.malyshevhen.dto.UserRegistrationForm;
import com.github.malyshevhen.dto.UserUpdateForm;
import com.github.malyshevhen.models.User;

/**
 * Mapper interface for converting between User entities and DTO objects.
 * </p>
 * Provides methods to map a UserRegistrationForm to a User entity, a
 * UserUpdateForm to a User entity, and a User entity to a UserInfo DTO.
 * The mapping ignores the id, createdAt, and updatedAt fields, as these are
 * typically managed by the application.
 * 
 * @author Evhen Malysh
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps a {@link UserUpdateForm} to a {@link User} entity, ignoring the
     * {@code id}, {@code createdAt}, and {@code updatedAt} properties.
     *
     * @param updateForm the {@link UserUpdateForm} to map from
     * @return the mapped {@link User} entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUser(UserRegistrationForm registrationForm);

    /**
     * Maps a {@link UserUpdateForm} to a {@link User} entity.
     * The {@code id}, {@code createdAt}, and {@code updatedAt} fields are ignored
     * during the mapping.
     *
     * @param updateForm the {@link UserUpdateForm} to map from
     * @return the mapped {@link User} entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUser(UserUpdateForm updateForm);

    /**
     * Converts a {@link User} entity to a {@link UserInfo} data transfer object.
     *
     * @param user the {@link User} entity to convert
     * @return the corresponding {@link UserInfo} data transfer object
     */
    UserInfo toUserInfo(User user);
}
