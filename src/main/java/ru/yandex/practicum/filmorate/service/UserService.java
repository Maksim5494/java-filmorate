package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final Map<Integer, Set<Integer>> friendships = new HashMap<>();

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        friendships.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friendships.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    public void removeFriend(int userId, int friendId) {
        // Сначала проверяем, существуют ли оба пользователя
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Друг с id " + friendId + " не найден");
        }

        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        getUserById(userId); // Проверка на существование
        return friendships.getOrDefault(userId, new HashSet<>()).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId1, int userId2) {
        Set<Integer> user1Friends = friendships.getOrDefault(userId1, new HashSet<>());
        Set<Integer> user2Friends = friendships.getOrDefault(userId2, new HashSet<>());

        return user1Friends.stream()
                .filter(user2Friends::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user.getId(), user);
    }

    public User getUserById(int id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь " + id + " не найден");
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void deleteFriend(Integer id, Integer friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        if (user != null && friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(id); // Удаляем взаимно
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
