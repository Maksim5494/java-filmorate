package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.userService = userService;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getById(filmId); // Используем внутренний метод, который кинет 404 если надо
        if (userService.findUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        film.getLikes().add(userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getById(filmId); // Бросит NotFoundException "Фильм с id ... не найден"

        // Проверяем существование пользователя через storage или service
        if (userStorage.findUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Лайк от пользователя " + userId + " не найден");
        }

        film.getLikes().remove(userId);
        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
    }




    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> {
                    int likesComparison = Integer.compare(f2.getLikes().size(), f1.getLikes().size());
                    if (likesComparison != 0) {
                        return likesComparison; // сначала сортируем по количеству лайков (по убыванию)
                    }
                    return Long.compare(f1.getId(), f2.getId()); // при равенстве — по ID (по возрастанию)
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    public Collection<Film> findAll() {
        return filmStorage.getAllFilms();
    }

    public Film create(Film film) {
        if (film.getReleaseDate() == null) {
            throw new IllegalArgumentException("Дата релиза фильма не может быть null");
        }

        LocalDate minAllowedDate = LocalDate.of(1888, 1, 1);
        if (film.getReleaseDate().isBefore(minAllowedDate)) {
            throw new IllegalArgumentException(
                    "Дата релиза не может быть раньше " + minAllowedDate
            );
        }

        return filmStorage.addFilm(film);
    }


    public Film update(Film film) {
        Film existingFilm = filmStorage.findFilmById(film.getId());
        if (existingFilm == null) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        return filmStorage.modifyFilm(film);
    }

    public void removeFilm(Long id) {
        Film film = filmStorage.findFilmById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        filmStorage.deleteFilm(id);
        log.info("Фильм {} был удалён", id);
    }

    public Film getById(Long id) {
        Film film = filmStorage.findFilmById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    public void addToFavorites(Long filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }

        User user = userService.findUserById(userId); // Используем userService для поиска пользователя
        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        // Добавляем фильм в список избранного пользователя
        user.getFavoriteFilms().add(film);
        userService.update(user); // Используем userService для обновления пользователя
    }
}

