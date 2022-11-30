package ru.practicum.shareit.item.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
public class ItemShortDto {
    private Long id;
    private String name;
    private Long ownerId;
    private String description;
    private Boolean available;
    private Long requestId;
}