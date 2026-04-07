package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private Map<Integer, Film> films = new HashMap<>(); // Карта, где ключ — ID фильма, а значение — объект фильма

    public void addLike(int filmId, int userId) {
        Film film = films.get(filmId);
        if (film == null) {
            film = new Film(filmId, "", "", LocalDate.now(), 0); // Создаём фильм, если его нет
            films.put(filmId, film);
        }
        film.addLike(userId);
    }

    public Film addFilm(Film film) {
        int newId = films.size() + 1; // Генерируем новый ID для фильма
        film.setId(newId);
        films.put(newId, film);
        return film;
    }

    public void removeLike(int filmId, int userId) {
        Film film = films.get(filmId);
        if (film != null) {
            film.removeLike(userId);
        }
    }

    public List<Film> getTopFilms(int count) {
        return films.values()
                .stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed()) // Сортируем по количеству лайков
                .limit(count)
                .collect(Collectors.toList());
    }

    // Реализуем метод getFilmById
    public Film getFilmById(int id) {
        return films.get(id);
    }

    // Реализуем метод updateFilm
    public void updateFilm(int id, Film updatedFilm) {
        updatedFilm.setId(id); // Убеждаемся, что ID не изменился
        films.put(id, updatedFilm);
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public void clearFilms() {
        films.clear();
    }

    public List<Film> getTopFilms() {
        return List.of();
    }
}
