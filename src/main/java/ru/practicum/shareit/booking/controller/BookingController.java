package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    public Optional<BookingFullDto> create(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestBody @Valid BookingDto bookingDto) {
        return service.create(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Optional<BookingFullDto> confirmation(
            @RequestHeader("X-Sharer-User-Id") Long ownerItemId,
            @PathVariable Long bookingId,
            @RequestParam(value = "approved") Boolean approved) {
        return service.confirmation(ownerItemId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Optional<BookingFullDto> getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        return service.getByIdAndBookerOrOwner(userId, bookingId);
    }

    @GetMapping()
    public List<BookingFullDto> getAllByBooker(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        return service.getAllByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingFullDto> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        return service.getAllByOwner(bookerId, state);
    }
}