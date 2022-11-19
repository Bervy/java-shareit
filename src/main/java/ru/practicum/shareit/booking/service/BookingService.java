package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private static final String START_FIELD = "start";
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final Sort sort = Sort.by(Sort.Direction.DESC, START_FIELD);

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
        Booking savedBooking = bookingRepository.save(
                bookingMapper.fromBookingDto(bookingDto, item, booker, BookingStatus.WAITING));
        return Optional.ofNullable(bookingMapper.toBookingFullDto(savedBooking));
    }

    @Override
    public Optional<BookingFullDto> confirmation(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId, ownerId).orElseThrow(
                () -> new NotFoundException(BOOKING_NOT_FOUND.getTitle()));

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException(BOOKING_ALREADY_CONFIRMED.getTitle());
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);
        return Optional.ofNullable(bookingMapper.toBookingFullDto(savedBooking));
    }

    @Override
    public Optional<BookingFullDto> getByIdAndBookerOrOwner(Long userId, Long bookingId) {
        return Optional.ofNullable(bookingMapper.toBookingFullDto(
                bookingRepository.findByIdAndBookerOrOwner(bookingId, userId)
                        .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND.getTitle()))));
    }

    @Override
    public List<BookingFullDto> getAllByBooker(Long bookerId, String stateBooking, int from, int size) {
        validateFromAndSize(from, size);
        BookingState state = getValidState(stateBooking);
        validationUser(bookerId);
        return getBookings(false, state, bookerId, PageRequest.of(from / size, size, sort))
                .stream()
                .map(bookingMapper::toBookingFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingFullDto> getAllByOwner(Long ownerId, String stateBooking, int from, int size) {
        validateFromAndSize(from, size);
        BookingState state = getValidState(stateBooking);
        validationUser(ownerId);
        return getBookings(true, state, ownerId, PageRequest.of(from / size, size, sort))
                .stream()
                .map(bookingMapper::toBookingFullDto)
                .collect(Collectors.toList());
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

    private void validateFromAndSize(int from, int size) {
        if (from < 0 || size < 0) {
            throw new ValidationException(FROM_OR_SIZE_LESS_THAN_ZERO.getTitle());
        }
    }

    private void validationUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(USER_NOT_FOUND.getTitle());
        }
    }

    private Page<Booking> getBookings(boolean isOwner, BookingState state, Long userId, Pageable pageable) {
        Page<Booking> bookings;
        switch (state) {
            case CURRENT:
                bookings = isOwner ? bookingRepository
                        .findByOwnerAndDatesCurrent(userId, LocalDateTime.now(), pageable) :
                        bookingRepository.findByBookerAndDatesCurrent(userId, LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookings = isOwner ? bookingRepository.findByOwnerAndDatesPast(userId, LocalDateTime.now(), pageable) :
                        bookingRepository.findByBookerAndDatesPast(userId, LocalDateTime.now(), pageable);
                break;
            case REJECTED:
                bookings = isOwner ? bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.REJECTED, pageable) :
                        bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                                BookingStatus.REJECTED, pageable);
                break;
            case WAITING:
                bookings = isOwner ? bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.WAITING, pageable) :
                        bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                                BookingStatus.WAITING, pageable);
                break;
            case FUTURE:
                bookings = isOwner ? bookingRepository
                        .findByOwnerAndDatesFuture(userId, LocalDateTime.now(), pageable) :
                        bookingRepository.findByBookerAndDatesFuture(userId, LocalDateTime.now(), pageable);
                break;
            default:
                bookings = isOwner ? bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageable) :
                        bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable);
        }
        return bookings;
    }
}