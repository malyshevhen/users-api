package com.github.malyshevhen.services.impl;

import static com.github.malyshevhen.testutils.FakeData.getValidAddress;
import static com.github.malyshevhen.testutils.FakeData.getValidUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.github.malyshevhen.exceptions.EntityAlreadyExistsException;
import com.github.malyshevhen.exceptions.EntityNotFoundException;
import com.github.malyshevhen.exceptions.UserValidationException;
import com.github.malyshevhen.models.User;
import com.github.malyshevhen.repositories.UserRepository;

import configs.UserConfiguration;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConfiguration userConfig;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = getValidUser();
    }

    @DisplayName("Test create user with valid data")
    @Test
    void testSave_ValidUser_ShouldSaveUser() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userConfig.getRequiredAge()).thenReturn(18);
        when(userRepository.save(user)).thenReturn(user);

        var savedUser = userService.save(user);

        assertNotNull(savedUser);
        verify(userRepository, times(1)).save(user);
    }

    @DisplayName("Test create user with existing email")
    @Test
    void testSave_ExistingEmail_ShouldThrowException() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> userService.save(user));
        verify(userRepository, never()).save(any());
    }

    @DisplayName("Test create user with invalid age throws exception")
    @Test
    void testSave_UnderAgeUser_ShouldThrowException() {
        when(userConfig.getRequiredAge()).thenReturn(100);

        assertThrows(UserValidationException.class, () -> userService.save(user));
        verify(userRepository, never()).save(any());
    }

    @DisplayName("Test get all users returns page of users")
    @Test
    void testGetAll_ShouldReturnPageOfUsers() {
        var pageable = PageRequest.of(0, 10);
        var userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        var result = userService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(user, result.getContent().get(0));
    }

    @DisplayName("Test get user by id returns user")
    @Test
    void testGetById_ExistingUser_ShouldReturnUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        var result = userService.getById(user.getId());

        assertNotNull(result);
        assertEquals(user, result);
    }

    @DisplayName("Test get user by id not found")
    @Test
    void testGetById_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.getById(user.getId()));
    }

    @DisplayName("Test update user with valid data")
    @Test
    void testUpdateById_ExistingUser_ShouldUpdateUser() {
        var updatedUser = getValidUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updatedUser.getEmail())).thenReturn(false);

        var result = userService.updateById(user.getId(), updatedUser);

        assertNotNull(result);
        assertEquals(updatedUser.getFirstName(), result.getFirstName());
        assertEquals(updatedUser.getLastName(), result.getLastName());
        assertEquals(updatedUser.getEmail(), result.getEmail());
        assertEquals(updatedUser.getAddress(), result.getAddress());
        assertEquals(updatedUser.getPhone(), result.getPhone());
    }

    @DisplayName("Test update user with existing email throws exception")
    @Test
    void testUpdateById_ExistingEmail_ShouldThrowException() {
        var updatedUser = getValidUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updatedUser.getEmail())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> userService.updateById(user.getId(), updatedUser));
    }

    @DisplayName("Test update email if no exception is thrown should pass")
    @Test
    void testUpdateEmail_ExistingUser_ShouldUpdateEmail() {
        var newEmail = "newemail@example.com";
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        var result = userService.updateEmail(user.getId(), newEmail);

        assertNotNull(result);
        assertEquals(newEmail, result.getEmail());
    }

    @DisplayName("Test update address if no exception is thrown should pass")
    @Test
    void testUpdateAddress_ExistingUser_ShouldUpdateAddress() {
        var newAddress = getValidAddress();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        var result = userService.updateAddress(user.getId(), newAddress);

        assertNotNull(result);
        assertEquals(newAddress, result.getAddress());
    }

    @DisplayName("Test delete user by id")
    @Test
    void testDeleteById_ExistingUser_ShouldDeleteUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.deleteById(user.getId());

        verify(userRepository, times(1)).delete(user);
    }

    @DisplayName("Test delete user by id not found")
    @Test
    void testDeleteById_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.deleteById(user.getId()));
        verify(userRepository, never()).delete(any());
    }
}
