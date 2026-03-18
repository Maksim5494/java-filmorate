package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;

@Data
public class User {
    private int id;
    @NotNull
    @Email
    private String email;
    @NotNull
    private String login;
    private String name;
    private LocalDate birthday;
}