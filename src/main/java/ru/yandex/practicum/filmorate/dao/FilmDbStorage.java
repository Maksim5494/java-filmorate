package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Film> filmMapper = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);

        return film;
    };

    @Override
    public Film addFilm(Film film) {
        if (film.getMpa() == null) {
            film.setMpa(new Mpa(1, "G"));
        }

        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genreSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(genreSql, film.getId(), genre.getId());
            }
        }

        return film;
    }

    @Override
    public void updateFilm(int id, Film film) {
        if (!exists(id)) {
            throw new NotFoundException("Film not found");
        }

        Film oldFilm = getFilmById(id);

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : oldFilm.getMpa().getId(),
                id);

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", id);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", id, genre.getId());
            }
        } else if (oldFilm.getGenres() != null) {
            for (Genre genre : oldFilm.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", id, genre.getId());
            }
        }
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

    @Override
    public Film getFilmById(int id) {
        try {
            String sql = "SELECT f.*, m.name AS mpa_name FROM films f " +
                    "JOIN mpa m ON f.mpa_id = m.id WHERE f.id = ?";
            Film film = jdbcTemplate.queryForObject(sql, filmMapper, id);
            fillGenres(film);
            return fillLikes(film);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Film with id " + id + " not found");
        }
    }

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
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.id";
        List<Film> films = jdbcTemplate.query(sql, filmMapper);
        films.forEach(this::fillLikes);
        films.forEach(this::fillGenres);
        return films;
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String sql = """
        SELECT f.*, m.name AS mpa_name
        FROM films f
        JOIN mpa m ON f.mpa_id = m.id
        LEFT JOIN likes l ON f.id = l.film_id
        GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name
        ORDER BY COUNT(l.user_id) DESC, f.id
        LIMIT ?
        """;
        List<Film> films = jdbcTemplate.query(sql, filmMapper, count);
        films.forEach(this::fillLikes);
        films.forEach(this::fillGenres);
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
