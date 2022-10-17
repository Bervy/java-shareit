package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@AllArgsConstructor
@Data
public class ItemDto {
    @EqualsAndHashCode.Exclude
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long owner;
    private Long request;
}