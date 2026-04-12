package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@RequiredArgsConstructor
@Repository
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userMapper = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            var birthday = rs.getDate("birthday");
            if (birthday != null) {
                user.setBirthday(birthday.toLocalDate());
            }
            return user;
        }
    };

    public User addUser(User user) {
        String checkSql = "SELECT * FROM users WHERE email = ?";
        List<User> existingUsers = jdbcTemplate.query(checkSql, userMapper, user.getEmail());
        if (!existingUsers.isEmpty()) {
            throw new RuntimeException("User with this email already exists");
        }

        String insertSql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User updateUser(int id, User updatedUser) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                updatedUser.getName(),
                java.sql.Date.valueOf(updatedUser.getBirthday()),
                id);
        updatedUser.setId(id);
        return updatedUser;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users ORDER BY id";
        return jdbcTemplate.query(sql, userMapper);
    }

    @Override
    public void addFriend(int id, int friendId) {
        throw new UnsupportedOperationException("Friendship storage is not implemented for DB yet");
    }

    @Override
    public void removeFriend(int id, int friendId) {
        throw new UnsupportedOperationException("Friendship storage is not implemented for DB yet");
    }

    @Override
    public List<User> getFriends(int id) {
        throw new UnsupportedOperationException("Friendship storage is not implemented for DB yet");
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        throw new UnsupportedOperationException("Friendship storage is not implemented for DB yet");
    }

    @Override
    public void clearUsers() {
        jdbcTemplate.update("DELETE FROM users");
    }

    @Override
    public boolean exists(int id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE id = ?)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return exists != null && exists;
    }
}