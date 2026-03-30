package ru.yandex.practicum.filmorate.model;

public class ErrorResponse {
    private final String description;
    private final String detail;

    public ErrorResponse(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }

}

