package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Set<Integer>> friendships = new HashMap<>();
    private int idCounter = 0;

    @Override
    public User addUser(User user) {
        user.setId(++idCounter);
        users.put(idCounter, user);
        return user;
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }

    @Override
    public User updateUser(int id, User updatedUser) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        updatedUser.setId(id);
        users.put(id, updatedUser);
        return updatedUser;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(int id, int friendId) {
        if (!users.containsKey(id) || !users.containsKey(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        friendships.computeIfAbsent(id, k -> new HashSet<>()).add(friendId);
        friendships.computeIfAbsent(friendId, k -> new HashSet<>()).add(id);
    }

    @Override
    public void removeFriend(int id, int friendId) {
        if (!users.containsKey(id) || !users.containsKey(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        friendships.getOrDefault(id, new HashSet<>()).remove(friendId);
        friendships.getOrDefault(friendId, new HashSet<>()).remove(id);
    }

    @Override
    public List<User> getFriends(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }

        Set<Integer> friendIds = friendships.getOrDefault(id, new HashSet<>());
        return friendIds.stream()
                .map(this::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int id1, int id2) {
        if (!users.containsKey(id1) || !users.containsKey(id2)) {
            throw new NotFoundException("Пользователь не найден");
        }

        Set<Integer> friends1 = friendships.getOrDefault(id1, new HashSet<>());
        Set<Integer> friends2 = friendships.getOrDefault(id2, new HashSet<>());

        Set<Integer> commonIds = new HashSet<>(friends1);
        commonIds.retainAll(friends2);

        return commonIds.stream()
                .map(this::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public boolean exists(int id) {
        return users.containsKey(id);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        removeFriend(userId, friendId);
    }
}