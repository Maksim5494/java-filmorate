package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);
    User getUserById(int id);
    User updateUser(int id, User updatedUser);
    List<User> getAllUsers();
}

