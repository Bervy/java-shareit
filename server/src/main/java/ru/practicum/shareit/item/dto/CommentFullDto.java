package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentFullDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
