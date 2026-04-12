package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        assertEquals(2, allGenres.size());

        boolean hasRomance = allGenres.stream().anyMatch(g -> g.getId() == 5 && g.getName().equals("Romance"));
        boolean hasThriller = allGenres.stream().anyMatch(g -> g.getId() == 6 && g.getName().equals("Thriller"));
        assertTrue(hasRomance);
        assertTrue(hasThriller);
    }

    @Test
    void shouldReturnZeroWhenEmpty() {
        List<Genre> allGenres = new ArrayList<>(genres.values());
        assertEquals(0, allGenres.size());
    }
}