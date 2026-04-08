package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable int id, @Valid @RequestBody Film film) {
        // Проверка согласованности ID
        Integer filmIdFromBody = film.getId();
        if (filmIdFromBody != null && filmIdFromBody != id) {
            throw new ValidationException("ID в пути (" + id + ") не совпадает с ID в теле (" + filmIdFromBody + ")");
        }

        // Устанавливаем ID из пути, если в теле не указан
        if (filmIdFromBody == null) {
            film.setId(id);
        }

        return filmService.updateFilm(id, film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {  // ← int вместо Long
        Film film = filmService.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        return film;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable int filmId, @PathVariable int userId) {  // ← int вместо Long
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {  // ← Long
        filmService.removeLike(filmId.intValue(), userId.intValue());
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", required = false, defaultValue = "10") int count) {
        return filmService.getTopFilms(count);
    }
}