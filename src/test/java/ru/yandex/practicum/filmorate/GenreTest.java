package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GenreTest {

    @Test
    void shouldHaveCorrectIdAndName() {
        Genre genre = new Genre(1, "Action");
        assertEquals(1, genre.getId());
        assertEquals("Action", genre.getName());
    }

    @Test
    void shouldEqualAnotherGenreWithSameId() {
        Genre genre1 = new Genre(1, "Action");
        Genre genre2 = new Genre(1, "Adventure");
        assertTrue(genre1.equals(genre2));
    }

    @Test
    void shouldNotEqualGenreWithDifferentId() {
        Genre genre1 = new Genre(1, "Action");
        Genre genre2 = new Genre(2, "Comedy");
        assertFalse(genre1.equals(genre2));
    }

    @Test
    void shouldReturnCorrectToString() {
        Genre genre = new Genre(1, "Drama");
        String actual = genre.toString();

        assertTrue(actual.contains("id=1"));
        assertTrue(actual.contains("name=Drama"));
        assertTrue(actual.startsWith("Genre"));
    }

    @Test
    void shouldHaveSameHashCodeForEqualGenres() {
        Genre genre1 = new Genre(1, "Action");
        Genre genre2 = new Genre(1, "Action");

        assertEquals(genre1.hashCode(), genre2.hashCode());
    }

    @Test
    void filmGenresSize() {
        // Создаём объект Film — теперь переменная film инициализирована
        Film film = new Film(1, "Test Film", "Description", LocalDate.now(), 120, Mpa.PG_13);

        // Создаём жанры
        Genre genre1 = new Genre(1, "Action");
        Genre genre2 = new Genre(2, "Comedy");
        Genre genre3 = new Genre(3, "Drama");

        // Добавляем жанры в фильм
        film.addGenre(genre1);
        film.addGenre(genre2);
        film.addGenre(genre3);

        // Проверяем, что в фильме ровно 3 жанра
        assertEquals(3, film.getGenres().size());
    }


}

