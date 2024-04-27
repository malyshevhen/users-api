package com.github.malyshevhen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;

import com.github.malyshevhen.models.User;

/**
 * Repository interface for managing User entities.
 * </p>
 * Extends JpaRepository and JpaSpecificationExecutor to use Spring Data JPA features.
 *
 * @author Evhen Malysh
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Checks if a user with the given email address exists in the repository.
     *
     * @param email the email address to check
     * @return true if a user with the given email address exists, false otherwise
     */
    boolean existsByEmail(@NonNull String email);

}
