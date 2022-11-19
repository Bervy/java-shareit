package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingJsonTests {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingJson() throws Exception {
        LocalDateTime start = LocalDateTime.of(2022, 12, 5, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2022, 12, 7, 1, 1, 1);
        BookingDto bookingDto = BookingDto.builder().itemId(1L).start(start).end(end).build();
        JsonContent<BookingDto> resultJson = json.write(bookingDto);

        assertThat(resultJson).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(resultJson).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.toString());
        assertThat(resultJson).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.toString());
    }
}