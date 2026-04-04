package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FilmValidationTest {

    private MockMvc mockMvc;

    @Autowired
    private Validator validator;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Test
    public void testFilmValidation() {
        System.out.println("Validator is " + (validator == null ? "null" : "initialized"));
        Film film = new Film();
        film.setName("");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertThat(violations).isNotEmpty();

        violations.forEach(violation -> System.out.println(violation.getMessage()));
    }

    @BeforeEach
    void setUp() {
        Collection<Film> allFilms = filmService.findAll();
        for (Film film : allFilms) {
            filmService.removeFilm(film.getId());
        }

        assertThat(filmService.findAll()).isEmpty();

        Set<Long> likeIds = new HashSet<>();
        likeIds.add(1L);
        likeIds.add(2L);

        User user = new User();
        user.setName("Тестовый пользователь");
        userService.create(user);
        Long userId = user.getId();

        Film film1 = new Film("Фильм 1");
        film1.setReleaseDate(LocalDate.now());
        filmService.create(film1);

        Film film2 = new Film("Фильм 2");
        film2.setReleaseDate(LocalDate.of(2023, 1, 1));
        filmService.create(film2);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testRemoveLikeSuccess() throws Exception {
        Film film = new Film("Тестовый фильм");
        film.setReleaseDate(LocalDate.now());
        filmService.create(film);
        Long filmId = film.getId();
        Long userId = 1L;

        filmService.addLike(filmId, userId);

        filmService.removeLike(filmId, userId);
        Film updatedFilm = filmService.getById(filmId);
        assertThat(updatedFilm.getLikes()).doesNotContain(userId);
    }

    @Test
    void testRemoveLikeForNonExistingFilm() throws Exception {
        Long nonExistingFilmId = 999L;
        Long userId = 1L;

        try {
            filmService.removeLike(nonExistingFilmId, userId);
            fail("Ожидалось исключение NotFoundException, но его не было");
        } catch (NotFoundException e) {
            assertThat(e.getMessage()).contains("Фильм с ID " + nonExistingFilmId + " не найден");
        }
    }



    @Test
    void testRemoveLikeNotFound() throws Exception {
        Film film = new Film("Тестовый фильм для проверки ошибки");
        film.setReleaseDate(LocalDate.now());
        filmService.create(film);
        Long filmId = film.getId();
        Long nonExistingUserId = 999L;

        try {
            filmService.removeLike(filmId, nonExistingUserId);
            fail("Ожидалось исключение NotFoundException, но его не было");
        } catch (NotFoundException e) {
            assertThat(e.getMessage()).contains("Лайк от пользователя " + nonExistingUserId + " не найден");
        }
    }

    @Test
    void testGetPopularFilms() {
        // Создаём несколько фильмов с разным количеством лайков
        Film film1 = new Film("Фильм 1");
        film1.setReleaseDate(LocalDate.now());
        filmService.create(film1);

        Film film2 = new Film("Фильм 2");
        film2.setReleaseDate(LocalDate.of(2023, 1, 1));
        filmService.create(film2);

        // Добавляем пользователя в систему
        User user1 = new User();
        user1.setName("Пользователь 1");
        userService.create(user1);
        Long userId1 = user1.getId();

        User user2 = new User();
        user2.setName("Пользователь 2");
        userService.create(user2);
        Long userId2 = user2.getId();

        // Эмуляция лайков (2 для film1, 1 для film2, 0 для film3)
        filmService.addLike(film1.getId(), userId1);
        filmService.addLike(film1.getId(), userId2);

    }

    @Test
    void testGetPopularFilms_WhenAllFilmsHaveSameLikes() {
        // Создаём пользователя с ID, который будем использовать для лайков
        User user = new User();
        user.setName("Тестовый пользователь");
        Long userId = userService.create(user).getId(); // Сохраняем ID созданного пользователя

        // Теперь создаём фильмы и ставим лайки от имени этого пользователя
        Film film1 = new Film("Фильм 1");
        film1.setReleaseDate(LocalDate.now());
        filmService.create(film1);
        filmService.addLike(film1.getId(), userId); // Используем ID реального пользователя

        Film film2 = new Film("Фильм 2");
        film2.setReleaseDate(LocalDate.of(2023, 1, 1));
        filmService.create(film2);
        filmService.addLike(film2.getId(), userId);
    }
}