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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
        return film;
    };

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        updateGenres(film);
        return getFilmById(film.getId());
    }

    @Override
    public void updateFilm(int id, Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        int rows = jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), id);
        if (rows == 0) throw new NotFoundException("Фильм не найден");
        updateGenres(film);
    }

    private void updateGenres(Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            List<Object[]> batch = film.getGenres().stream()
                    .map(g -> new Object[]{film.getId(), g.getId()})
                    .collect(Collectors.toList());
            jdbcTemplate.batchUpdate(sql, batch);
        }
    }

    @Override
    public Film getFilmById(int id) {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.id WHERE f.id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, filmMapper, id);
            film.setGenres(new LinkedHashSet<>(getGenresByFilmId(id)));
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм не найден");
        }
    }

    private List<Genre> getGenresByFilmId(int id) {
        return jdbcTemplate.query("SELECT g.* FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.id",
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")), id);
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT f.*, m.name as mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.id", filmMapper);
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN likes l ON f.id = l.film_id GROUP BY f.id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
        return jdbcTemplate.query(sql, filmMapper, count);
    }

    @Override
    public boolean exists(int id) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM films WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    @Override public void clearFilms() { jdbcTemplate.update("DELETE FROM films"); }
    @Override public List<Genre> getAllGenres() { return jdbcTemplate.query("SELECT * FROM genres", (rs, n) -> new Genre(rs.getInt("id"), rs.getString("name"))); }
    @Override public Genre getGenreById(int id) {
        try { return jdbcTemplate.queryForObject("SELECT * FROM genres WHERE id = ?", (rs, n) -> new Genre(rs.getInt("id"), rs.getString("name")), id); }
        catch (EmptyResultDataAccessException e) { throw new NotFoundException("Жанр не найден"); }
    }
    @Override public List<Mpa> getAllMpa() { return jdbcTemplate.query("SELECT * FROM mpa", (rs, n) -> new Mpa(rs.getInt("id"), rs.getString("name"))); }
    @Override public Mpa getMpaById(int id) {
        try { return jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE id = ?", (rs, n) -> new Mpa(rs.getInt("id"), rs.getString("name")), id); }
        catch (EmptyResultDataAccessException e) { throw new NotFoundException("MPA не найден"); }
    }
}
