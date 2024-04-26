package com.github.malyshevhen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.github.malyshevhen.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(@NonNull String email);

    boolean existsByPhone(String phone);
}
