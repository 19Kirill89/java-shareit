package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User create(User user);

    User update(User user);

    User delete(Long userId);

    List<User> getAllUsers();

    User getUserById(Long userId);

    boolean isUserInBD(Long id);

    Long getUserIdByEmail(String email);
}