package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователи {} и {} стали друзьями", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        User user = getUserOrThrow(userId);
        return user.getFriends().stream()
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = getUserOrThrow(id);
        User otherUser = getUserOrThrow(otherId);

        Set<Long> commonIds = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .collect(Collectors.toSet());

        return commonIds.stream()
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Long id) {
        User user = userStorage.findUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    public Collection<User> findAll() {
        return userStorage.getAllUsers();
    }

    public User findUserById(long id) {
        return userStorage.findUserById(id);
    }

    public User create(User user) {

        return userStorage.addUser(user);
    }

    public User update(User user) {
        getUserOrThrow(user.getId());
        return userStorage.modifyUser(user);
    }

    public Set<Film> getFavoriteFilms(Long userId) {
        User user = getUserOrThrow(userId);
        return user.getFavoriteFilms(); // Возвращаем реальную коллекцию
    }

    public void removeUser(Long userId) {
        User user = userStorage.findUserById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        userStorage.removeUser(userId);  // предполагаемый метод удаления из хранилища
        log.info("Пользователь с ID {} удалён", userId);
    }

    public AddToFavoritesResult addToFavorites(Long userId, Long filmId) {
        User user = getUserOrThrow(userId);
        Film film = filmStorage.findFilmById(filmId);

        // Если фильм не найден — возвращаем соответствующий статус
        if (film == null) {
            return new AddToFavoritesResult(
                    false,
                    "Фильм с ID " + filmId + " не найден"
            );
        }

        // Проверяем, есть ли фильм уже в избранном
        if (user.getFavoriteFilms().contains(film)) {
            log.info("Пользователь {} уже добавил фильм {} в избранное", userId, film.getTitle());
            return new AddToFavoritesResult(
                    false,
                    "Фильм уже добавлен в избранное"
            );
        } else {
            user.getFavoriteFilms().add(film);
            log.info("Фильм {} добавлен в избранное пользователем {}", film.getTitle(), userId);
            userStorage.modifyUser(user);
            return new AddToFavoritesResult(
                    true,
                    "Успешное добавление в избранное"
            );
        }
    }
}

