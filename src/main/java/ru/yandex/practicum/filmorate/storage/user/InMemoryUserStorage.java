package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;
    private final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);


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
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        users.remove(id);
        log.info("Пользователь с ID {} удалён из хранилища", id);
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


