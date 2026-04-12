package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Genre;

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
        assertTrue(genre1.equals(genre2)); // Обычно равенство зависит только от ID
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
}

