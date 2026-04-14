package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    void testFindUserById() {
        User newUser = new User(0, "user@email.ru", "vladimir", "vladimir", LocalDate.of(1990, 1, 1));
        User savedUser = userStorage.addUser(newUser);

        User user = userStorage.getUserById(savedUser.getId());

        assertThat(user)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", savedUser.getId())
                .hasFieldOrPropertyWithValue("email", "user@email.ru")
                .hasFieldOrPropertyWithValue("login", "vladimir")
                .hasFieldOrPropertyWithValue("name", "vladimir")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 1, 1));
    }

    @Test
    void testUpdateUser() {
        User user = new User(0, "old@email.ru", "old", "old", LocalDate.of(1990, 1, 1));
        User savedUser = userStorage.addUser(user);

        User updatedUser = new User(savedUser.getId(), "new@email.ru", "new", "new", LocalDate.of(1995, 5, 5));
        userStorage.updateUser(savedUser.getId(), updatedUser);

        User retrievedUser = userStorage.getUserById(savedUser.getId());

        assertThat(retrievedUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", savedUser.getId())
                .hasFieldOrPropertyWithValue("email", "new@email.ru")
                .hasFieldOrPropertyWithValue("login", "new")
                .hasFieldOrPropertyWithValue("name", "new")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1995, 5, 5));
    }

    @Test
    void testFindNonexistentUser() {
        User user = userStorage.getUserById(-1);
        assertThat(user).isNull();
    }

    @Test
    void testUniqueEmail() {
        User existingUser = new User(0, "unique@email.ru", "user", "user", LocalDate.of(1990, 1, 1));
        userStorage.addUser(existingUser);

        User newUserWithSameEmail = new User(0, "unique@email.ru", "another", "another", LocalDate.of(1995, 5, 5));

        Throwable exception = catchThrowable(() -> userStorage.addUser(newUserWithSameEmail));

        assertThat(exception).isNotNull();
    }

}