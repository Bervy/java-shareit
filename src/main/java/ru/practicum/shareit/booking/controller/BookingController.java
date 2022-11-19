package ru.practicum.shareit.booking.controller;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;

import java.util.List;
import java.util.Optional;

public interface BookingController {

    Optional<BookingFullDto> create(Long bookerId, BookingDto bookingDto);

    Optional<BookingFullDto> confirmation(Long ownerItemId, Long bookingId, boolean approved);

    Optional<BookingFullDto> getByIdAndBookerOrOwner(Long userId, Long bookingId);

    List<BookingFullDto> getAllByBooker(Long bookerId, String state);

    List<BookingFullDto> getAllByOwner(Long bookerId, String state);
}