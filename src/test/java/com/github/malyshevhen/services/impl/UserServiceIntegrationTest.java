package com.github.malyshevhen.services.impl;

import static com.github.malyshevhen.testutils.FakeData.getValidAddress;
import static com.github.malyshevhen.testutils.FakeData.getValidEmail;
import static com.github.malyshevhen.testutils.FakeData.getValidUser;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.malyshevhen.configs.TestApplicationConfig;
import com.github.malyshevhen.exceptions.EntityAlreadyExistsException;
import com.github.malyshevhen.exceptions.EntityNotFoundException;
import com.github.malyshevhen.exceptions.UserValidationException;
import com.github.malyshevhen.models.Address;
import com.github.malyshevhen.models.DateRange;
import com.github.malyshevhen.models.User;
import com.github.malyshevhen.repositories.UserRepository;
import com.github.malyshevhen.services.UserService;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import({ TestApplicationConfig.class })
@Testcontainers
@Transactional
@DirtiesContext
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("UserService should save new user")
    @Test
    public void testSaveUser_whenNewUserIsSaved_thenReturnUser() {
        // Prepare:
        var validUser = getValidUser();

        // Execute:
        var savedUser = userService.save(validUser);

        // Verify:
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(validUser.getEmail(), savedUser.getEmail());
        assertEquals(validUser.getFirstName(), savedUser.getFirstName());
        assertEquals(validUser.getLastName(), savedUser.getLastName());
        assertEquals(validUser.getBirthDate(), savedUser.getBirthDate());
    }

    @DisplayName("Save user with existing email should throw exception")
    @Test
    public void testSaveUser_whenEmailIsExist_thenThrowException() {
        // Prepare:
        var validUser = getValidUser();
        userService.save(validUser);

        // Execute:
        var exception = assertThrows(EntityAlreadyExistsException.class,
                () -> userService.save(validUser));

        // Verify:
        assertEquals("User with this email already registered", exception.getMessage());
    }

    @DisplayName("Save user under 18 should throw exception")
    @Test
    public void testSaveUser_whenAgeIsUnderMinimum_thenThrowException() {
        // Prepare:
        var invalidUser = getValidUser();
        invalidUser.setBirthDate(java.time.LocalDate.of(2010, 1, 1));

        // Execute:
        var exception = assertThrows(UserValidationException.class,
                () -> userService.save(invalidUser));

        // Verify:
        assertEquals("Users age must be greater than or equal to 18", exception.getMessage());
    }

    @DisplayName("Get all with 0 users in DB should return empty list")
    @Test
    public void testFindAll_whenDBIsEmpty_thenReturnEmptyList() {
        // Execute:
        var pageable = PageRequest.of(0, 10);
        var dateRange = new DateRange(LocalDate.of(1990, 1, 1), LocalDate.of(2001, 1, 1));
        var users = userService.getAll(pageable, dateRange);

        // Verify:
        assertEquals(0, users.getTotalElements());
        assertNotNull(users.getContent());
        assertNotNull(users.getSize());
        assertNotNull(users.getNumber());
    }

    @DisplayName("Get all with non empty DB should return page of users")
    @Test
    public void testFindAll_whenDBHasOneUser_thenReturnThisUser() {
        // Prepare:
        var validUser = getValidUser();
        var savedUser = userService.save(validUser);
        var pageable = PageRequest.of(0, 10);

        // Execute:
        var users = userService.getAll(pageable, null);

        // Verify:
        assertEquals(1, users.getTotalElements());
        assertNotNull(users.getContent());
        assertNotNull(users.getSize());
        assertNotNull(users.getNumber());
        assertEquals(savedUser, users.getContent().get(0));
    }

    @DisplayName("Get all with valid range should return page of users with birth date in this range")
    @ParameterizedTest(name = "{index}: Get all with valid range {arguments}")
    @ValueSource(strings = { "1983-01-01,2000-12-29", "1985-01-01,2010-12-29", "1983-01-01,1990-01-01",
            "1985-01-01,1990-01-01" })
    public void testFindAll_whenDBHasUsersWithBirthDateInRange_thenReturnTheseUsers(String dateRange) {
        // Prepare:
        var validUser1 = getValidUser();
        validUser1.setBirthDate(LocalDate.of(1985, 1, 1));
        var savedUser1 = userService.save(validUser1);

        var validUser2 = getValidUser();
        validUser2.setBirthDate(LocalDate.of(1990, 1, 1));
        var savedUser2 = userService.save(validUser2);

        var validUser3 = getValidUser();
        validUser3.setBirthDate(LocalDate.of(1950, 12, 29));
        var savedUser3 = userService.save(validUser3);

        var pageable = PageRequest.of(0, 10);

        // Execute:
        var users = userService.getAll(pageable,
                new DateRange(LocalDate.parse(dateRange.split(",")[0]), LocalDate.parse(dateRange.split(",")[1])));
        System.out.println("USERS: " + users.getContent());

        // Verify:
        assertEquals(2, users.getTotalElements());
        assertTrue(users.hasContent());
        assertTrue(users.getContent().stream().anyMatch(user -> user.equals(savedUser1)));
        assertTrue(users.getContent().stream().anyMatch(user -> user.equals(savedUser2)));
        assertFalse(users.getContent().stream().anyMatch(user -> user.equals(savedUser3)));
    }

    @DisplayName("Get all with invalid range should throw an exception and do not call the repository method")
    @ParameterizedTest(name = "{index}: Get all with invalid range {arguments}")
    @ValueSource(strings = { "2019-01-01,1983-01-01", "1985-01-01,1983-01-01" })
    public void testFindAll_whenDBHasUsersAndRangeIsInvalid_thenThrowException(String dateRange) {
        // Prepare:
        var validUser = getValidUser();
        userService.save(validUser);

        var pageable = PageRequest.of(0, 10);
        var from = LocalDate.parse(dateRange.split(",")[0]);
        var to = LocalDate.parse(dateRange.split(",")[1]);

        // Execute:
        var exception = assertThrows(IllegalArgumentException.class,
                () -> userService.getAll(pageable, new DateRange(from, to)));

        // Verify:
        assertEquals("From date must be before to date", exception.getMessage());
        assertFalse(userRepository.findAll().isEmpty());
        assertFalse(userService.getAll(PageRequest.of(0, 10), null).isEmpty());
    }

    @DisplayName("Get user by id should return existing user")
    @Test
    public void testFindById_whenIdIsValid_thenReturnUser() {
        // Prepare:
        var validUser = getValidUser();
        var savedUser = userService.save(validUser);

        // Execute:
        var foundUser = userService.getById(savedUser.getId());

        // Verify:
        assertNotNull(foundUser);
        assertEquals(savedUser, foundUser);
    }

    @DisplayName("Get user by id should throw an exception when id is invalid")
    @Test
    public void testFindById_whenIdIsInvalid_thenThrowException() {
        // Execute:
        var exception = assertThrows(EntityNotFoundException.class,
                () -> userService.getById(-1L));

        // Verify:
        assertEquals("User with id -1 was not found", exception.getMessage());
    }

    @DisplayName("Update by id should return updated user")
    @Test
    public void testUpdateById_whenIdIsValid_thenReturnUpdatedUser() {
        // Prepare:
        var validUser = getValidUser();

        var userForUpdate = getValidUser();
        var savedUser = userService.save(validUser);

        // Update:
        userService.updateById(savedUser.getId(), userForUpdate);
        var foundUser = userService.getById(savedUser.getId());

        // Verify:
        assertNotNull(foundUser);
        assertNotNull(foundUser.getId());
        assertEquals(userForUpdate.getBirthDate(), foundUser.getBirthDate());
        assertEquals(userForUpdate.getFirstName(), foundUser.getFirstName());
        assertEquals(userForUpdate.getLastName(), foundUser.getLastName());
        assertEquals(userForUpdate.getEmail(), foundUser.getEmail());
        assertEquals(userForUpdate.getAddress(), foundUser.getAddress());
        assertEquals(userForUpdate.getPhone(), foundUser.getPhone());
    }

    @DisplayName("Update user with taken email should throw exception")
    @Test
    public void testUpdateById_whenEmailIsTaken_thenThrowException() {
        // Prepare:
        var savedUserToUpdate = userService.save(getValidUser());
        var savedUser2 = userService.save(getValidUser());

        var userForUpdate = User.builder()
                .email(savedUser2.getEmail())
                .firstName("Jack")
                .lastName("Black")
                .birthDate(java.time.LocalDate.of(1965, 1, 1))
                .address(Address.builder()
                        .city("New City")
                        .country("New Country")
                        .street("New Street")
                        .number("123-NEW")
                        .build())
                .phone("+380987654321")
                .build();

        // Update:
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.updateById(savedUserToUpdate.getId(), userForUpdate));

        // Verify:
        assertEquals("User with this email already registered", exception.getMessage());
    }

    @DisplayName("Delete by id should delete user from DB")
    @Test
    public void testDeleteById_whenIdIsValid_thenDeleteUser() {
        // Prepare:
        var validUser = getValidUser();
        var savedUser = userService.save(validUser);

        // Delete:
        userService.deleteById(savedUser.getId());

        // Verify:
        Optional<User> deletedUserOptional = userRepository.findById(savedUser.getId());
        assertFalse(deletedUserOptional.isPresent());
    }

    @DisplayName("Delete by id should throw exception when id is invalid")
    @ParameterizedTest(name = "{index}: Delete by id {0} should throw an exception")
    @ValueSource(longs = { -1, 0, 1, 100 })
    public void testDeleteById_whenIdIsInvalid_thenThrowException(Long id) {
        // Execute:
        var exception = assertThrows(EntityNotFoundException.class,
                () -> userService.deleteById(id));

        // Verify:
        var message = String.format("User with id %d was not found", id);
        assertEquals(message, exception.getMessage());
    }

    @DisplayName("Update email should update email in DB")
    @Test
    public void testUpdateEmail_whenIdIsValid_thenUpdateEmail() {
        // Prepare:
        var validUser = getValidUser();
        var savedUser = userService.save(validUser);

        var newEmail = getValidEmail();

        // Update:
        userService.updateEmail(savedUser.getId(), newEmail);

        // Verify:
        var updatedUserOptional = userRepository.findById(savedUser.getId());
        var updatedUser = updatedUserOptional.get();
        assertEquals(newEmail, updatedUser.getEmail());
    }

    @DisplayName("Update email should throw exception when id is invalid")
    @ParameterizedTest(name = "{index}: Update email should throw an exception when id is: {0}")
    @ValueSource(longs = { -1, 0, 1, 100 })
    public void testUpdateEmail_whenIdIsInvalid_thenThrowException(Long id) {
        // Execute:
        var exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateEmail(id, getValidEmail()));

        // Verify:
        assertEquals("User with id " + id + " was not found", exception.getMessage());
    }

    @DisplayName("Update email should throw exception when new email is already registered")
    @Test
    public void testUpdateEmail_whenNewEmailIsTaken_thenThrowException() {
        // Prepare:
        var savedUser1 = userService.save(getValidUser());
        var savedUser2 = userService.save(getValidUser());

        // Update:
        Exception exception = assertThrows(EntityAlreadyExistsException.class,
                () -> userService.updateEmail(savedUser1.getId(), savedUser2.getEmail()));

        // Verify:
        assertEquals("User with this email already registered", exception.getMessage());
    }

    @DisplayName("Update address should update address in DB")
    @Test
    public void testUpdateAddress_whenIdIsValid_thenUpdateAddress() {
        // Prepare:
        var validUser = getValidUser();
        var savedUser = userService.save(validUser);

        var newAddress = getValidAddress();

        // Update:
        userService.updateAddress(savedUser.getId(), newAddress);

        // Verify:
        var updatedUserOptional = userRepository.findById(savedUser.getId());
        var updatedUser = updatedUserOptional.get();
        assertEquals(newAddress, updatedUser.getAddress());
    }

}
