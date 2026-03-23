package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private List<Film> films = new ArrayList<>();

    @Override
    public void addFilm(Film film) {
        films.add(film);
    }

    @Override
    public void removeFilm(Film film) {
        films.remove(film);
    }

    @Override
    public void modifyFilm(Film film) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId().equals(film.getId())) {
                films.set(i, film);
                break;
            }
        }
    }

    @Override
    public Film findFilmById(long id) {
        for (Film film : films) {
            if (film.getId() == id) {
                return film;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Film> getAllFilms() {
        return new ArrayList<>(films);
    }

    @Override
    public List<Film> findFilmsByName(String name) {
        List<Film> foundFilms = new ArrayList<>();
        for (Film film : films) {
            if (film.getName().equals(name)) {
                foundFilms.add(film);
            }
        }
        return foundFilms;
    }

    @Override
    public int countFilms() {
        return films.size();
    }
}

