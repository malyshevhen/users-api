package com.github.malyshevhen.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.malyshevhen.dto.UpdateEmailForm;
import com.github.malyshevhen.dto.UserInfo;
import com.github.malyshevhen.dto.UserRegistrationForm;
import com.github.malyshevhen.exceptions.ErrorResponse;
import com.github.malyshevhen.models.Address;
import com.github.malyshevhen.models.DateRange;
import com.github.malyshevhen.models.User;
import com.github.malyshevhen.models.mapper.UserMapper;
import com.github.malyshevhen.models.mapper.UserMapperImpl;
import com.github.malyshevhen.services.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static com.github.malyshevhen.testutils.FakeData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ActiveProfiles("test")
@Import({UserMapperImpl.class})
@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String USERS_URL = "/users";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @MockBean
    private UserService userService;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @DisplayName("register users should return 201 if required fields is valid")
    @Test
    @SneakyThrows
    void registerUserCreated() {
        // Given:
        var registerForm = getValidUserRegistrationForm();

        var savedUser = userMapper.toUser(registerForm);
        savedUser.setId(1L);
        savedUser.setCreatedAt(LocalDateTime.now());

        when(userService.save(any(User.class))).thenReturn(savedUser);

        // Execute:
        var response = mvc.perform(post(USERS_URL)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(registerForm)))
                           .andReturn()
                           .getResponse();

        // Verify:
        assertEquals(201, response.getStatus());

        // Execute:
        var responseBody = response.getContentAsString();
        var createdUser = objectMapper.readValue(responseBody, User.class);

        // Verify:
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals(registerForm.getEmail(), createdUser.getEmail());
        assertEquals(registerForm.getFirstName(), createdUser.getFirstName());
        assertEquals(registerForm.getLastName(), createdUser.getLastName());
        assertEquals(registerForm.getBirthDate(), createdUser.getBirthDate());
    }

    @DisplayName("register users should return 400 if form is invalid")
    @ParameterizedTest(name = "{index}: register user with email: {0}, firstName: {1}, lastName: {2}")
    @MethodSource
    @SneakyThrows
    void registerUserBadRequest(String email, String firstName, String lastName) {
        // Given:
        var registerForm = new UserRegistrationForm()
                               .email(email)
                               .firstName(firstName)
                               .lastName(lastName)
                               .birthDate(LocalDate.of(1990, 1, 1));

        // Execute:
        var response = mvc.perform(post(USERS_URL)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(registerForm)))
                           .andReturn()
                           .getResponse();

        // Verify:
        assertEquals(400, response.getStatus());

        // Execute:
        var responseBody = response.getContentAsString();
        var errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);

        // Verify:
        assertNotNull(errorResponse);
        assertTrue(errorResponse.message().contains("Validation failed"));
    }

    private static Stream<Arguments> registerUserBadRequest() {
        return Stream.of(
            // Invalid email:
            Arguments.of("@invalid.com", "John", "Doe", LocalDate.now().minusYears(20)),
            Arguments.of("invalid@email", "John", "Doe", LocalDate.now().minusYears(20)),
            Arguments.of("invalid.email.com", "John", "Doe", LocalDate.now().minusYears(20)),
            Arguments.of("", "John", "Doe", LocalDate.now().minusYears(20)),
            // Empty first name:
            Arguments.of("valid@email.com", "", "Doe", LocalDate.now().minusYears(20)),
            // Empty last name:
            Arguments.of("valid@email.com", "John", "", LocalDate.now().minusYears(20)));
    }

    @DisplayName("get all users should return 200 and list of users")
    @Test
    @SneakyThrows
    void getAllUsers() {
        // Given:
        var user1 = getValidUser();
        var user2 = getValidUser();

        var userPage = new PageImpl<>(List.of(user1, user2));

        when(userService.getAll(any(Pageable.class), any(DateRange.class))).thenReturn(userPage);

        // Execute:
        var response = mvc.perform(get(USERS_URL)
                                       .contentType(MediaType.APPLICATION_JSON))
                           .andReturn()
                           .getResponse();

        // Verify:
        assertEquals(200, response.getStatus());

        // Execute:
        var responseBody = response.getContentAsString();

        // Verify:
        assertNotNull(responseBody);
    }

    @DisplayName("get all users should return 400 if date range is invalid")
    @ParameterizedTest(name = "{index}: get all users in date range: {0}")
    @ValueSource(strings = {"?from=2020-01-01&to=1990-10-10", "?from=0000-00-00&to=0000-00-00", "?from=0&to=10",
        "?from=INVALID&to=INVALID"})
    @SneakyThrows
    void getAllUsers_BadRequest(String dateRange) {
        // Execute:
        var response = mvc.perform(get(USERS_URL + dateRange)
                                       .contentType(MediaType.APPLICATION_JSON))
                           .andReturn()
                           .getResponse();

        assertEquals(400, response.getStatus());

    }

    @DisplayName("Get all with invalid pagination should use default pagination")
    @ParameterizedTest(name = "{index}: Get All with pagination: {0}")
    @ValueSource(strings = {"?size=INVALID&page=INVALID", "?page=-10&size=INVALID", "?page=INVALID&size=-10",
        "?page=-100&size=-20"})
    @SneakyThrows
    void getAllUsers_withInvalidPagination_useDefaultPagination(String pagination) {
        // Prepare:
        var user1 = getValidUser();
        var user2 = getValidUser();

        var userPage = new PageImpl<>(List.of(user1, user2));

        var defaultPageable = PageRequest.of(0, 20);

        when(userService.getAll(eq(defaultPageable), any(DateRange.class))).thenReturn(userPage);

        // Execute:
        var response = mvc.perform(get(USERS_URL + pagination)
                                       .contentType(MediaType.APPLICATION_JSON))
                           .andReturn()
                           .getResponse();

        assertEquals(200, response.getStatus());

        verify(userService).getAll(pageableCaptor.capture(), any());
        var pageable = pageableCaptor.getValue();

        assertEquals(20, pageable.getPageSize());
        assertEquals(0, pageable.getPageNumber());
    }

    @DisplayName("get user by id should return 200 and user info")
    @Test
    @SneakyThrows
    void getUserById() {
        // Given:
        var user = getValidUser();

        when(userService.getById(1L)).thenReturn(user);

        // Execute:
        var response = mvc.perform(get(USERS_URL + "/1")
                                       .contentType(MediaType.APPLICATION_JSON))
                           .andReturn()
                           .getResponse();

        // Verify:
        assertEquals(200, response.getStatus());

        // Execute:
        var responseBody = response.getContentAsString();
        var userInfo = objectMapper.readValue(responseBody, UserInfo.class);

        // Verify:
        assertNotNull(userInfo);
        assertEquals(user.getId(), userInfo.getId());
        assertEquals(user.getEmail(), userInfo.getEmail());
        assertEquals(user.getFirstName(), userInfo.getFirstName());
        assertEquals(user.getLastName(), userInfo.getLastName());
        assertEquals(user.getBirthDate(), userInfo.getBirthDate());
    }

    @DisplayName("update user by id should return 200 and updated user info")
    @Test
    @SneakyThrows
    void updateUserById() {
        // Given:
        var updateForm = getValidUserUpdateForm();

        var updatedUser = userMapper.toUser(updateForm);
        updatedUser.setId(1L);

        when(userService.updateById(anyLong(), any(User.class))).thenReturn(updatedUser);

        // Execute:
        var response = mvc.perform(put(USERS_URL + "/1")
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(updateForm)))
                           .andReturn()
                           .getResponse();

        // Verify:
        assertEquals(200, response.getStatus());

        var responseBody = response.getContentAsString();
        var userInfo = objectMapper.readValue(responseBody, UserInfo.class);

        assertNotNull(userInfo);
        assertEquals(updatedUser.getId(), userInfo.getId());
        assertEquals(updatedUser.getEmail(), userInfo.getEmail());
        assertEquals(updatedUser.getFirstName(), userInfo.getFirstName());
        assertEquals(updatedUser.getLastName(), userInfo.getLastName());
        assertEquals(updatedUser.getBirthDate(), userInfo.getBirthDate());
    }

    @DisplayName("delete user by id should return 204")
    @Test
    @SneakyThrows
    void deleteUserById() {
        // Execute:
        var response = mvc.perform(delete(USERS_URL + "/1"))
                           .andReturn()
                           .getResponse();

        // Verify:
        assertEquals(204, response.getStatus());
    }

    @DisplayName("update user email should return 200 and updated user info")
    @Test
    @SneakyThrows
    void updateUserEmail() {
        // Given:
        var id = 1L;
        var email = getValidEmail();
        var updateEmailForm = new UpdateEmailForm().email(email);

        var updatedUser = getValidUser();
        updatedUser.setEmail(email);

        when(userService.updateEmail(id, email)).thenReturn(updatedUser);

        // Execute:
        var response = mvc.perform(patch(USERS_URL + "/{id}/email", id)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(updateEmailForm)))
                           .andReturn()
                           .getResponse();

        // Verify:
        assertEquals(200, response.getStatus());

        var responseBody = response.getContentAsString();
        var userInfo = objectMapper.readValue(responseBody, UserInfo.class);

        assertNotNull(userInfo);
        assertEquals(updatedUser.getId(), userInfo.getId());
        assertEquals(updatedUser.getEmail(), userInfo.getEmail());
        assertEquals(updatedUser.getFirstName(), userInfo.getFirstName());
        assertEquals(updatedUser.getLastName(), userInfo.getLastName());
        assertEquals(updatedUser.getBirthDate(), userInfo.getBirthDate());
    }

    @DisplayName("update user email should return 400 if email is invalid")
    @ParameterizedTest(name = "{index}: update user email with invalid email {0}")
    @MethodSource
    @SneakyThrows
    void updateUserEmailBadRequest(String email) {
        // Given:
        var id = 1L;
        var updateEmailForm = new UpdateEmailForm().email(email);

        // Execute:
        var response = mvc.perform(patch(USERS_URL + "/{id}/email", id)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(updateEmailForm)))
                           .andReturn()
                           .getResponse();

        // Verify:
        assertEquals(400, response.getStatus());

        // Execute:
        var responseBody = response.getContentAsString();
        var errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);

        // Verify:
        assertNotNull(errorResponse);
        assertTrue(errorResponse.message().contains("Validation failed"));
    }

    private static Stream<Arguments> updateUserEmailBadRequest() {
        return Stream.of(
            Arguments.of("@invalid.com"),
            Arguments.of("invalid@email"),
            Arguments.of("invalid.email.com"),
            Arguments.of(""));
    }

    @DisplayName("update user address should return 200 and updated user info")
    @Test
    @SneakyThrows
    void updateUserAddress() {
        // Given:
        var id = 1L;
        var address = getValidAddress();

        var updatedUser = getValidUser();
        updatedUser.setAddress(address);

        when(userService.updateAddress(anyLong(), any(Address.class))).thenReturn(updatedUser);

        // Execute:
        var response = mvc.perform(patch(USERS_URL + "/{id}/address", id)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(address)))
                           .andReturn()
                           .getResponse();

        // Verify:
        assertEquals(200, response.getStatus());

        var responseBody = response.getContentAsString();
        var userInfo = objectMapper.readValue(responseBody, UserInfo.class);

        assertNotNull(userInfo);
        assertEquals(updatedUser.getId(), userInfo.getId());
        assertEquals(updatedUser.getEmail(), userInfo.getEmail());
        assertEquals(updatedUser.getFirstName(), userInfo.getFirstName());
        assertEquals(updatedUser.getLastName(), userInfo.getLastName());
        assertEquals(updatedUser.getBirthDate(), userInfo.getBirthDate());
        assertEquals(address.getStreet(), userInfo.getAddress().getStreet());
        assertEquals(address.getCity(), userInfo.getAddress().getCity());
        assertEquals(address.getCountry(), userInfo.getAddress().getCountry());
        assertEquals(address.getNumber(), userInfo.getAddress().getNumber());
    }

    @DisplayName("delete user address should return 204")
    @Test
    @SneakyThrows
    void deleteUserAddress() {
        // Given:
        var id = 1L;

        // Execute:
        var response = mvc.perform(delete(USERS_URL + "/{id}/address", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // Verify:
        assertEquals(204, response.getStatus());
    }

}