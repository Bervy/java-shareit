package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@AllArgsConstructor
@Data
public class User {
    @EqualsAndHashCode.Exclude
    private long id;
    private String name;
    private String email;
}