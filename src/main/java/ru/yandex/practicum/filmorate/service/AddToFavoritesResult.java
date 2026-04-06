package ru.yandex.practicum.filmorate.service;

import lombok.Data;

@Data
public class AddToFavoritesResult {
    private boolean success;
    private String message;

    public AddToFavoritesResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}


