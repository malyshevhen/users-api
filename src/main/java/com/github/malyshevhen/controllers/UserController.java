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
import com.github.malyshevhen.models.mapper.UserMapper;
import com.github.malyshevhen.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<UserInfo> registerUser(UserRegistrationForm userRegistrationForm) {
        var userToRegister = userMapper.toUser(userRegistrationForm);
        var registeredUser = userService.save(userToRegister);
        var userInfo = userMapper.toUserInfo(registeredUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(userInfo);
    }

    @Override
    public ResponseEntity<Page<UserInfo>> getAll(Pageable pageable) {
        var users = userService.getAll(pageable)
                .map(userMapper::toUserInfo);
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<UserInfo> getById(Long id) {
        var user = userService.getById(id);
        var userInfo = userMapper.toUserInfo(user);
        return ResponseEntity.ok(userInfo);
    }

    @Override
    public ResponseEntity<UserInfo> updateById(Long id, UserUpdateForm userUpdateForm) {
        var user = userMapper.toUser(userUpdateForm);
        var updatedUser = userService.updateById(id, user);
        var userInfo = userMapper.toUserInfo(updatedUser);
        return ResponseEntity.ok(userInfo);
    }

    @Override
    public ResponseEntity<UserInfo> updateUserEmail(Long id, UpdateEmailForm updateEmailForm) {
        var updatedUser = userService.updateEmail(id, updateEmailForm.getEmail());
        var userInfo = userMapper.toUserInfo(updatedUser);
        return ResponseEntity.ok(userInfo);
    }

    @Override
    public ResponseEntity<UserInfo> updateUserAddress(Long id, Address address) {
        var updatedUser = userService.updateAddress(id, address);
        var userInfo = userMapper.toUserInfo(updatedUser);
        return ResponseEntity.ok(userInfo);
    }

    @Override
    public ResponseEntity<Void> deleteById(Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
