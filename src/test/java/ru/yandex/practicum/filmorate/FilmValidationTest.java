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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FilmValidationTest {

    private MockMvc mockMvc;

    @Autowired
    private Validator validator;

    @Autowired
    private FilmService filmService;

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

        Set<Long> likeIds = new HashSet<>();
        likeIds.add(1L);
        likeIds.add(2L);

        Film film1 = new Film("Фильм 1");
        film1.setLikes(likeIds);
        filmService.create(film1);

        Film film2 = new Film("Фильм 2");
        film2.setLikes(Set.of(3L, 4L));
        filmService.create(film2);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

}