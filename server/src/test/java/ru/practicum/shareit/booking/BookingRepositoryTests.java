package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
 class BookingRepositoryTests {
    private static final long ID_1 = 1L;
    private static final long ID_2 = 2L;
    private final Pageable page = PageRequest.of(0, 5);
    private final LocalDateTime start = LocalDateTime.of(2022, 11, 5, 1, 1);
    private final LocalDateTime end = LocalDateTime.of(2022, 11, 7, 1, 1);
    private final User owner = User.builder()
            .name("userName1")
            .email("mail1@ya.ru").build();
    private final User booker = User.builder()
            .name("userName2")
            .email("mail2@ya.ru").build();
    private final Item item = Item.builder()
            .name("item1Name")
            .description("item1Description")
            .available(true)
            .owner(owner).build();
    private final Booking booking1 = Booking.builder()
            .start(start)
            .end(end)
            .booker(booker)
            .item(item)
            .status(BookingStatus.CANCELED)
            .build();
    private final Booking booking2 = Booking.builder()
            .start(start.plusHours(1))
            .end(end.plusHours(2))
            .booker(booker)
            .item(item)
            .status(BookingStatus.WAITING)
            .build();
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @BeforeEach
    void saveData() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
    }

    @Test
    void findAllByItemIdOrderByStartAsc_shouldReturnCollectionWithSize2Asc() {
        List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartAsc(ID_1);

        int expectedSize = 2;
        Optional<Booking> firstElement = bookings.stream().findFirst();
        assertNotNull(bookings);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, bookings.size());
        assertEquals(booking1.getId(), firstElement.get().getId());
    }

    @Test
    void findAllByItemIdOrderByStartDesc_shouldReturnCollectionWithSize2Desc() {
        List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartDesc(ID_1);

        int expectedSize = 2;
        Optional<Booking> firstElement = bookings.stream().findFirst();
        assertNotNull(bookings);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, bookings.size());
        assertEquals(booking2.getId(), firstElement.get().getId());
    }

    @Test
    void findAllByBookerIdOrderByStartDesc_shouldReturnCollectionWithSize2Desc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(ID_2, page)
                .stream().collect(Collectors.toList());

        int expectedSize = 2;
        Optional<Booking> firstElement = bookings.stream().findFirst();
        assertNotNull(bookings);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, bookings.size());
        assertEquals(booking2.getId(), firstElement.get().getId());
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc_shouldReturnCollectionWithSize1Desc() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(ID_2, BookingStatus.CANCELED, page)
                .stream().collect(Collectors.toList());

        int expectedSize = 1;
        Optional<Booking> firstElement = bookings.stream().findFirst();
        assertNotNull(bookings);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, bookings.size());
        assertEquals(booking1.getId(), firstElement.get().getId());
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc_shouldReturnCollectionWithSize2Desc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ID_1, page)
                .stream().collect(Collectors.toList());

        int expectedSize = 2;
        Optional<Booking> firstElement = bookings.stream().findFirst();
        assertNotNull(bookings);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, bookings.size());
        assertEquals(booking2.getId(), firstElement.get().getId());
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByStartDesc_shouldReturnCollectionWithSize1Desc() {
        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(ID_1, BookingStatus.CANCELED, page)
                .stream().collect(Collectors.toList());

        int expectedSize = 1;
        Optional<Booking> firstElement = bookings.stream().findFirst();
        assertNotNull(bookings);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, bookings.size());
        assertEquals(booking1.getId(), firstElement.get().getId());
    }

    @Test
    void findByOwnerAndDatesFuture_shouldReturnCollectionWithSize2() {
        List<Booking> bookings = bookingRepository.findByOwnerAndDatesFuture(ID_1, start.minusDays(1), page)
                .stream().collect(Collectors.toList());

        int expectedSize = 2;
        Optional<Booking> firstElement = bookings.stream().findFirst();
        assertNotNull(bookings);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, bookings.size());
        assertEquals(booking2.getId(), firstElement.get().getId());
    }

    @Test
    void findByOwnerAndDatesCurrent_shouldReturnCollectionWithSize1() {
        List<Booking> bookings = bookingRepository.findByOwnerAndDatesCurrent(ID_1, start.plusMinutes(30), page)
                .stream().collect(Collectors.toList());

        int expectedSize = 1;
        Optional<Booking> firstElement = bookings.stream().findFirst();
        assertNotNull(bookings);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, bookings.size());
        assertEquals(booking1.getId(), firstElement.get().getId());
    }

    @Test
    void findByOwnerAndDatesPast_shouldReturnCollectionWithSize2() {
        List<Booking> bookings = bookingRepository.findByOwnerAndDatesPast(ID_1, end.plusHours(8), page)
                .stream().collect(Collectors.toList());

        int expectedSize = 2;
        Optional<Booking> firstElement = bookings.stream().findFirst();
        assertNotNull(bookings);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, bookings.size());
        assertEquals(booking2.getId(), firstElement.get().getId());
    }

    @Test
    void findAllByBookerId_shouldReturnCollectionWithSize2() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerId(ID_2);

        int expectedSize = 2;
        Optional<Booking> firstElement = bookings.stream().findFirst();
        assertNotNull(bookings);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, bookings.size());
        assertEquals(booking2.getId(), firstElement.get().getId());
    }

    @Test
    void findByBookerAndDatesFuture_shouldReturnCollectionWithSize2() {
        List<Booking> bookings = bookingRepository.findByBookerAndDatesFuture(ID_2, start.minusDays(1), page)
                .stream().collect(Collectors.toList());

        int expectedSize = 2;
        Optional<Booking> firstElement = bookings.stream().findFirst();
        assertNotNull(bookings);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, bookings.size());
        assertEquals(booking2.getId(), firstElement.get().getId());
    }

    @Test
    void findByBookerAndDatesCurrent_shouldReturnCollectionWithSize1() {
        List<Booking> bookings = bookingRepository.findByBookerAndDatesCurrent(ID_2, start.plusMinutes(30), page)
                .stream().collect(Collectors.toList());
        int expectedSize = 1;
        Optional<Booking> firstElement = bookings.stream().findFirst();
        assertNotNull(bookings);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, bookings.size());
        assertEquals(booking1.getId(), firstElement.get().getId());
    }

    @Test
    void findByBookerAndDatesPast_shouldReturnCollectionWithSize2() {
        List<Booking> bookings = bookingRepository.findByBookerAndDatesPast(ID_2, end.plusHours(8), page)
                .stream().collect(Collectors.toList());

        int expectedSize = 2;
        Optional<Booking> firstElement = bookings.stream().findFirst();
        assertNotNull(bookings);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, bookings.size());
        assertEquals(booking2.getId(), firstElement.get().getId());
    }

    @Test
    void findByIdAndBookerOrOwner_shouldReturnBooking() {
        Optional<Booking> booking = bookingRepository
                .findByIdAndBookerOrOwner(ID_1, ID_1);

        assertTrue(booking.isPresent());
        assertEquals(start, booking.get().getStart());
        assertEquals(ID_1, booking.get().getId());
    }

    @Test
    void findByIdAndItemOwnerId_shouldReturnBooking() {
        Optional<Booking> booking = bookingRepository
                .findByIdAndItemOwnerId(ID_1, ID_1);

        assertTrue(booking.isPresent());
        assertEquals(start, booking.get().getStart());
        assertEquals(ID_1, booking.get().getId());
    }
}