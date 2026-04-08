package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;

@Data
public class User {
    private int id;

    public int getId() {
        return id;
    }

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    private String login;

    @NotEmpty(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 50, message = "Длина имени должна быть от 2 до 50 символов")
    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public Collection<Object> getFriends() {
        return java.util.List.of();
    }
}