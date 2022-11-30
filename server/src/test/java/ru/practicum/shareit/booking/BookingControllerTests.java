package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTests {
    @MockBean
    private BookingService bookingService;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private static final long ID_1 = 1L;
    private static final long ID_2 = 2L;
    private final User owner = User.builder()
            .id(ID_1)
            .name("userName1")
            .email("mail1@ya.ru").build();
    private final Item item = Item.builder()
            .id(ID_1)
            .name("item1Name")
            .description("item1Description")
            .available(true)
            .owner(owner).build();
    private final User booker = User.builder()
            .id(ID_2)
            .name("userName2")
            .email("mail2@ya.ru").build();
    private final BookingFullDto bookingFullDto = BookingFullDto.builder()
            .id(ID_1)
            .start(LocalDateTime.of(2022, 12, 5, 1, 1))
            .end(LocalDateTime.of(2022, 12, 7, 1, 1))
            .booker(new BookingFullDto
                    .BookerDto(booker.getId(), booker.getName()))
            .item(new BookingFullDto
                    .BookingItemDto(item.getId(), item.getName()))
            .status(BookingStatus.WAITING)
            .build();
    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(ID_1)
            .start(LocalDateTime.of(2022, 12, 5, 1, 1))
            .end(LocalDateTime.of(2022, 12, 7, 1, 1))
            .build();

    @Test
    void create_shouldCreateBooking() throws Exception {
        when(bookingService.create(anyLong(), any())).thenReturn(Optional.of(bookingFullDto));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, ID_1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingFullDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingFullDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.booker.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(booker.getName())))
                .andExpect(jsonPath("$.item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(item.getName())))
                .andExpect(jsonPath("$.status", is(bookingFullDto.getStatus().toString())));
    }

    @Test
    void confirmation_shouldConfirmBooking() throws Exception {
        bookingFullDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.confirmation(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(Optional.of(bookingFullDto));

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, ID_1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingFullDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingFullDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.booker.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(booker.getName())))
                .andExpect(jsonPath("$.item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(item.getName())))
                .andExpect(jsonPath("$.status", is(bookingFullDto.getStatus().toString())));
    }

    @Test
    void getByIdAndBookerOrOwner_shouldReturnBookingById() throws Exception {
        when(bookingService.getByIdAndBookerOrOwner(anyLong(), anyLong()))
                .thenReturn(Optional.of(bookingFullDto));

        mockMvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, ID_1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingFullDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingFullDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.booker.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(booker.getName())))
                .andExpect(jsonPath("$.item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(item.getName())))
                .andExpect(jsonPath("$.status", is(bookingFullDto.getStatus().toString())));
    }

    @Test
    void getAllByBooker_shouldReturnListOfBookingByBookerId() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingFullDto));

        mockMvc.perform(get("/bookings")
                        .content(objectMapper.writeValueAsString(bookingFullDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, ID_1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingFullDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(bookingFullDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].booker.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(booker.getName())))
                .andExpect(jsonPath("$.[0].item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(item.getName())))
                .andExpect(jsonPath("$.[0].status", is(bookingFullDto.getStatus().toString())));
    }

    @Test
    void getAllByOwner_shouldReturnListOfBookingByOwnerId() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingFullDto));

        mockMvc.perform(get("/bookings/owner")
                        .content(objectMapper.writeValueAsString(bookingFullDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, ID_1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingFullDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(bookingFullDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].booker.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(booker.getName())))
                .andExpect(jsonPath("$.[0].item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(item.getName())))
                .andExpect(jsonPath("$.[0].status", is(bookingFullDto.getStatus().toString())));
    }
}