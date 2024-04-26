package com.github.malyshevhen.services;

import com.github.malyshevhen.models.Address;
import com.github.malyshevhen.models.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UserService {

    User save(@Valid User userToRegister);

    Page<User> getAll(@NotNull Pageable pageable);

    User getById(@NotNull Long id);

    User updateById(@NotNull Long id, @Valid User user);

    void deleteById(@NotNull Long id);

    User updateEmail(Long id, String email);

    User updateAddress(Long id, Address address);

}
