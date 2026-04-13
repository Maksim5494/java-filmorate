package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        validate(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(int id, Film updatedFilm) {
        if (!filmStorage.exists(id)) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        validate(updatedFilm);
        filmStorage.updateFilm(id, updatedFilm);
        return filmStorage.getFilmById(id);
    }

    public Film getFilmById(int id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(int filmId, int userId) {
        if (!filmStorage.exists(filmId)) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
        if (!userStorage.exists(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        if (!filmStorage.exists(filmId)) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
        if (!userStorage.exists(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }

    public void clearFilms() {
        filmStorage.clearFilms();
    }

    private void validate(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getMpa() == null || film.getMpa().getId() <= 0) {
            throw new ValidationException("Не указан рейтинг MPA");
        }

        Mpa mpa = filmStorage.getMpaById(film.getMpa().getId());
        if (mpa == null) {
            throw new NotFoundException("MPA с id=" + film.getMpa().getId() + " не найден");
        }
        film.setMpa(mpa);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            LinkedHashSet<Genre> validGenres = new LinkedHashSet<>();
            for (Genre genre : film.getGenres()) {
                Genre foundGenre = filmStorage.getGenreById(genre.getId());
                if (foundGenre == null) {
                    throw new NotFoundException("Жанр с id=" + genre.getId() + " не найден");
                }
                validGenres.add(foundGenre);
            }
            film.setGenres(validGenres);
        }
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        Genre genre = filmStorage.getGenreById(id);
        if (genre == null) {
            throw new NotFoundException("Жанр с id=" + id + " не найден");
        }
        return genre;
    }

    public List<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Mpa getMpaById(int id) {
        Mpa mpa = filmStorage.getMpaById(id);
        if (mpa == null) {
            throw new NotFoundException("MPA с id=" + id + " не найден");
        }
        return mpa;
    }
}