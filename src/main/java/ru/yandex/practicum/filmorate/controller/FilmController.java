package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;


@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;

    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        filmStorage.addFilm(film);
        log.info("Фильм успешно добавлен: {}", film);
        return film;
    }

    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable int id, @Valid @RequestBody Film film) {
        filmStorage.updateFilm(id, film);
        log.info("Фильм с ID {} успешно обновлен", film.getId());
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получен запрос на список всех фильмов");
        return filmStorage.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с таким ID не найден");
        }
        return film;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable int filmId, @PathVariable int userId) {
        filmStorage.addLike(filmId, userId); //лайк фильму
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable int filmId, @PathVariable int userId) {
        filmStorage.removeLike(filmId, userId); //удаление лайка фильму
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", required = false, defaultValue = "10") int count) {
        return filmStorage.getTopFilms(count);
    }
}
