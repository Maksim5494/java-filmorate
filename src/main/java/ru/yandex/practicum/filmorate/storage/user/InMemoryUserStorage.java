package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int counter = 1;

    @Override
    public User addUser(User user) {
        user.setId(counter++);
        users.put(user.getId(), user);
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
}
