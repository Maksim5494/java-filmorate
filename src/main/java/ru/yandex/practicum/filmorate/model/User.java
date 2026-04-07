package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private long id;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    private String login;

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 100, message = "Длина имени должна быть от 2 до 100 символов")
    private String name;


    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    // Храним ID друзей
    private final Set<Long> friends = new HashSet<>();

    // Храним объекты фильмов
    private final Set<Film> favoriteFilms = new HashSet<>();
}

