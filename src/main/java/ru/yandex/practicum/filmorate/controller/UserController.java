package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;

    public UserController(UserStorage userStorage) {

        this.userStorage = userStorage;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userStorage.addFriend(id, friendId);//добавление в друзья
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        userStorage.removeFriend(id, friendId);//удаление из друзей
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        return userStorage.getFriends(id); //получение списка друзей
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userStorage.getCommonFriends(id, otherId); //получение списка общих друзей
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        userStorage.addUser(user);
        log.info("Пользователь успешно добавлен: {}", user);
        return user;
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable int id, @Valid @RequestBody User user) {
        User updatedUser = userStorage.updateUser(id, user);
        if (updatedUser == null) {
            throw new NotFoundException("Пользователь с таким ID не найден");
        }
        log.info("Пользователь с ID {} успешно обновлен", id);
        return updatedUser;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с таким ID не найден");
        }
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        List<User> users = userStorage.getAllUsers();
        log.info("Получен запрос на список всех пользователей");
        return users;
    }

}

