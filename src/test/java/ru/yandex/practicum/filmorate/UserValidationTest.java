package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.yandex.practicum.filmorate.model.User;
import jakarta.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Fail.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@SpringBootTest
class UserValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void testUserValidation() {
        User user = new User();
        user.setLogin("");
        user.setEmail("invalid_email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAddFriendWithUnknownUser() {
        long existingUserId = 1L;
        long unknownFriendId = 999L;

        try {
            ResultActions result = mockMvc.perform(
                    put("/users/" + existingUserId + "/friends/" + unknownFriendId)
            );
            result.andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace(); // выведет стектрейс ошибки
            fail("Test failed: " + e.getMessage());
        }
    }

}

