package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    //private final RowMapper<Film> filmMapper = new RowMapper<>() {
    private final RowMapper<Film> filmMapper = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        // Обязательно для Postman: объект MPA с id и name
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);

        return film;
    };

    /*@Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());
        return film;
    }*/
    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());

        // Сохраняем жанры в таблицу film_genres
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public void updateFilm(int id, Film film) {
        if (!exists(id)) {
            throw new NotFoundException("Film with id " + id + " not found");
        }

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                id);
        film.setId(id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    private Film fillLikes(Film film) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Integer> likes = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getInt("user_id"),
                film.getId());
        film.setLikes(new java.util.HashSet<>(likes));
        return film;
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    /*@Override
    public Film getFilmById(int id) {
        try {
            String sql = "SELECT id, name, description, release_date, duration FROM films WHERE id = ?";
            Film film = jdbcTemplate.queryForObject(sql, filmMapper, id);
            return fillLikes(film);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Film with id " + id + " not found");
        }
    }*/
    @Override
    public Film getFilmById(int id) {
        try {
            // Добавляем JOIN mpa_ratings, чтобы получить имя рейтинга (G, PG и т.д.)
            String sql = "SELECT f.*, m.name AS mpa_name FROM films f " +
                    "JOIN mpa_ratings m ON f.mpa_id = m.id WHERE f.id = ?";
            Film film = jdbcTemplate.queryForObject(sql, filmMapper, id);
            fillGenres(film); // Метод для загрузки жанров
            return fillLikes(film);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Film with id " + id + " not found");
        }
    }

    // Добавь метод для загрузки жанров (Postman требует список объектов)
    private void fillGenres(Film film) {
        String sql = "SELECT g.id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ? ORDER BY g.id";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("id"), rs.getString("name")), film.getId());
        film.setGenres(new LinkedHashSet<>(genres));
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT id, name, description, release_date, duration FROM films";
        List<Film> films = jdbcTemplate.query(sql, filmMapper);
        films.forEach(this::fillLikes);
        return films;
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String sql = """
            SELECT f.id, f.name, f.description, f.release_date, f.duration
            FROM films f
            LEFT JOIN likes l ON f.id = l.film_id
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration
            ORDER BY COUNT(l.user_id) DESC, f.id
            LIMIT ?
            """;
        List<Film> films = jdbcTemplate.query(sql, filmMapper, count);
        films.forEach(this::fillLikes);
        return films;
    }

    @Override
    public void clearFilms() {
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM films");
    }

    @Override
    public boolean exists(int id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM films WHERE id = ?)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return exists != null && exists;
    }
}
