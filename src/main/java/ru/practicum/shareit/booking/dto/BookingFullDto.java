package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingFullDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private BookerDto booker;
    private BookingItemDto item;

    @AllArgsConstructor
    @Getter
    public static class BookerDto {
        private Long id;
        private String name;
    }

    @AllArgsConstructor
    @Getter
    public static class BookingItemDto {
        private Long id;
        private String name;
    }
}