package ru.yandex.practicum.filmorate;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
public class FilmValidationTest {

    @Autowired
    private Validator validator;

    @Test
    public void testFilmValidation() {
        Film film = new Film();
        film.setName("");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertThat(violations).isNotEmpty();
    }
}

