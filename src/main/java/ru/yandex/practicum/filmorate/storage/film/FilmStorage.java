package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    void addFilm(Film film);
    Film getFilmById(int id);
    void updateFilm(int id, Film updatedFilm);
    List<Film> getAllFilms();
}

