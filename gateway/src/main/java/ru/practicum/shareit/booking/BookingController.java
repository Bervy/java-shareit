package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.error.ExceptionDescriptions.BOOKING_START_DATE_LATER_END_DATE;
import static ru.practicum.shareit.error.ExceptionDescriptions.UNKNOWN_STATE;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestBody @Valid BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException(BOOKING_START_DATE_LATER_END_DATE.getTitle());
        }
        return bookingClient.create(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmation(
            @Positive @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @Positive @PathVariable Long bookingId,
            @RequestParam(name = "approved") boolean approved) {
        return bookingClient.confirmation(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getByIdAndBookerOrOwner(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @Positive @PathVariable Long bookingId) {
        return bookingClient.getByIdAndBookerOrOwner(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(
            @Positive @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0")
            int from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10")
            int size) {
        BookingState bookingState = getValidState(state);
        return bookingClient.getAllByBooker(bookerId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0")
            int from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10")
            int size) {
        BookingState bookingState = getValidState(state);
        return bookingClient.getAllByOwner(bookerId, bookingState, from, size);
    }

    private BookingState getValidState(String stateBooking) {
        BookingState state;
        try {
            state = BookingState.valueOf(stateBooking);
            return state;
        } catch (IllegalArgumentException e) {
            throw new ValidationException(UNKNOWN_STATE.getTitle());
        }
    }
}