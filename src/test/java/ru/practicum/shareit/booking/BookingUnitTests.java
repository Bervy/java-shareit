package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.error.ExceptionDescriptions.*;

@ExtendWith(MockitoExtension.class)
class BookingUnitTests {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingService bookingService;

    private final static long ID_1 = 1L;
    private final static long ID_2 = 2L;
    private final static int FROM = 0;
    private final static int SIZE = 5;
    private final static LocalDateTime START = LocalDateTime.of(2022, 11, 5, 1, 1);
    private final static LocalDateTime START_LATER_END =
            LocalDateTime.of(2022, 11, 8, 1, 1);
    private final static LocalDateTime END = LocalDateTime.of(2022, 11, 7, 1, 1);
    private final static Sort SORT = Sort.by(Sort.Direction.DESC, "start");
    private final static Pageable PAGE = PageRequest.of(0, 5);
    private final static Pageable PAGE_SORT = PageRequest.of(0, 5, SORT);
    private BookingDto bookingDto = new BookingDto();
    private User owner = new User();
    private User booker = new User();
    private Item item = new Item();
    private BookingFullDto bookingFullDto = new BookingFullDto();
    private Booking booking = new Booking();
    private List<Booking> bookings;

    @BeforeEach
    public void init() {
        bookingDto = BookingDto.builder()
                .itemId(ID_1)
                .start(START)
                .end(END).build();
        owner = User.builder()
                .id(ID_1)
                .name("userName1")
                .email("userMail1@ya.ru").build();
        booker = User.builder()
                .id(ID_2)
                .name("userName2")
                .email("userMail1@ya.ru").build();
        item = Item.builder()
                .id(ID_1)
                .name("itemName1")
                .description("itemDescription1")
                .available(true)
                .owner(owner).build();
        bookingFullDto = BookingFullDto.builder()
                .id(ID_1)
                .start(START)
                .end(END)
                .booker(new BookingFullDto
                        .BookerDto(booker.getId(), booker.getName()))
                .item(new BookingFullDto
                        .BookingItemDto(item.getId(), item.getName()))
                .status(BookingStatus.WAITING)
                .build();
        booking = Booking.builder()
                .id(ID_1)
                .start(START)
                .end(END)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        bookings = Collections.singletonList(booking);
    }

    @Test
    void create_shouldCallRepository() {
        mockFindUserById2();
        mockFindItemById1();
        mockFromBookingFullDto();
        mockToBookingFullDto();
        when(bookingRepository.save(booking)).thenReturn(booking);

        Optional<BookingFullDto> createdBooking = bookingService.create(ID_2, bookingDto);

        assertTrue(createdBooking.isPresent());
        assertEquals(ID_1, createdBooking.get().getId());
        verify(bookingRepository,times(1)).save(booking);
    }

    @Test
    void confirmation_shouldCallRepository() {
        when(bookingRepository.findByIdAndItemOwnerId(ID_1, ID_1)).thenReturn(Optional.ofNullable(booking));
        mockToBookingFullDto();
        when(bookingRepository.save(booking)).thenReturn(booking);

        Optional<BookingFullDto> confirmedBooking = bookingService.confirmation(ID_1, ID_1, true);

        assertTrue(confirmedBooking.isPresent());
        assertEquals(ID_1, confirmedBooking.get().getId());
        verify(bookingRepository,times(1)).save(booking);
    }

