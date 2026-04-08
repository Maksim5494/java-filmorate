package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 0;

    @Override
    public Film addFilm(Film film) {
        film.setId(++idCounter);
        films.put(film.getId(), film);
        return film;
    }

    public boolean exists(int id) {
        return films.containsKey(id);
    }

    @Override
    public void clearFilms() {
        films.clear();
        idCounter = 0;
    }

    @Override
    public Film getFilmById(int id) {

        return films.get(id);
    }

    @Override
    public void updateFilm(int id, Film film) {
        film.setId(id);
        films.put(id, film);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = films.get(filmId);
        if (film != null) film.getLikes().add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = films.get(filmId);
        if (film != null) film.getLikes().remove(userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikesCount(), f1.getLikesCount()))
                .limit(count)
                .collect(Collectors.toList());
    }
}