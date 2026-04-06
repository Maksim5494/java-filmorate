package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import jakarta.validation.Validator;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@SpringBootTest
class UserValidationTest {

    @Autowired
    private Validator validator;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired // Добавляем @Autowired для userService
    private UserService userService;

    private MockMvc mockMvc;

    @Autowired
    private FilmService filmService;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testUserValidation() {
        User user = new User();
        user.setLogin("");
        user.setEmail("invalid_email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testAddFriendWithUnknownUser() throws Exception {
        long existingUserId = 1L;
        long unknownFriendId = 999L;

        ResultActions result = mockMvc.perform(
                put("/users/" + existingUserId + "/friends/" + unknownFriendId)
        );
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetFavoriteFilms() {
        // Подготавливаем пользователя
        User user = new User();
        user.setEmail("test@yandex.ru");
        user.setLogin("test_user");
        user.setBirthday(LocalDate.now().minusYears(20));

        // Сохраняем пользователя
        User savedUser = userService.create(user);

        // Создаём и сохраняем два РАЗНЫХ фильма
        Film film1 = new Film(0L, "Фильм 1", "Описание 1", LocalDate.now(), 120, 8);
        Film film2 = new Film(1L, "Фильм 2", "Описание 2", LocalDate.now(), 90, 7);

        Film savedFilm1 = filmService.create(film1);
        Film savedFilm2 = filmService.create(film2);

// Добавляем разные фильмы в избранное
        userService.addToFavorites(savedUser.getId(), savedFilm1.getId());
        userService.addToFavorites(savedUser.getId(), savedFilm2.getId());


        // Получаем избранные фильмы
        Set<Film> favoriteFilms = userService.getFavoriteFilms(savedUser.getId());

        // Проверки
        assertNotNull(favoriteFilms, "Набор избранных фильмов не должен быть null");
        assertEquals(2, favoriteFilms.size(), "Количество избранных фильмов должно быть равным 2");
        assertTrue(favoriteFilms.contains(savedFilm1));
        assertTrue(favoriteFilms.contains(savedFilm2));
    }


}
