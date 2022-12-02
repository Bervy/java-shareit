package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemFullDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemJsonTests {

    @Autowired
    private JacksonTester<ItemFullDto> json;

    @Test
    void testItemJson() throws Exception {
         ItemFullDto itemFullDto = ItemFullDto.builder()
                .id(1L)
                .name("itemName1")
                .description("itemDescription1")
                .available(true)
                .build();
        JsonContent<ItemFullDto> resultJson = json.write(itemFullDto);

        assertThat(resultJson).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(resultJson).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemFullDto.getName());
        assertThat(resultJson).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemFullDto.getDescription());
        assertThat(resultJson).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemFullDto.getAvailable());
    }
}