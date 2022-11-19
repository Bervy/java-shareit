package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.error.ExceptionDescriptions.*;

@Service
@RequiredArgsConstructor
public class BookingService implements BookingController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Optional<BookingFullDto> create(Long bookerId, BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException(BOOKING_START_DATE_LATER_END_DATE.getTitle());
        }

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND.getTitle()));

        if (!item.isAvailable()) {
            throw new ValidationException(ITEM_UNAVAILABLE.getTitle());
        }
        if (item.getOwner().getId() == bookerId) {
            throw new NotFoundException(USER_RESERVE_OWN_ITEM.getTitle());
        }

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getTitle()));
        return Optional.ofNullable(BookingMapper.toBookingFullDto(bookingRepository.save(
                BookingMapper.fromBookingDto(bookingDto, item, booker, BookingStatus.WAITING))));
    }

    @Override
    public Optional<BookingFullDto> confirmation(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId, ownerId).orElseThrow(
                () -> new NotFoundException(BOOKING_NOT_FOUND.getTitle()));

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException(BOOKING_ALREADY_CONFIRMED.getTitle());
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return Optional.ofNullable(BookingMapper.toBookingFullDto(bookingRepository.save(booking)));
    }

    @Override
    public Optional<BookingFullDto> getByIdAndBookerOrOwner(Long userId, Long bookingId) {
        return Optional.ofNullable(BookingMapper.toBookingFullDto(
                bookingRepository.findByIdAndBookerOrOwner(bookingId, userId)
                        .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND.getTitle()))));
    }

    @Override
    public List<BookingFullDto> getAllByBooker(Long bookerId, String stateBooking) {
        BookingState state = getValidState(stateBooking);
        validationUser(bookerId);
        return getBookings(false, state, bookerId).stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingFullDto> getAllByOwner(Long ownerId, String stateBooking) {
        BookingState state = getValidState(stateBooking);
        validationUser(ownerId);
        return getBookings(true, state, ownerId).stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());
    }

    private BookingState getValidState(String stateBooking) {
        BookingState state;
        try {
            state = BookingState.valueOf(stateBooking);
            return state;
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void validationUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(USER_NOT_FOUND.getTitle());
        }
    }

    private List<Booking> getBookings(boolean isOwner, BookingState state, Long userId) {
        List<Booking> bookings;
        switch (state) {
            case CURRENT:
                bookings = isOwner ? bookingRepository.findByOwnerAndDatesCurrent(userId, LocalDateTime.now()) :
                        bookingRepository.findByBookerAndDatesCurrent(userId, LocalDateTime.now());
                break;
            case PAST:
                bookings = isOwner ? bookingRepository.findByOwnerAndDatesPast(userId, LocalDateTime.now()) :
                        bookingRepository.findByBookerAndDatesPast(userId, LocalDateTime.now());
                break;
            case REJECTED:
                bookings = isOwner ? bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.REJECTED) :
                        bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                                BookingStatus.REJECTED);
                break;
            case WAITING:
                bookings = isOwner ? bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.WAITING) :
                        bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                                BookingStatus.WAITING);
                break;
            case FUTURE:
                bookings = isOwner ? bookingRepository.findByOwnerAndDatesFuture(userId, LocalDateTime.now()) :
                        bookingRepository.findByBookerAndDatesFuture(userId, LocalDateTime.now());
                break;
            default:
                bookings = isOwner ? bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId) :
                        bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        }
        return bookings;
    }
}