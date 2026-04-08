package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FilmValidationTest {

    @Autowired
    private Validator validator;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        filmService.clearFilms();
        userStorage.clearUsers();
    }

    private int createTestUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setLogin("login" + email.hashCode());
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return userStorage.addUser(user).getId();
    }

    @Test
    public void testAddLike() {
        Film film = new Film();
        film.setName("Inception");
        film.setDuration(148);
        film.setReleaseDate(LocalDate.now());
        Film addedFilm = filmService.addFilm(film);

        int userId = createTestUser("user@test.com");

        filmService.addLike(addedFilm.getId(), userId);

        assertThat(filmService.getFilmById(addedFilm.getId()).getLikes()).contains(userId);
    }

    @Test
    public void testRemoveLike() {
        Film film = new Film();
        film.setName("Film");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now());
        Film addedFilm = filmService.addFilm(film);

        int user1 = createTestUser("u1@test.com");
        int user2 = createTestUser("u2@test.com");

        filmService.addLike(addedFilm.getId(), user1);
        filmService.addLike(addedFilm.getId(), user2);

        filmService.removeLike(addedFilm.getId(), user1);

        assertThat(filmService.getFilmById(addedFilm.getId()).getLikes()).doesNotContain(user1);
        assertThat(filmService.getFilmById(addedFilm.getId()).getLikes()).contains(user2);
    }

    @Test
    public void testGetTopFilms() {
        Film film1 = new Film();
        film1.setName("Popular Film");
        film1.setReleaseDate(LocalDate.now());
        film1.setDuration(100);
        Film addedFilm1 = filmService.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Less Popular Film");
        film2.setReleaseDate(LocalDate.now());
        film2.setDuration(90);
        Film addedFilm2 = filmService.addFilm(film2);

        int u1 = createTestUser("1@t.com");
        int u2 = createTestUser("2@t.com");
        int u3 = createTestUser("3@t.com");

        filmService.addLike(addedFilm1.getId(), u1);
        filmService.addLike(addedFilm1.getId(), u2);

        filmService.addLike(addedFilm2.getId(), u3);

        List<Film> topFilms = filmService.getTopFilms(10);

        assertThat(topFilms).hasSize(2);
        assertThat(topFilms.get(0).getId()).isEqualTo(addedFilm1.getId());
        assertThat(topFilms.get(1).getId()).isEqualTo(addedFilm2.getId());
    }

    @Test
    public void testAddLikeIncreasesLikesCount() {
        Film film = new Film();
        film.setName("Count Test");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        Film addedFilm = filmService.addFilm(film);

        int u1 = createTestUser("a@t.com");
        int u2 = createTestUser("b@t.com");

        filmService.addLike(addedFilm.getId(), u1);
        filmService.addLike(addedFilm.getId(), u2);

        Film updatedFilm = filmService.getFilmById(addedFilm.getId());
        assertThat(updatedFilm.getLikesCount()).isEqualTo(2);
    }

    @Test
    public void testFilmValidation() {
        Film film = new Film();
        film.setName("");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertThat(violations).isNotEmpty();
    }

    @Test
    public void testAddFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        Film addedFilm = filmService.addFilm(film);

        assertThat(addedFilm).isNotNull();
        assertThat(addedFilm.getId()).isGreaterThan(0);
    }

    @Test
    void testUpdateFilm() {
        // 1. Сначала создаем фильм
        Film film = new Film();
        film.setName("Фильм для обновления");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmService.addFilm(film);
        int filmId = createdFilm.getId();

        Film updatedFilm = new Film();
        updatedFilm.setId(filmId);
        updatedFilm.setName("Обновленное название");
        updatedFilm.setDescription("Обновленное описание");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        updatedFilm.setDuration(150);

        Film result = filmService.updateFilm(filmId, updatedFilm);

        assertEquals("Обновленное название", result.getName());
        assertEquals("Обновленное описание", result.getDescription());
    }

    @Test
    public void testGetFilmById() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        Film addedFilm = filmService.addFilm(film);

        Film retrievedFilm = filmService.getFilmById(addedFilm.getId());

        assertThat(retrievedFilm).isNotNull();
        assertThat(retrievedFilm.getId()).isEqualTo(addedFilm.getId());
        assertThat(retrievedFilm.getName()).isEqualTo("Test Film");
        assertThat(retrievedFilm.getDescription()).isEqualTo("Description");
        assertThat(retrievedFilm.getDuration()).isEqualTo(120);
    }

    @Test
    public void testGetAllFilms() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Desc 1");
        film1.setReleaseDate(LocalDate.now());
        film1.setDuration(90);
        filmService.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Desc 2");
        film2.setReleaseDate(LocalDate.now());
        film2.setDuration(120);
        filmService.addFilm(film2);

        List<Film> allFilms = filmService.getAllFilms();

        assertThat(allFilms).isNotNull();
        assertThat(allFilms.size()).isEqualTo(2);
        assertThat(allFilms).contains(film1, film2);
    }

    @Test
    public void testGetTopFilmsWhenNoFilms() {
        filmService.clearFilms();

        List<Film> topFilms = filmService.getTopFilms(1);

        assertThat(topFilms).isEmpty();
    }
}
