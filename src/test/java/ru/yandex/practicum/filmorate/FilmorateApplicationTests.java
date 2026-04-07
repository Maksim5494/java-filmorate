package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private FilmService filmService;

	@Test
	void shouldAddAndFindFilm() {
		//Film film = new Film("Тест-фильм", "Описание", LocalDate.now());
		Film film = new Film("Название фильма");
		film.setDescription("Описание");
		film.setReleaseDate(LocalDate.now());
		// и так далее — задать остальные поля вручную

		filmService.create(film);

		Film foundFilm = filmService.getById(film.getId());
		assertEquals(film.getTitle(), foundFilm.getTitle());
	}

	@Test void shouldThrowExceptionForNonexistentFilm() {
		assertThrows(NotFoundException.class, () -> filmService.getById(-1L)); }

}
