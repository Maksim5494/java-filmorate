package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {

    Film addFilm(Film film);

    void removeFilm(Long id);

    Film modifyFilm(Film film);

    Collection<Film> getAllFilms();

    Film findFilmById(long id);

}