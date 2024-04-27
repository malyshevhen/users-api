package com.github.malyshevhen.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.github.malyshevhen.models.Address;
import com.github.malyshevhen.models.User;
import com.github.malyshevhen.exceptions.EntityAlreadyExistsException;
import com.github.malyshevhen.exceptions.EntityNotFoundException;
import com.github.malyshevhen.exceptions.UserValidationException;
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
    private Address address;

    @BeforeEach
    void setUp() {
        address = Address.builder()
                .street("123 Main St")
                .city("Anytown")
                .country("CA")
                .number("12345")
                .build();
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .phone("555-1234")
                .build();

    }

    @Test
    void testSave_ValidUser_ShouldSaveUser() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userConfig.getRequiredAge()).thenReturn(18);
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.save(user);

        assertNotNull(savedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSave_ExistingEmail_ShouldThrowException() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> userService.save(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSave_UnderAgeUser_ShouldThrowException() {
        when(userConfig.getRequiredAge()).thenReturn(100);

        assertThrows(UserValidationException.class, () -> userService.save(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetAll_ShouldReturnPageOfUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<User> result = userService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(user, result.getContent().get(0));
    }

    @Test
    void testGetById_ExistingUser_ShouldReturnUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.getById(user.getId());

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void testGetById_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getById(user.getId()));
    }

    @Test
    void testUpdateById_ExistingUser_ShouldUpdateUser() {
        User updatedUser = new User();
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setEmail("jane@example.com");
        updatedUser.setAddress(Address.builder()
                .country("UA")
                .city("Kyiv")
                .street("Bohdana Khmelnytskogo")
                .number("11-B")
                .build());
        updatedUser.setPhone("987-6543");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updatedUser.getEmail())).thenReturn(false);

        User result = userService.updateById(user.getId(), updatedUser);

        assertNotNull(result);
        assertEquals(updatedUser.getFirstName(), result.getFirstName());
        assertEquals(updatedUser.getLastName(), result.getLastName());
        assertEquals(updatedUser.getEmail(), result.getEmail());
        assertEquals(updatedUser.getAddress(), result.getAddress());
        assertEquals(updatedUser.getPhone(), result.getPhone());
    }

    @Test
    void testUpdateById_ExistingEmail_ShouldThrowException() {
        User updatedUser = new User();
        updatedUser.setEmail("existing@example.com");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updatedUser.getEmail())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> userService.updateById(user.getId(), updatedUser));
    }

    @Test
    void testUpdateEmail_ExistingUser_ShouldUpdateEmail() {
        String newEmail = "newemail@example.com";
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.updateEmail(user.getId(), newEmail);

        assertNotNull(result);
        assertEquals(newEmail, result.getEmail());
    }

    @Test
    void testUpdateAddress_ExistingUser_ShouldUpdateAddress() {
        Address newAddress = Address.builder()
                .country("UA")
                .city("Kyiv")
                .street("Bohdana Khmelnytskogo")
                .number("11-B")
                .build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.updateAddress(user.getId(), newAddress);

        assertNotNull(result);
        assertEquals(newAddress, result.getAddress());
    }

    @Test
    void testDeleteById_ExistingUser_ShouldDeleteUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.deleteById(user.getId());

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteById_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deleteById(user.getId()));
        verify(userRepository, never()).delete(any());
    }
}
