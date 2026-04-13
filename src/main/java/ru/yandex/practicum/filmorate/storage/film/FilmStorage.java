package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film getFilmById(int id);

    void updateFilm(int id, Film film);

    List<Film> getAllFilms();

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getTopFilms(int count);

    void clearFilms();

    boolean exists(int id);

    List<Genre> getAllGenres();

    Genre getGenreById(int id);

    List<Mpa> getAllMpa();

    Mpa getMpaById(int id);

}


