package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private List<Film> filmsWithLikes;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        this.filmsWithLikes = new ArrayList<>(filmStorage.getAllFilms());
    }

    public void addLike(long filmId, long userId) {
        Film film = findFilmById(filmId);
        if (film == null) {
            throw new IllegalArgumentException("Фильм не найден");
        }
        if (!film.getLikes().contains(userId)) {
            film.getLikes().add(userId);
            filmStorage.modifyFilm(film);
        }
    }

    public void removeLike(long filmId, long userId) {
        Film film = findFilmById(filmId);
        if (film == null) {
            throw new IllegalArgumentException("Фильм не найден");
        }
        film.getLikes().removeIf(id -> id == userId);
        filmStorage.modifyFilm(film);
    }

    public List<Film> getTopPopularFilms() {
        Collections.sort(filmsWithLikes, (f1, f2) -> Integer.compare(f2.getLikesCount(), f1.getLikesCount()));
        return filmsWithLikes.subList(0, Math.min(10, filmsWithLikes.size()));
    }

    private Film findFilmById(long id) {
        for (Film film : filmsWithLikes) {
            if (film.getId() == id) {
                return film;
            }
        }
        return null;
    }
}

