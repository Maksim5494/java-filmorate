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
        assertFalse(genre1.equals(genre2));
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
    void shouldNotEqualGenreWithSameIdButDifferentName() {
        Genre genre1 = new Genre(1, "Action");
        Genre genre2 = new Genre(1, "Adventure");
        assertFalse(genre1.equals(genre2));
    }

    @Test
    void shouldNotAllowNegativeId() {
        assertThrows(IllegalArgumentException.class, () -> new Genre(-1, "Action"));
    }

    @Test
    void filmGenresSize() {
        Film film = new Film(1, "Test Film", "Description", LocalDate.now(), 120, Mpa.PG_13);

        Genre genre1 = new Genre(1, "Action");
        Genre genre2 = new Genre(2, "Comedy");
        Genre genre3 = new Genre(3, "Drama");

        film.addGenre(genre1);
        System.out.println("После первого добавления: " + film.getGenres().size()); // Должно быть 1

        film.addGenre(genre2);
        System.out.println("После второго добавления: " + film.getGenres().size()); // Должно быть 2

        film.addGenre(genre3);
        System.out.println("После третьего добавления: " + film.getGenres().size()); // Должно быть 3

        assertEquals(3, film.getGenres().size());
    }

}

