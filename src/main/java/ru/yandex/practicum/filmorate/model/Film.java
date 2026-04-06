package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    @Min(value = 1, message = "Рейтинг не может быть меньше 1")
    @Max(value = 10, message = "Рейтинг не может превышать 10")
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

    @Data
    public class LikesInfo {
        private final int count;
        private final List<Long> userIds;

        public LikesInfo(int count, List<Long> userIds) {
            this.count = count;
            this.userIds = userIds;
        }
    }

    public LikesInfo getLikesInfo() {
        return new LikesInfo(
                this.likes.size(),
                new ArrayList<>(this.likes)
        );
    }

}

