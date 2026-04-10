package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    private final Map<Integer, Map<Integer, FriendshipStatus>> friendships = new HashMap<>();
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

        Map<Integer, FriendshipStatus> userFriends = friendships.computeIfAbsent(id, k -> new HashMap<>());
        Map<Integer, FriendshipStatus> friendFriends = friendships.computeIfAbsent(friendId, k -> new HashMap<>());

        if (friendFriends.containsKey(id)) {
            // Если второй пользователь уже отправлял заявку первому — подтверждаем у обоих
            userFriends.put(friendId, FriendshipStatus.CONFIRMED);
            friendFriends.put(id, FriendshipStatus.CONFIRMED);
        } else {
            // Иначе — создаем неподтвержденную заявку у отправителя
            userFriends.put(friendId, FriendshipStatus.UNCONFIRMED);
        }
    }

    @Override
    public void removeFriend(int id, int friendId) {
        if (friendships.containsKey(id)) {
            friendships.get(id).remove(friendId);
        }
        // Если дружба была подтвержденной, у второго пользователя статус должен смениться
        // или удалиться (зависит от логики бизнеса, обычно удаляется связь вовсе)
        if (friendships.containsKey(friendId)) {
            friendships.get(friendId).remove(id);
        }
    }

    @Override
    public List<User> getFriends(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }

        Map<Integer, FriendshipStatus> userFriends = friendships.getOrDefault(id, Collections.emptyMap());

        // Возвращаем всех, кому мы отправили заявку или с кем дружба подтверждена
        return userFriends.entrySet().stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(entry -> users.get(entry.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int id1, int id2) {
        Set<Integer> friends1 = friendships.getOrDefault(id1, Collections.emptyMap()).entrySet().stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Set<Integer> friends2 = friendships.getOrDefault(id2, Collections.emptyMap()).entrySet().stream()
                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        return friends1.stream()
                .filter(friends2::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean exists(int id) {

        return users.containsKey(id);
    }

    @Override
    public void clearUsers() {
        users.clear();
        friendships.clear();
        idCounter = 0;
    }
}