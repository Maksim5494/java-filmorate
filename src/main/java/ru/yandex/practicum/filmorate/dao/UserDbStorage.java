package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
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
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs), id);
        if (users.isEmpty()) {
            return null;
        }
        User user = users.get(0);
        user.setFriends(getFriends(user.getId()));
        return user;
    }

    @Override
    public User updateUser(int id, User updatedUser) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        int updatedRows = jdbcTemplate.update(sql,
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                updatedUser.getName(),
                id
        );

        if (updatedRows > 0) {
            updatedUser.setId(id);
            updatedUser.setFriends(getFriends(id));
            return updatedUser;
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users ORDER BY id";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs));
        return users.stream()
                .peek(user -> user.setFriends(getFriends(user.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public void addFriend(int id, int friendId) {
        String sql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, true)";
        jdbcTemplate.update(sql, id, friendId);

        // Подтверждаем дружбу (двунаправленная)
        sql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, true)";
        jdbcTemplate.update(sql, friendId, id);
    }

    @Override
    public void removeFriend(int id, int friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, id, friendId);
        jdbcTemplate.update(sql, friendId, id);
    }

    @Override
    public List<User> getFriends(int id) {
        String sql = """
            SELECT u.* FROM users u
            JOIN friendships f ON u.id = f.friend_id
            WHERE f.user_id = ? AND f.status = true
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs), id);
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        String sql = """
            SELECT u.* FROM users u
            WHERE u.id IN (
                SELECT f.friend_id FROM friendships f
                WHERE f.user_id = ? AND f.status = true
                INTERSECT
                SELECT f.friend_id FROM friendships f
                WHERE f.user_id = ? AND f.status = true
            )
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs), id, otherId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        removeFriend(userId, friendId);
    }

    @Override
    public void clearUsers() {
        String sql = "DELETE FROM users";
        jdbcTemplate.update(sql);
    }

    @Override
    public boolean exists(int id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }
}
