package com.github.malyshevhen.models.mapper;

import static com.github.malyshevhen.testutils.FakeData.getValidUser;
import static com.github.malyshevhen.testutils.FakeData.getValidUserRegistrationForm;
import static com.github.malyshevhen.testutils.FakeData.getValidUserUpdateForm;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.malyshevhen.dto.UserInfo;
import com.github.malyshevhen.models.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void testToUserFromRegistrationForm() {
        var registrationForm = getValidUserRegistrationForm();

        var user = userMapper.toUser(registrationForm);

        assertNotNull(user);
        assertEquals(registrationForm.getEmail(), user.getEmail());
        assertEquals(registrationForm.getFirstName(), user.getFirstName());
        assertEquals(registrationForm.getLastName(), user.getLastName());
        assertEquals(registrationForm.getBirthDate(), user.getBirthDate());
        assertEquals(registrationForm.getAddress(), user.getAddress());
        assertEquals(registrationForm.getPhone(), user.getPhone());

        assertNull(user.getId());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    @Test
    void testToUserFromUpdateForm() {
        var updateForm = getValidUserUpdateForm();

        var user = userMapper.toUser(updateForm);

        assertNotNull(user);
        assertEquals(updateForm.getEmail(), user.getEmail());
        assertEquals(updateForm.getFirstName(), user.getFirstName());
        assertEquals(updateForm.getLastName(), user.getLastName());
        assertEquals(updateForm.getBirthDate(), user.getBirthDate());
        assertEquals(updateForm.getAddress(), user.getAddress());
        assertEquals(updateForm.getPhone(), user.getPhone());

        assertNull(user.getId());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    @Test
    void testToUserInfo() {
        User user = getValidUser();
        user.setId(1L);

        UserInfo userInfo = userMapper.toUserInfo(user);

        assertNotNull(userInfo);
        assertEquals(1L, userInfo.getId());
        assertEquals(user.getEmail(), userInfo.getEmail());
        assertEquals(user.getFirstName(), userInfo.getFirstName());
        assertEquals(user.getLastName(), userInfo.getLastName());
        assertEquals(user.getBirthDate(), userInfo.getBirthDate());
        assertEquals(user.getAddress(), userInfo.getAddress());
        assertEquals(user.getPhone(), userInfo.getPhone());
    }
}
