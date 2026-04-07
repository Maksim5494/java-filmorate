package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {
    private Map<Integer, Set<Integer>> friendships = new HashMap<>(); // Карта, где ключ - ID пользователя, а значение - множество ID друзей

    public void addFriend(int userId, int friendId) {
        if (!friendships.containsKey(userId)) {
            friendships.put(userId, new HashSet<>());
        }
        friendships.get(userId).add(friendId);

        if (!friendships.containsKey(friendId)) {
            friendships.put(friendId, new HashSet<>());
        }
        friendships.get(friendId).add(userId);
    }

    public void removeFriend(int userId, int friendId) {
        if (friendships.containsKey(userId)) {
            friendships.get(userId).remove(friendId);
        }
        if (friendships.containsKey(friendId)) {
            friendships.get(friendId).remove(userId);
        }
    }

    public Set<Integer> getCommonFriends(int userId1, int userId2) {
        Set<Integer> commonFriends = new HashSet<>(friendships.getOrDefault(userId1, new HashSet<>()));
        commonFriends.retainAll(friendships.getOrDefault(userId2, new HashSet<>()));
        return commonFriends;
    }
}

