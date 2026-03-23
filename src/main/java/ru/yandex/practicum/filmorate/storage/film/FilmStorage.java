package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;

public interface FilmStorage {

    void addFilm(Film film);

    void removeFilm(Film film);

    void modifyFilm(Film film);

    ArrayList<Film> getAllFilms();
}

