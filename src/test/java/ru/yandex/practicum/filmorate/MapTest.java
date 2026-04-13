package ru.yandex.practicum.filmorate;

import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.*;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.*;

class MapTest {
    private final Map<Integer, Genre> genres = new HashMap<>();

    @Test
    void shouldAddGenre() {
        Genre genre = new Genre(1, "Action");
        genres.put(genre.getId(), genre);
        assertTrue(genres.containsKey(1));
    }

    @Test
    void shouldReturnGenreById() {
        Genre addedGenre = new Genre(2, "Comedy");
        genres.put(addedGenre.getId(), addedGenre);

        Genre retrievedGenre = genres.get(2);
        assertNotNull(retrievedGenre);
        assertEquals("Comedy", retrievedGenre.getName());
    }

    @Test
    void shouldNotReturnGenreForNonexistentId() {
        genres.put(3, new Genre(3, "Horror"));
        assertNull(genres.get(999));
    }

    @Test
    void shouldReturnNullForNonexistentGenreId() {
        genres.put(3, new Genre(3, "Horror"));
        assertNull(genres.get(999));
        assertFalse(genres.containsKey(999));  // Дополнительная проверка
    }

    @Test
    void shouldRemoveGenre() {
        Genre genre = new Genre(4, "Sci-Fi");
        genres.put(genre.getId(), genre);
        genres.remove(4);
        assertFalse(genres.containsKey(4));
    }

    @Test
    void shouldGetAllGenres() {
        genres.put(5, new Genre(5, "Romance"));
        genres.put(6, new Genre(6, "Thriller"));

        List<Genre> allGenres = new ArrayList<>(genres.values());

        assertThat(allGenres)
                .extracting("id", "name")
                .contains(
                        tuple(5, "Romance"),
                        tuple(6, "Thriller")
                );
    }


    @Test
    void shouldNotAllowDuplicateId() {
        Genre firstGenre = new Genre(1, "Action");
        Genre secondGenre = new Genre(1, "Another Action");

        genres.put(firstGenre.getId(), firstGenre);
        genres.put(secondGenre.getId(), secondGenre);

        assertTrue(genres.containsKey(1));
        assertEquals("Another Action", genres.get(1).getName());
    }

    @Test
    void shouldReturnZeroWhenEmpty() {
        List<Genre> allGenres = new ArrayList<>(genres.values());
        assertEquals(0, allGenres.size());
    }

    @Test
    void shouldReturnZeroWhenMapIsEmpty() {
        genres.put(1, new Genre(1, "Action"));
        genres.remove(1);
        List<Genre> allGenres = new ArrayList<>(genres.values());
        assertEquals(0, allGenres.size());
    }
}