package com.github.malyshevhen.services.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.malyshevhen.exceptions.EntityAlreadyExistsException;
import com.github.malyshevhen.exceptions.EntityNotFoundException;
import com.github.malyshevhen.exceptions.UserValidationException;
import com.github.malyshevhen.models.Address;
import com.github.malyshevhen.models.User;
import com.github.malyshevhen.repositories.UserRepository;
import com.github.malyshevhen.services.UserService;

import configs.UserConfiguration;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConfiguration userConfig;


    @Transactional
    @Override
    public User save(User userToRegister) {
        assertThatAgeIsLegal(userToRegister);
        assertThatEmailNotExists(userToRegister.getEmail());

        return userRepository.save(userToRegister);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public User getById(Long id) {
        var errorMessage = String.format("User with id %d was not found", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));
    }

    @Transactional
    @Override
    public User updateById(Long id, User user) {
        var existingUser = getById(id);
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setAddress(user.getAddress());
        existingUser.setPhone(user.getPhone());

        var existingEmail = existingUser.getEmail();
        var emailToUpdate = user.getEmail();
        if (!Objects.equals(existingEmail, emailToUpdate)) {
            assertThatEmailNotExists(emailToUpdate);
            existingUser.setEmail(user.getEmail());
        }

        return existingUser;
    }

    @Transactional
    @Override
    public User updateEmail(Long id, String email) {
        var existingUser = getById(id);
        existingUser.setEmail(email);

        return existingUser;
    }

    @Transactional
    @Override
    public User updateAddress(Long id, Address address) {
        var existingUser = getById(id);
        existingUser.setAddress(address);

        return existingUser;
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        var existingUser = getById(id);
        userRepository.delete(existingUser);
    }

    private void assertThatEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EntityAlreadyExistsException("User with this email already registered");
        }
    }

    private void assertThatAgeIsLegal(User user) {
        int requiredAge = userConfig.getRequiredAge();
        long userAge = ChronoUnit.YEARS.between(user.getBirthDate(), LocalDate.now());
        if (userAge < requiredAge) {
            var message = String.format("Users age must be greater than or equal to %d", requiredAge);
            throw new UserValidationException(message);
        }
    }
}
