package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 0;

    @Override
    public Film addFilm(Film film) {
        film.setId(++idCounter);
        if (film.getLikes() == null) {
            film.setLikes(new java.util.HashSet<>());
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
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
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        Film oldFilm = films.get(id);
        film.setId(id);
        if (film.getLikes() == null) {
            film.setLikes(oldFilm.getLikes());
        }
        films.put(id, film);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = films.get(filmId);
        if (film != null) {
            film.getLikes().add(userId);
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = films.get(filmId);
        if (film != null) {
            film.getLikes().remove(userId);
        }
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return films.values().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}