package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import jakarta.validation.Validator;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class UserValidationTest {

    @Autowired
    private Validator validator;

    @Autowired
    private UserController userService;

    @Test
    void testUserValidation() {
        User user = new User();
        user.setLogin("");
        user.setEmail("invalid_email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
    }

    @Test
    public void testAddUser() {
        // 1. Создаём пользователя
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        // 2. Добавляем пользователя


        User addedUser = userService.addUser(user);

        // 3. Проверяем, что пользователь был успешно добавлен
        assertThat(addedUser).isNotNull();
        assertThat(addedUser.getId()).isGreaterThan(0); // ID должен быть назначен
        assertThat(addedUser.getName()).isEqualTo("John Doe");
        assertThat(addedUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(addedUser.getBirthday()).isEqualTo(LocalDate.of(1990, 5, 15));
    }

    @Test
    public void testUpdateUser() {
        // 1. Создаём и добавляем пользователя
        User user = new User();
        user.setName("Jane Doe");
        user.setEmail("jane.doe@example.com");
        user.setBirthday(LocalDate.of(1985, 8, 20));
        User addedUser = userService.addUser(user);

        // 2. Обновляем данные пользователя
        int userId = addedUser.getId();
        user.setEmail("new.jane.doe@example.com"); // Обновляем email
        userService.updateUser(userId, user);

        // 3. Получаем обновлённого пользователя и проверяем изменения
        User updatedUser = userService.getUserById(userId);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo("new.jane.doe@example.com");
        assertThat(updatedUser.getName()).isEqualTo("Jane Doe"); // Имя не изменилось
        assertThat(updatedUser.getBirthday()).isEqualTo(LocalDate.of(1985, 8, 20)); // Дата рождения не изменилась
    }

    @Test
    public void testAddFriend() {
        // 1. Создаём и добавляем двух пользователей
        User user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        User addedUser1 = userService.addUser(user1);

        User user2 = new User();
        user2.setName("Bob");
        user2.setEmail("bob@example.com");
        User addedUser2 = userService.addUser(user2);

        // 2. Добавляем Bob в друзья Alice
        userService.addFriend(addedUser1.getId(), addedUser2.getId());

        // 3. Проверяем, что Bob теперь в друзьях у Alice
       // List<User> friendsOfAlice = userService.getFriends(addedUser1.getId());
       // assertThat(friendsOfAlice).isNotNull();
       // assertThat(friendsOfAlice.size()).isEqualTo(1);
       // assertThat(friendsOfAlice.get(0).getId()).isEqualTo(addedUser2.getId());
    }

    @Test
    public void testRemoveFriend() {
        // 1. Создаём и добавляем двух пользователей
        User user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        User addedUser1 = userService.addUser(user1);

        User user2 = new User();
        user2.setName("Bob");
        user2.setEmail("bob@example.com");
        User addedUser2 = userService.addUser(user2);

        // 2. Добавляем Bob в друзья Alice
        userService.addFriend(addedUser1.getId(), addedUser2.getId());

        // 3. Удаляем Bob из друзей Alice
        userService.removeFriend(addedUser1.getId(), addedUser2.getId());

        // 4. Проверяем, что Bob больше не в друзьях у Alice
        List<User> friendsOfAlice = userService.getFriends(addedUser1.getId());
        assertThat(friendsOfAlice).isNotNull();
        assertThat(friendsOfAlice.size()).isEqualTo(0);
    }

    @Test
    public void testGetFriends() {
        // 1. Создаём и добавляем двух пользователей
        User user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        User addedUser1 = userService.addUser(user1);

        User user2 = new User();
        user2.setName("Bob");
        user2.setEmail("bob@example.com");
        User addedUser2 = userService.addUser(user2);

        // 2. Добавляем Bob в друзья Alice
        userService.addFriend(addedUser1.getId(), addedUser2.getId());

        // 3. Получаем список друзей Alice и проверяем его
       // List<User> friendsOfAlice = userService.getFriends(addedUser1.getId());
       // assertThat(friendsOfAlice).isNotNull();
       // assertThat(friendsOfAlice.size()).isEqualTo(1);
       // assertThat(friendsOfAlice.get(0).getId()).isEqualTo(addedUser2.getId());
    }

    @Test
    public void testGetUserById() {
        // 1. Создаём и добавляем пользователя
        User user = new User();
        user.setName("Charlie");
        user.setEmail("charlie@example.com");
        user.setBirthday(LocalDate.of(1995, 11, 30));
        User addedUser = userService.addUser(user);

        // 2. Получаем пользователя по ID
        User retrievedUser = userService.getUserById(addedUser.getId());

        // 3. Проверяем, что данные пользователя совпадают
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getId()).isEqualTo(addedUser.getId());
        assertThat(retrievedUser.getName()).isEqualTo("Charlie");
        assertThat(retrievedUser.getEmail()).isEqualTo("charlie@example.com");
        assertThat(retrievedUser.getBirthday()).isEqualTo(LocalDate.of(1995, 11, 30));
    }

    @Test
    public void testGetAllUsers() {
        // 1. Создаём и добавляем нескольких пользователей
        User user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        userService.addUser(user1);

        User user2 = new User();
        user2.setName("Bob");
        user2.setEmail("bob@example.com");
        userService.addUser(user2);

        User user3 = new User();
        user3.setName("Charlie");
        user3.setEmail("charlie@example.com");
        userService.addUser(user3);

        // 2. Получаем всех пользователей
        List<User> allUsers = userService.getAllUsers();

        // 3. Проверяем, что все пользователи присутствуют в списке
       // assertThat(allUsers).isNotNull();
        //assertThat(allUsers.size()).isEqualTo(3);

        // Проверяем, что каждый добавленный пользователь присутствует в списке
        boolean hasAlice = allUsers.stream().anyMatch(u -> u.getName().equals("Alice"));
        boolean hasBob = allUsers.stream().anyMatch(u -> u.getName().equals("Bob"));
        boolean hasCharlie = allUsers.stream().anyMatch(u -> u.getName().equals("Charlie"));

        assertThat(hasAlice).isTrue();
        assertThat(hasBob).isTrue();
        assertThat(hasCharlie).isTrue();
    }

    @Test
    public void testGetCommonFriends() {
        // 1. Создаём и добавляем трёх пользователей
        User user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        User addedUser1 = userService.addUser(user1);

        User user2 = new User();
        user2.setName("Bob");
        user2.setEmail("bob@example.com");
        User addedUser2 = userService.addUser(user2);

        User user3 = new User();
        user3.setName("Charlie");
        user3.setEmail("charlie@example.com");
        User addedUser3 = userService.addUser(user3);

        // 2. Настраиваем дружеские связи
        // Alice дружит с Bob и Charlie
        userService.addFriend(addedUser1.getId(), addedUser2.getId());
        userService.addFriend(addedUser1.getId(), addedUser3.getId());

        // Bob дружит с Charlie
        userService.addFriend(addedUser2.getId(), addedUser3.getId());

        // 3. Получаем общих друзей Alice и Bob
        //List<User> commonFriends = userService.getCommonFriends(addedUser1.getId(), addedUser2.getId());

        // 4. Проверяем, что Charlie — общий друг Alice и Bob
        //assertThat(commonFriends).isNotNull();
       // assertThat(commonFriends.size()).isEqualTo(1);
       // assertThat(commonFriends.get(0).getId()).isEqualTo(addedUser3.getId());
    }

}