    @Test
    void getAllByBooker_shouldCallRepository_StateCurrent() {
        mockFindUserById2();
        mockToBookingFullDto();
        when(bookingRepository.findByBookerAndDatesCurrent(
                Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookings, PAGE, bookings.size()));

        List<BookingFullDto> bookingsResult = bookingService.getAllByBooker(
                ID_2, BookingState.CURRENT.toString(), FROM, SIZE);

        Assertions.assertNotNull(bookingsResult);
        assertEquals(bookings.size(), bookingsResult.size());
        verify(bookingRepository,times(1))
                .findByBookerAndDatesCurrent(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));
    }

    @Test
    void getAllByBooker_shouldCallRepository_StatePast() {
        mockFindUserById2();
        mockToBookingFullDto();
        when(bookingRepository.findByBookerAndDatesPast(
                Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookings, PAGE, bookings.size()));

        List<BookingFullDto> bookingsResult = bookingService.getAllByBooker(
                ID_2, BookingState.PAST.toString(), FROM, SIZE);

        Assertions.assertNotNull(bookingsResult);
        assertEquals(bookings.size(), bookingsResult.size());
        verify(bookingRepository,times(1))
                .findByBookerAndDatesPast(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));
    }

    @Test
    void getAllByBooker_shouldCallRepository_StateCurrentRejected() {
        mockFindUserById2();
        mockToBookingFullDto();
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                ID_2, BookingStatus.REJECTED, PageRequest.of(FROM / SIZE, SIZE, SORT)))
                .thenReturn(new PageImpl<>(bookings, PAGE, bookings.size()));

        List<BookingFullDto> bookingsResult = bookingService.getAllByBooker(
                ID_2, BookingState.REJECTED.toString(), FROM, SIZE);

        Assertions.assertNotNull(bookingsResult);
        assertEquals(bookings.size(), bookingsResult.size());
        verify(bookingRepository,times(1))
                .findAllByBookerIdAndStatusOrderByStartDesc(ID_2, BookingStatus.REJECTED,
                        PageRequest.of(FROM / SIZE, SIZE, SORT));
    }

    @Test
    void getAllByBooker_shouldCallRepository_StateCurrentWaiting() {
        mockFindUserById2();
        mockToBookingFullDto();
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                ID_2, BookingStatus.WAITING, PageRequest.of(FROM / SIZE, SIZE, SORT)))
                .thenReturn(new PageImpl<>(bookings, PAGE_SORT, bookings.size()));

        List<BookingFullDto> bookingsResult = bookingService.getAllByBooker(
                ID_2, BookingState.WAITING.toString(), FROM, SIZE);

        Assertions.assertNotNull(bookingsResult);
        assertEquals(bookings.size(), bookingsResult.size());
        verify(bookingRepository,times(1))
                .findAllByBookerIdAndStatusOrderByStartDesc(ID_2, BookingStatus.WAITING,
                        PageRequest.of(FROM / SIZE, SIZE, SORT));
    }

    @Test
    void getAllByBooker_shouldCallRepository_StateCurrentFuture() {
        mockFindUserById2();
        mockToBookingFullDto();
        when(bookingRepository.findByBookerAndDatesFuture(
                Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookings, PAGE_SORT, bookings.size()));

        List<BookingFullDto> bookingsResult = bookingService.getAllByBooker(
                ID_2, BookingState.FUTURE.toString(), FROM, SIZE);

        Assertions.assertNotNull(bookingsResult);
        assertEquals(bookings.size(), bookingsResult.size());
        verify(bookingRepository,times(1))
                .findByBookerAndDatesFuture(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));
    }

    @Test
    void getAllByBooker_shouldCallRepository_StateCurrentAll() {
        mockFindUserById2();
        mockToBookingFullDto();
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(
                Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookings, PAGE_SORT, bookings.size()));

        List<BookingFullDto> bookingsResult = bookingService.getAllByBooker(
                ID_2, BookingState.ALL.toString(), FROM, SIZE);

        Assertions.assertNotNull(bookingsResult);
        assertEquals(bookings.size(), bookingsResult.size());
        verify(bookingRepository,times(1))
                .findAllByBookerIdOrderByStartDesc(Mockito.anyLong(), Mockito.any(Pageable.class));
    }

    @Test
    void getAllByOwner_shouldCallRepository_StateCurrent() {
        mockFindUserById1();
        mockToBookingFullDto();
        when(bookingRepository.findByOwnerAndDatesCurrent(
                Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookings, PAGE, bookings.size()));

        List<BookingFullDto> bookingsResult = bookingService.getAllByOwner(
                ID_1, BookingState.CURRENT.toString(), FROM, SIZE);

        Assertions.assertNotNull(bookingsResult);
        assertEquals(bookings.size(), bookingsResult.size());
        verify(bookingRepository,times(1))
                .findByOwnerAndDatesCurrent(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));
    }

    @Test
    void getAllByOwner_shouldCallRepository_StatePast() {
        mockFindUserById1();
        mockToBookingFullDto();
        when(bookingRepository.findByOwnerAndDatesPast(
                Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookings, PAGE, bookings.size()));

        List<BookingFullDto> bookingsResult = bookingService.getAllByOwner(
                ID_1, BookingState.PAST.toString(), FROM, SIZE);

        Assertions.assertNotNull(bookingsResult);
        assertEquals(bookings.size(), bookingsResult.size());
        verify(bookingRepository,times(1))
                .findByOwnerAndDatesPast(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));
    }

    @Test
    void getAllByOwner_shouldCallRepository_StateCurrentRejected() {
        mockFindUserById1();
        mockToBookingFullDto();
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                ID_1, BookingStatus.REJECTED, PAGE_SORT))
                .thenReturn(new PageImpl<>(bookings, PageRequest.of(FROM / SIZE, SIZE), bookings.size()));

        List<BookingFullDto> bookingsResult = bookingService.getAllByOwner(
                ID_1, BookingState.REJECTED.toString(), FROM, SIZE);

        Assertions.assertNotNull(bookingsResult);
        assertEquals(bookings.size(), bookingsResult.size());
        verify(bookingRepository,times(1))
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(ID_1, BookingStatus.REJECTED, PAGE_SORT);
    }

    @Test
    void getAllByOwner_shouldCallRepository_StateCurrentWaiting() {
        mockFindUserById1();
        mockToBookingFullDto();
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                ID_1, BookingStatus.WAITING, PAGE_SORT))
                .thenReturn(new PageImpl<>(bookings, PageRequest.of(FROM / SIZE, SIZE, SORT), bookings.size()));

        List<BookingFullDto> bookingsResult = bookingService.getAllByOwner(
                ID_1, BookingState.WAITING.toString(), FROM, SIZE);

        Assertions.assertNotNull(bookingsResult);
        assertEquals(bookings.size(), bookingsResult.size());
        verify(bookingRepository,times(1))
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(ID_1, BookingStatus.WAITING, PAGE_SORT);
    }

    @Test
    void getAllByOwner_shouldCallRepository_StateCurrentFuture() {
        mockFindUserById1();
        mockToBookingFullDto();
        when(bookingRepository.findByOwnerAndDatesFuture(
                Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookings, PAGE_SORT, bookings.size()));

        List<BookingFullDto> bookingsResult = bookingService.getAllByOwner(
                ID_1, BookingState.FUTURE.toString(), FROM, SIZE);

        Assertions.assertNotNull(bookingsResult);
        assertEquals(bookings.size(), bookingsResult.size());
        verify(bookingRepository,times(1))
                .findByOwnerAndDatesFuture(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));
    }

    @Test
    void getAllByOwner_shouldCallRepository_StateCurrentAll() {
        mockFindUserById1();
        mockToBookingFullDto();
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
                Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(bookings, PAGE_SORT, bookings.size()));

        List<BookingFullDto> bookingsResult = bookingService.getAllByOwner(
                ID_1, BookingState.ALL.toString(), FROM, SIZE);

        Assertions.assertNotNull(bookingsResult);
        assertEquals(bookings.size(), bookingsResult.size());
        verify(bookingRepository,times(1))
                .findAllByItemOwnerIdOrderByStartDesc(Mockito.anyLong(), Mockito.any(Pageable.class));
    }

    @Test
    void create_shouldThrowValidationException_BookingStartAfterEnd() throws ValidationException {
        bookingDto.setStart(START_LATER_END);

        Exception exception = assertThrows(ValidationException.class, () -> bookingService.create(ID_2, bookingDto));

        assertEquals(BOOKING_START_DATE_LATER_END_DATE.getTitle(), exception.getMessage());
    }

    @Test
    void create_shouldThrowValidationException_ItemUnavailable() throws ValidationException {
        item.setAvailable(false);
        when(itemRepository.findById(ID_1)).thenReturn(Optional.ofNullable(item));

        Exception exception = assertThrows(ValidationException.class, () -> bookingService.create(ID_2, bookingDto));

        assertEquals(ITEM_UNAVAILABLE.getTitle(), exception.getMessage());
    }

    @Test
    void create_shouldThrowNotFoundException_UserReserveOwnItem() throws ValidationException {
        when(itemRepository.findById(ID_1)).thenReturn(Optional.ofNullable(item));

        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.create(ID_1, bookingDto));

        assertEquals(USER_RESERVE_OWN_ITEM.getTitle(), exception.getMessage());
    }

    @Test
    void confirmation_shouldThrowNotFoundException_BookingNotFound() throws ValidationException {
        when(bookingRepository.findByIdAndItemOwnerId(ID_1, ID_1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.confirmation(ID_1, ID_1, true));

        assertEquals(BOOKING_NOT_FOUND.getTitle(), exception.getMessage());
    }

    @Test
    void confirmation_shouldThrowValidationException_BookingAlreadyConfirmed() throws ValidationException {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findByIdAndItemOwnerId(ID_1, ID_1)).thenReturn(Optional.ofNullable(booking));

        Exception exception = assertThrows(ValidationException.class, () ->
                bookingService.confirmation(ID_1, ID_1, true));

        assertEquals(BOOKING_ALREADY_CONFIRMED.getTitle(), exception.getMessage());
    }

    @Test
    void getByIdAndBookerOrOwner_shouldThrowNotFoundException_BookingNotFound() throws ValidationException {
        when(bookingRepository.findByIdAndBookerOrOwner(ID_1, ID_2)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () ->
                bookingService.getByIdAndBookerOrOwner(ID_2, ID_1));

        assertEquals(BOOKING_NOT_FOUND.getTitle(), exception.getMessage());
    }

    @Test
    void getAllByBooker_shouldThrowValidationException_UnknownState() throws ValidationException {
        Exception exception = assertThrows(ValidationException.class, () ->
                bookingService.getAllByBooker(ID_2, "123", FROM / SIZE, SIZE));

        assertEquals(UNKNOWN_STATE.getTitle(), exception.getMessage());
    }

    @Test
    void getAllByBooker_shouldThrowValidationException_FromOrSizeLessThanZero() throws ValidationException {
        Exception exception = assertThrows(ValidationException.class, () ->
                bookingService.getAllByBooker(ID_2, "CURRENT", -1, SIZE));

        assertEquals(FROM_OR_SIZE_LESS_THAN_ZERO.getTitle(), exception.getMessage());

        Exception exception1 = assertThrows(ValidationException.class, () ->
                bookingService.getAllByBooker(ID_2, "CURRENT", FROM / SIZE, -1));

        assertEquals(FROM_OR_SIZE_LESS_THAN_ZERO.getTitle(), exception1.getMessage());
    }

    @Test
    void getAllByBooker_shouldThrowValidationException_UserNotFound() throws ValidationException {
        when(userRepository.findById(ID_2)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () ->
                bookingService.getAllByBooker(ID_2, "CURRENT", FROM / SIZE, SIZE));

        assertEquals(USER_NOT_FOUND.getTitle(), exception.getMessage());
    }

    private void mockFindUserById1() {
        when(userRepository.findById(ID_1)).thenReturn(Optional.ofNullable(owner));
    }

    private void mockFindUserById2() {
        when(userRepository.findById(ID_2)).thenReturn(Optional.ofNullable(booker));
    }

    private void mockFindItemById1() {
        when(itemRepository.findById(ID_1)).thenReturn(Optional.ofNullable(item));
    }

    private void mockToBookingFullDto() {
        when(bookingMapper.toBookingFullDto(booking)).thenReturn(bookingFullDto);
    }

    private void mockFromBookingFullDto() {
        when(bookingMapper.fromBookingDto(bookingDto, item, booker, BookingStatus.WAITING)).thenReturn(booking);
    }
}