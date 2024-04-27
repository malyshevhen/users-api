package com.github.malyshevhen.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.github.malyshevhen.api.UsersApi;
import com.github.malyshevhen.dto.UpdateEmailForm;
import com.github.malyshevhen.dto.UserInfo;
import com.github.malyshevhen.dto.UserRegistrationForm;
import com.github.malyshevhen.dto.UserUpdateForm;
import com.github.malyshevhen.models.Address;
import com.github.malyshevhen.models.DateRange;
import com.github.malyshevhen.models.mapper.UserMapper;
import com.github.malyshevhen.services.UserService;

import lombok.RequiredArgsConstructor;

/**
 * Provides a REST API for managing user-related operations, including
 * registration, retrieval, update, and deletion.
 * 
 * @author Evhen Malysh
 */
@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Registers a new user with the provided registration form data.
     *
     * @param userRegistrationForm the user registration form containing the
     *                             necessary data to create a new user
     * @return a ResponseEntity containing the newly registered user's information
     *         and HTTP status:
     *         Created - 201.
     *         Bad request - 400. Invalid {@code email} or {@code birthDate},
     *         or empty {@code firstName} or {@code lastName}
     *         Internal server error -500.
     */
    @Override
    public ResponseEntity<UserInfo> registerUser(UserRegistrationForm userRegistrationForm) {
        var userToRegister = userMapper.toUser(userRegistrationForm);
        var registeredUser = userService.save(userToRegister);
        var userInfo = userMapper.toUserInfo(registeredUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(userInfo);
    }

    /**
     * Retrieves a paginated list of user information.
     *
     * @param pageable  the pagination parameters. It is optional. If not provided,
     *                  default page size and 0-based index are used.
     * @param dateRange the date range to filter users by. It is optional. If not
     *                  provided, all users will be returned.
     * @return a page of user information and HTTP status:
     *         OK - 200.
     *         Bad request. Invalid {@code pageable} parameters or {@code dateRange}
     *         Internal server error -500.
     */
    @Override
    public ResponseEntity<Page<UserInfo>> getAll(Pageable pageable, DateRange dateRange) {
        var users = userService.getAll(pageable, dateRange)
                .map(userMapper::toUserInfo);
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user to retrieve
     * @return a ResponseEntity containing the user's information and HTTP status:
     *         OK - 200. The user was found and returned.
     *         Not Found - 404. No user was found with the given ID.
     *         Internal Server Error - 500. An error occurred while retrieving the
     *         user.
     */
    @Override
    public ResponseEntity<UserInfo> getById(Long id) {
        var user = userService.getById(id);
        var userInfo = userMapper.toUserInfo(user);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * Updates an existing user by their unique identifier.
     *
     * @param id             the unique identifier of the user to update
     * @param userUpdateForm the user update form containing the new data to update
     *                       the user with
     * @return a ResponseEntity containing the updated user's information and HTTP
     *         status:
     *         OK - 200. The user was successfully updated.
     *         Not Found - 404. No user was found with the given ID.
     *         Internal Server Error - 500. An error occurred while updating the
     *         user.
     */
    @Override
    public ResponseEntity<UserInfo> updateById(Long id, UserUpdateForm userUpdateForm) {
        var user = userMapper.toUser(userUpdateForm);
        var updatedUser = userService.updateById(id, user);
        var userInfo = userMapper.toUserInfo(updatedUser);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * Updates the email address of an existing user by their unique identifier.
     *
     * @param id              the unique identifier of the user to update
     * @param updateEmailForm the form containing the new email address to update
     *                        the user with
     * @return a ResponseEntity containing the updated user's information and HTTP
     *         status:
     *         OK - 200. The user's email was successfully updated.
     *         Not Found - 404. No user was found with the given ID.
     *         Internal Server Error - 500. An error occurred while updating the
     *         user's email.
     */
    @Override
    public ResponseEntity<UserInfo> updateUserEmail(Long id, UpdateEmailForm updateEmailForm) {
        var updatedUser = userService.updateEmail(id, updateEmailForm.getEmail());
        var userInfo = userMapper.toUserInfo(updatedUser);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * Updates the address of an existing user by their unique identifier.
     *
     * @param id      the unique identifier of the user to update
     * @param address the new address to update the user with
     * @return a ResponseEntity containing the updated user's information and HTTP
     *         status:
     *         OK - 200. The user's address was successfully updated.
     *         Not Found - 404. No user was found with the given ID.
     *         Internal Server Error - 500. An error occurred while updating the
     *         user's address.
     */
    @Override
    public ResponseEntity<UserInfo> updateUserAddress(Long id, Address address) {
        var updatedUser = userService.updateAddress(id, address);
        var userInfo = userMapper.toUserInfo(updatedUser);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id the unique identifier of the user to delete
     * @return a ResponseEntity with HTTP status:
     *         No Content - 204. The user was successfully deleted.
     *         Not Found - 404. No user was found with the given ID.
     *         Internal Server Error - 500. An error occurred while deleting the
     *         user.
     */
    @Override
    public ResponseEntity<Void> deleteById(Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
