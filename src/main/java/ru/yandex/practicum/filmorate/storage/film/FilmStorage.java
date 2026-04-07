package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film getFilmById(int id);

    void updateFilm(int id, Film updatedFilm);

    List<Film> getAllFilms();

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getTopFilms(int count);
}


