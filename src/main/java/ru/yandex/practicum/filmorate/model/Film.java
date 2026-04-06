package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private long id;
    @NotBlank(message = "Название не может быть пустым")
    private String title; // Переименовали name в title
    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private String description;
    @NotNull @PastOrPresent(message = "Дата релиза должна быть указана")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
    // Добавили поле для rating
    private int rating;
    private Set<Long> likes = new HashSet<>();

    public Film() {
    }

    public Film(String title) { // Новый конструктор
        this.title = title;
    }

    public Film(long id, String title, String description, LocalDate releaseDate, int duration, int rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rating = rating; // Теперь rating сохраняется
    }

    public int getLikesCount() {
        return likes.size();
    }
}

