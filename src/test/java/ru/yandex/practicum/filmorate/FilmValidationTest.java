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
import java.util.List;
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
        Film film = new Film();
        film.setName("Inception");
        film.setDuration(148);
        film.setReleaseDate(LocalDate.now());
        Film addedFilm = filmService.addFilm(film);

        filmService.addLike(addedFilm.getId(), 101);

        assertThat(filmService.getFilmById(addedFilm.getId()).getLikes()).contains(101);
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

    @Test
    public void testGetFilmById() {
        // 1. Добавляем фильм в систему
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        Film addedFilm = filmService.addFilm(film);

        // 2. Получаем фильм по ID
        Film retrievedFilm = filmService.getFilmById(addedFilm.getId());

        // 3. Проверяем, что возвращённый фильм соответствует добавленному
        assertThat(retrievedFilm).isNotNull();
        assertThat(retrievedFilm.getId()).isEqualTo(addedFilm.getId());
        assertThat(retrievedFilm.getName()).isEqualTo("Test Film");
        assertThat(retrievedFilm.getDescription()).isEqualTo("Description");
        assertThat(retrievedFilm.getDuration()).isEqualTo(120);
    }

    @Test
    public void testGetAllFilms() {
        // 1. Добавляем несколько фильмов
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

        // 2. Получаем список всех фильмов
        List<Film> allFilms = filmService.getAllFilms();

        // 3. Проверяем список
        assertThat(allFilms).isNotNull();
        assertThat(allFilms.size()).isEqualTo(2); // Проверяем количество
        assertThat(allFilms).contains(film1, film2); // Проверяем наличие обоих фильмов
    }

    @Test
    public void testGetTopFilms() {
        // 1. Создаём и добавляем фильмы
        Film film1 = new Film();
        film1.setName("Popular Film");
        film1.setDescription("Very popular");
        film1.setReleaseDate(LocalDate.now());
        film1.setDuration(100);
        Film addedFilm1 = filmService.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Less Popular Film");
        film2.setDescription("Not so popular");
        film2.setReleaseDate(LocalDate.now());
        film2.setDuration(90);
        Film addedFilm2 = filmService.addFilm(film2);

        // 2. Добавляем лайки — имитируем популярность
        filmService.addLike(addedFilm1.getId(), 101); // Добавляем лайк первому фильму
        filmService.addLike(addedFilm1.getId(), 102); // Второй лайк первому фильму

        filmService.addLike(addedFilm2.getId(), 103); // Один лайк второму фильму

        // 3. Получаем топ-фильмы
      //  List<Film> topFilms = filmService.getTopFilms(2); // Передаём число фильмов в топе


        // 4. Проверяем, что топ-список содержит фильмы в правильном порядке
       // assertThat(topFilms).isNotNull();
        //assertThat(topFilms.size()).isEqualTo(2); // Проверяем количество фильмов в топе
        //assertThat(topFilms.get(0).getId()).isEqualTo(addedFilm1.getId()); // Первый фильм в топе должен быть most popular
       // assertThat(topFilms.get(1).getId()).isEqualTo(addedFilm2.getId()); // Второй фильм в топе
    }

    @Test
    public void testGetTopFilmsWhenNoFilms() {
        // Очищаем все фильмы — гарантируем, что база пуста
        filmService.clearFilms(); // или другой метод очистки, если есть

        // Получаем топ‑фильмы
        List<Film> topFilms = filmService.getTopFilms(1);

        // Проверяем, что список пуст
        assertThat(topFilms).isEmpty();
    }

    @Test
    public void testAddLikeIncreasesLikesCount() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.of(2023, 1, 1)); // Устанавливаем дату релиза
        Film addedFilm = filmService.addFilm(film);

        filmService.addLike(addedFilm.getId(), 101);
        filmService.addLike(addedFilm.getId(), 102);

        Film updatedFilm = filmService.getFilmById(addedFilm.getId());
        assertThat(updatedFilm.getLikes()).hasSize(2); // Должно быть 2 лайка
    }

}
