package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public User addUser(User user) {
        if (user.getId() <= 0) {
            user.setId(nextId++);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void removeUser(Long id) {
        users.remove(id);
    }

    @Override
    public User modifyUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values()  // получаем коллекцию значений (пользователей)
                .stream() // теперь можно вызвать stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }
}


