package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public static BookingFullDto toBookingFullDto(Booking booking) {
        return BookingFullDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(new BookingFullDto
                        .BookerDto(booking.getBooker().getId(), booking.getBooker().getName()))
                .item(new BookingFullDto
                        .BookingItemDto(booking.getItem().getId(), booking.getItem().getName()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .itemId(booking.getItem().getId())
                .build();
    }

    public static Booking fromBookingDto(BookingDto dto, Item item, User booker, BookingStatus status) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .booker(booker)
                .item(item)
                .status(status)
                .build();
    }
}