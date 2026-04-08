package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import jakarta.validation.Validator;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserValidationTest {

    @Autowired
    private Validator validator;

    @Autowired
    private UserController userService;

    @Test
    void testUserValidation() {
        User user = new User();
        user.setLogin(" ");
        user.setEmail("invalid_email");
        user.setName("Jo");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setLogin("jane_doe");
        user.setName("Jane Doe");
        user.setEmail("jane.doe@example.com");
        user.setBirthday(LocalDate.of(1985, 8, 20));
        User addedUser = userService.addUser(user);

        addedUser.setEmail("new.jane.doe@example.com");
        userService.updateUser(addedUser);

        User updatedUser = userService.getUserById(addedUser.getId());
        assertThat(updatedUser.getEmail()).isEqualTo("new.jane.doe@example.com");
    }

    @Test
    public void testGetCommonFriends() {
        User u1 = createTestUser("Alice", "alice@test.com");
        User u2 = createTestUser("Bob", "bob@test.com");
        User u3 = createTestUser("Charlie", "charlie@test.com");

        userService.addFriend(u1.getId(), u3.getId());
        userService.addFriend(u2.getId(), u3.getId());

        List<User> common = userService.getCommonFriends(u1.getId(), u2.getId());

        assertThat(common).hasSize(1);
        assertThat(common.get(0).getName()).isEqualTo("Charlie");
    }

    private User createTestUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setLogin(name + "_login");
        user.setEmail(email);
        user.setBirthday(LocalDate.now().minusYears(20));
        return userService.addUser(user);
    }
}