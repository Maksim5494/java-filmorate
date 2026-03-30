package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface UserStorage {
    User addUser(User user);
    void removeUser(Long id);
    User modifyUser(User user);
    User findUserById(long id);
    Collection<User> getAllUsers();
}

