package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User getUserById(int id);

    User updateUser(int id, User updatedUser);

    List<User> getAllUsers();

    void addFriend(int id, int friendId);

    void removeFriend(int id, int friendId);

    List<User> getFriends(int id);

    List<User> getCommonFriends(int id, int otherId);

    void deleteFriend(int userId, int friendId);
}

