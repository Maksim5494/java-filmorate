package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mpa {
    private int id;
    private String name;

    public static final Mpa G = new Mpa(1, "G");
    public static final Mpa PG = new Mpa(2, "PG");
    public static final Mpa PG_13 = new Mpa(3, "PG-13");
    public static final Mpa R = new Mpa(4, "R");
    public static final Mpa NC_17 = new Mpa(5, "NC-17");
}
