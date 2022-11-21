package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingIntegrationTests {
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;
    private final BookingService bookingService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    private static final long OWNER_ID = 1L;
    private static final long BOOKER_ID = 2L;
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
    private final BookingDto booking1 = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.of(2022, 11, 5, 1, 1))
            .end(LocalDateTime.of(2022, 11, 7, 1, 1))
            .build();
    private final BookingDto booking2 = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.of(2022, 11, 5, 1, 1).plusHours(1))
            .end(LocalDateTime.of(2022, 11, 7, 1, 1).plusHours(2))
            .build();

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getAllByBooker_shouldReturnAllBookingsByBookerId() {
        userService.save(userMapper.toUserDto(owner));
        userService.save(userMapper.toUserDto(booker));
        itemService.save(OWNER_ID, itemMapper.toItemFullDto(item));
        Optional<BookingFullDto> bookingFullDto1 = bookingService.create(BOOKER_ID, booking1);
        Optional<BookingFullDto> bookingFullDto2 = bookingService.create(BOOKER_ID, booking2);

        Collection<BookingFullDto> bookings = bookingService.getAllByBooker(BOOKER_ID,
                BookingState.WAITING.toString(), 0, 5);

        int expectedSize = 2;
        assertTrue(bookingFullDto1.isPresent());
        assertTrue(bookingFullDto2.isPresent());
        assertNotNull(bookingFullDto2);
        assertEquals(expectedSize, bookings.size());
        assertTrue(bookings.stream().findFirst().isPresent());
        assertEquals(bookingFullDto2.get().getId(), bookings.stream().findFirst().get().getId());
        assertEquals(bookingFullDto2.get().getStart(), bookings.stream().findFirst().get().getStart());
        assertEquals(bookingFullDto2.get().getEnd(), bookings.stream().findFirst().get().getEnd());
    }
}