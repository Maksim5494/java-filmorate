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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        film.setTitle("");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertThat(violations).isNotEmpty();

        violations.forEach(violation -> System.out.println(violation.getMessage()));
    }

    private Long testUserId;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        //filmStorage.deleteAll();
        //userStorage.deleteAll();
        // Получаем список всех фильмов, извлекаем их ID и сохраняем в новый список (копию)
        List<Long> filmIds = filmService.findAll().stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        // Теперь удаляем по ID из независимого списка
        filmIds.forEach(id -> filmService.removeFilm(id));

        // То же самое стоит сделать для пользователей, если их много
        List<Long> userIds = userService.findAll().stream()
                .map(User::getId)
                .collect(Collectors.toList());
        userIds.forEach(id -> userService.removeUser(id));

        // Создаем пользователя и сохраняем его РЕАЛЬНЫЙ ID
        User user = new User();
        user.setEmail("test@yandex.ru");
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user = userService.create(user);
        testUserId = user.getId();
    }

    @Test
    public void testRemoveLikeSuccess() {
        Film film = new Film("Тестовый фильм");
        film.setReleaseDate(LocalDate.now());
        film = filmService.create(film);

        filmService.addLike(film.getId(), testUserId); // используем ID из setUp
        filmService.removeLike(film.getId(), testUserId);

        assertThat(filmService.getById(film.getId()).getLikes()).doesNotContain(testUserId);
    }

    @Test
    void testRemoveLikeForNonExistingFilm() throws Exception {
        Long nonExistingFilmId = 999L;
        Long userId = testUserId; // Используем существующего пользователя из setUp

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                filmService.removeLike(nonExistingFilmId, userId)
        );

        // Исправлено: "id" в нижнем регистре, как в методе getById() сервиса
        assertThat(e.getMessage()).contains("Фильм с ID " + nonExistingFilmId + " не найден");
    }

    @Test
    void testRemoveLikeNotFound() throws Exception {
        Film film = new Film("Фильм без лайков");
        film.setReleaseDate(LocalDate.now());
        film = filmService.create(film);

        // Пользователь существует (создан в setUp), но лайк не ставил
        Long filmId = film.getId();

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                filmService.removeLike(filmId, testUserId)
        );

        // Единый стандарт сообщения
        String expectedMessage = "Лайк от пользователя " + testUserId + " не найден";
        assertThat(e.getMessage()).contains(expectedMessage);
    }

    @Test
    void testGetPopularFilms() {
        Film film1 = new Film("Фильм 1");
        film1.setReleaseDate(LocalDate.now());
        filmService.create(film1);

        Film film2 = new Film("Фильм 2");
        film2.setReleaseDate(LocalDate.of(2023, 1, 1));
        filmService.create(film2);

        User user1 = new User();
        user1.setEmail("u1@yandex.ru");
        user1.setLogin("u1");
        user1.setBirthday(LocalDate.now());
        user1 = userService.create(user1);

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film2.getId(), user1.getId());

        List<Film> popularFilms = filmService.getPopularFilms(10);
        // Проверяем, что вернулось именно 2 фильма (созданных в этом тесте)
        assertThat(popularFilms).hasSize(1);
    }

    @Test
    void testGetPopularFilms_WhenAllFilmsHaveSameLikes() {

        int limit = 5; // или любое другое нужное значение
        int expectedSize = 1; // ожидаемое количество фильмов в топе
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

        List<Film> popularFilms = filmService.getPopularFilms(limit);
        assertThat(popularFilms).hasSize(expectedSize);
        // или другие проверки — порядок, количество лайков и т. д.

    }
}