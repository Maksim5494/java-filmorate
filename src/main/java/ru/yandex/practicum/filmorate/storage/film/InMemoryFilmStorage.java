package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void removeFilm(Long id) {
        films.remove(id);
    }

    @Override
    public Film modifyFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film findFilmById(long id) {
        return films.get(id);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }
}