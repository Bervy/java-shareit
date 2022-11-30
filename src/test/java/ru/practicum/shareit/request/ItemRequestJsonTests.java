package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestJsonTests {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestsJson() throws Exception {
         ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Description")
                .build();
        JsonContent<ItemRequestDto> resultJson = json.write(itemRequestDto);

        assertThat(resultJson).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(resultJson).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
    }
}