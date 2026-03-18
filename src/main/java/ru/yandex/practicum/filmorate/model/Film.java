package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @org.jetbrains.annotations.NotNull
    private String name;
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    private int duration;
}