package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        if (user == null || friend == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        List<User> friends = user.getFriends();
        if (!friends.contains(friendId)) {
            friends.add(friendId);
            userStorage.modifyUser(user);
        }
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.findUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        List<Long> friends = user.getFriends();
        friends.removeIf(id -> id.equals(friendId));
        userStorage.modifyUser(user);
    }

    public List<Long> getCommonFriends(long userId, long otherUserId) {
        User user = userStorage.findUserById(userId);
        User otherUser = userStorage.findUserById(otherUserId);

        if (user == null || otherUser == null) {
            throw new IllegalArgumentException("Один или оба пользователя не найдены");
        }

        List<Long> commonFriends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            if (otherUser.getFriends().contains(friendId)) {
                commonFriends.add(friendId);
            }
        }
        return commonFriends;
    }
}

