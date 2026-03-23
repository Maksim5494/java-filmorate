package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

    void addUser(User user);

    void removeUser(User user);

    void modifyUser(User user);

    User findUserById(long userId);
}

