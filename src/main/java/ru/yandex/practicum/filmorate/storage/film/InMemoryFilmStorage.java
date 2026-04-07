package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private List<Film> films = new ArrayList<>();

    @Override
    public Film addFilm(Film film) {
        films.add(film);
        return film; // возвращаем добавленный фильм
    }

    @Override
    public Film getFilmById(int id) {
        return films.stream()
                .filter(film -> film.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void updateFilm(int id, Film updatedFilm) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == id) {
                films.set(i, updatedFilm);
                return; // завершаем метод, если фильм найден и обновлен
            }
        }
        throw new IllegalArgumentException("Фильм с ID " + id + " не найден"); // или другое исключение
    }


    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films);
    }

    @Override
    public void addLike(int filmId, int userId) {

    }

    @Override
    public void removeLike(int filmId, int userId) {

    }

    @Override
    public List<Film> getTopFilms(int count) {
        return List.of();
    }

    @Override
    public void clearFilms() {

    }
}