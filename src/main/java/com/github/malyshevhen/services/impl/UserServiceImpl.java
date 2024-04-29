package com.github.malyshevhen.services.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import com.github.malyshevhen.domain.dto.DateRange;
import com.github.malyshevhen.configs.UserConstraints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.malyshevhen.exceptions.EntityAlreadyExistsException;
import com.github.malyshevhen.exceptions.EntityNotFoundException;
import com.github.malyshevhen.exceptions.UserValidationException;
import com.github.malyshevhen.domain.models.Address;
import com.github.malyshevhen.domain.models.User;
import com.github.malyshevhen.repositories.UserRepository;
import com.github.malyshevhen.services.UserService;

import com.github.malyshevhen.configs.ApplicationConfig;
import lombok.RequiredArgsConstructor;

/**
 * Provides an implementation of the {@link UserService} interface, handling
 * user-related operations such as saving, retrieving, updating, and deleting
 * users.
 * <p>
 * This service implementation uses a {@link UserRepository} to interact with
 * the underlying data storage and a {@link ApplicationConfig} to access
 * application-specific configuration.
 * <p>
 * The service methods perform various validation checks, such as ensuring the
 * user's age is legal and the email is not already taken, before performing the
 * requested operations.
 * Also operations are performed in a transactional way.
 *
 * @author Evhen Malysh
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConstraints userConstraints;

    /**
     * Saves a new user to the system.
     *
     * @param userToRegister the user to be registered
     * @return the saved user
     * @throws EntityAlreadyExistsException if email is already taken
     *                                      in the database
     * @throws UserValidationException      if age validation fails
     */
    @Transactional
    @Override
    public User save(User userToRegister) {
        assertThatAgeIsLegal(userToRegister);
        assertThatEmailNotTaken(userToRegister.getEmail());

        return userRepository.save(userToRegister);
    }

    /**
     * Retrieves a paginated list of all users.
     * </p>
     * If {@code pageable} is null, using default values for sorting and paging.
     * If 0 or negative number passed as page size, then it will be set to 10.
     * If the result is empty, returns an empty page.
     *
     * @param pageable  the pagination parameters
     * @param dateRange date range filter for filtering users by age
     * @return a page of users
     */
    @Transactional(readOnly = true)
    @Override
    public Page<User> getAll(Pageable pageable, DateRange dateRange) {
        return userRepository.findAll(inRange(dateRange), pageable);
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user to retrieve
     * @return the user with the specified ID
     * @throws EntityNotFoundException if no user is found with the specified ID
     */
    @Transactional(readOnly = true)
    @Override
    public User getById(Long id) {
        var errorMessage = String.format("User with id %d was not found", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));
    }

    /**
     * Updates an existing user with the provided user data.
     * </p>
     * This method should not be used. Instead, use {@link #updateEmail} and
     * {@link #updateAddress} methods separately.
     * <p>
     * This method is kept for test assignment purposes only.
     *
     * @param id   the ID of the user to update
     * @param user the updated user data
     * @return the updated user
     * @throws EntityNotFoundException      if no user found with the specified ID
     * @throws EntityAlreadyExistsException if the new email is already registered
     * @throws UserValidationException      if the user's age is below the required
     *                                      age
     */
    @Transactional
    @Override
    public User updateById(Long id, User user) {
        var existingUser = getById(id);
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setAddress(user.getAddress());
        existingUser.setPhone(user.getPhone());

        var existingBirthDate = existingUser.getBirthDate();
        var birthDateToUpdate = user.getBirthDate();
        if (!Objects.equals(existingBirthDate, birthDateToUpdate)) {
            assertThatAgeIsLegal(user);
            existingUser.setBirthDate(user.getBirthDate());
        }

        var existingEmail = existingUser.getEmail();
        var emailToUpdate = user.getEmail();
        if (!Objects.equals(existingEmail, emailToUpdate)) {
            assertThatEmailNotTaken(emailToUpdate);
            existingUser.setEmail(user.getEmail());
        }

        return existingUser;
    }

    /**
     * Updates the email of an existing user.
     *
     * @param id    The ID of the user to update.
     * @param email The new email address to set for the user.
     * @return The updated user entity.
     * @throws EntityAlreadyExistsException if the new email is already registered
     *                                      to another user.
     */
    @Transactional
    @Override
    public User updateEmail(Long id, String email) {
        assertThatEmailNotTaken(email);

        var existingUser = getById(id);
        existingUser.setEmail(email);
        return existingUser;
    }

    /**
     * Updates the address of the user with the specified ID.
     *
     * @param id      the ID of the user whose address should be updated
     * @param address the new address to set for the user
     * @return the updated user with the new address
     * @throws EntityNotFoundException if no user is found with the specified ID
     */
    @Transactional
    @Override
    public User updateAddress(Long id, Address address) {
        var existingUser = getById(id);
        existingUser.setAddress(address);
        return existingUser;
    }

    /**
     * Deletes the users address by the specified user ID.
     *
     * @param id the ID of the user
     * @throws EntityNotFoundException if no user is found with the specified ID
     */
    @Transactional
    @Override
    public void deleteUsersAddress(Long id) {
        var existingUser = getById(id);
        existingUser.setAddress(null);
    }

    /**
     * Deletes the user with the specified ID.
     *
     * @param id the ID of the user to delete
     * @throws EntityNotFoundException if no user is found with the specified ID
     */
    @Transactional
    @Override
    public void deleteById(Long id) {
        var existingUser = getById(id);
        userRepository.delete(existingUser);
    }

    /**
     * Checks if the provided email is already taken in the database.
     *
     * @param email the email to check for existence
     * @throws EntityAlreadyExistsException if the email is already taken
     */
    private void assertThatEmailNotTaken(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EntityAlreadyExistsException("User with this email already registered");
        }
    }

    /**
     * Checks if the provided age is legal, i.e., greater than or equal to the
     * required minimum age.
     *
     * @param user the user whose age should be checked
     * @throws UserValidationException if the user's age is below the required
     *                                 age
     */
    private void assertThatAgeIsLegal(User user) {
        int requiredAge = userConstraints.getRequiredAge();
        long userAge = ChronoUnit.YEARS.between(user.getBirthDate(), LocalDate.now());
        if (userAge < requiredAge) {
            var message = String.format("Users age must be greater than or equal to %d", requiredAge);
            throw new UserValidationException(message);
        }
    }

    private Specification<User> inRange(DateRange dateRange) {
        if (dateRange == null || !dateRange.isSet()) return Specification.where(null);

        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            var birthDate = root.get("birthDate").as(LocalDate.class);

            if ((dateRange.getFrom() == null)) {
                return criteriaBuilder.lessThanOrEqualTo(birthDate, dateRange.getTo());
            } else if (dateRange.getTo() == null) {
                return criteriaBuilder.greaterThanOrEqualTo(birthDate, dateRange.getFrom());
            }
            return criteriaBuilder.between(birthDate, dateRange.from(), dateRange.to());
        };
    }
}
