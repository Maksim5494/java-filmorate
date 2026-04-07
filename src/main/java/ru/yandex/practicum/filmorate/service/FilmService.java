package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FilmService {
    private Map<Integer, Film> films = new HashMap<>(); // Карта, где ключ - ID фильма, а значение - объект фильма

    public void addLike(int filmId, int userId) {
        if (!films.containsKey(filmId)) {
            films.put(filmId, new Film(filmId));
        }
        films.get(filmId).addLike(userId);
    }

    public void removeLike(int filmId, int userId) {
        if (films.containsKey(filmId)) {
            films.get(filmId).removeLike(userId);
        }
    }

    public List<Film> getTopFilms(int count) {
        List<Film> sortedFilms = new ArrayList<>(films.values());
        sortedFilms.sort((f1, f2) -> f2.getLikesCount() - f1.getLikesCount());
        return sortedFilms.subList(0, Math.min(count, sortedFilms.size()));
    }

    private static class Film {
        private int id;
        private Set<Integer> likes = new HashSet<>();

        public Film(int id) {
            this.id = id;
        }

        public void addLike(int userId) {
            likes.add(userId);
        }

        public void removeLike(int userId) {
            likes.remove(userId);
        }

        public int getLikesCount() {
            return likes.size();
        }
    }
}
