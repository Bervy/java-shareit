package ru.practicum.shareit.booking.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingControllerImpl implements BookingController {

    private final BookingService service;

    @Override
    @PostMapping
    public Optional<BookingFullDto> create(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestBody BookingDto bookingDto) {
        return service.create(bookerId, bookingDto);
    }

    @Override
    @PatchMapping("/{bookingId}")
    public Optional<BookingFullDto> confirmation(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam(value = "approved") boolean approved) {
        return service.confirmation(ownerId, bookingId, approved);
    }

    @Override
    @GetMapping("/{bookingId}")
    public Optional<BookingFullDto> getByIdAndBookerOrOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        return service.getByIdAndBookerOrOwner(userId, bookingId);
    }

    @Override
    @GetMapping
    public List<BookingFullDto> getAllByBooker(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(value = "from", required = false, defaultValue = "0")
            int from,
            @RequestParam(value = "size", required = false, defaultValue = "10")
            int size) {
        return service.getAllByBooker(bookerId, state, from, size);
    }

    @Override
    @GetMapping("/owner")
    public List<BookingFullDto> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(value = "from", required = false, defaultValue = "0")
            int from,
            @RequestParam(value = "size", required = false, defaultValue = "10")
            int size) {
        return service.getAllByOwner(bookerId, state, from, size);
    }
}