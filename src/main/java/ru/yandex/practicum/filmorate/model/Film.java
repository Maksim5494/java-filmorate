package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class Film {
    private int id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    private Set<Integer> likes = new HashSet<>();

    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    private Mpa mpa;

    public int getLikesCount() {
        return likes == null ? 0 : likes.size();
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = new LinkedHashSet<>(genres);
    }
}