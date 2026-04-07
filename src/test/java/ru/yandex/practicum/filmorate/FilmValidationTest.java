package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FilmValidationTest {

    @Autowired
    private Validator validator;

    @Autowired
    private FilmService filmService;

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

        // Проверяем, что фильм создан и получил ID
        assertThat(addedFilm).isNotNull();
        assertThat(addedFilm.getId()).isGreaterThan(0);
    }



    @Test
    public void testUpdateFilm() {
        // Добавляем фильм
        Film originalFilm = new Film();
        originalFilm.setName("Original Film");
        originalFilm.setDescription("Original Description");
        originalFilm.setReleaseDate(LocalDate.now());
        originalFilm.setDuration(120);
        Film addedFilm = filmService.addFilm(originalFilm);

        // Обновляем фильм
        addedFilm.setName("Updated Film");
        addedFilm.setDescription("Updated Description");
        filmService.updateFilm(addedFilm.getId(), addedFilm);

        // Проверяем, что фильм обновлен
        Film updatedFilm = filmService.getFilmById(addedFilm.getId());
        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
    }


    @Test
    public void testAddLike() {
        // 1. Создаём фильм и настраиваем его
        Film film = new Film();
        film.setId(1); // задаём ID фильма

        // 2. Вызываем метод добавления лайка
        filmService.addLike(1, 101); // пользователь с ID 101 ставит лайк фильму с ID 1

        // 3. Проверяем, что лайк добавился
        assertThat(filmService.getFilmById(1).getLikes()).contains(101); // ожидаем, что 101 есть в наборе лайков

        // 4. Проверяем, что дубликаты не добавляются
        filmService.addLike(1, 101); // повторный лайк от того же пользователя
        assertThat(filmService.getFilmById(1).getLikes()).hasSize(1); // размер набора должен остаться 1
    }

    @Test
    public void testRemoveLike() {
        // 1. Создаём фильм и настраиваем его
        Film film = new Film();
        film.setId(1); // задаём ID фильма
        filmService.addLike(1, 101); // пользователь с ID 101 ставит лайк
        filmService.addLike(1, 102); // пользователь с ID 102 ставит лайк

        // 2. Вызываем метод удаления лайка
        filmService.removeLike(1, 101); // удаляем лайк от пользователя с ID 101

        // 3. Проверяем, что лайк удалён
        assertThat(filmService.getFilmById(1).getLikes()).doesNotContain(101); // ожидаем, что 101 нет в наборе лайков
        assertThat(filmService.getFilmById(1).getLikes()).contains(102); // ожидаем, что 102 остался в наборе лайков
    }

}
