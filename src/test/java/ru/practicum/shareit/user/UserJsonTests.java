package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserJsonTests {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserJson() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("userName1")
                .email("userMail1@ya.ru")
                .build();
        JsonContent<UserDto> resultJson = json.write(userDto);

        assertThat(resultJson).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(resultJson).extractingJsonPathStringValue("$.name")
                .isEqualTo(userDto.getName());
        assertThat(resultJson).extractingJsonPathStringValue("$.email")
                .isEqualTo(userDto.getEmail());
    }
}