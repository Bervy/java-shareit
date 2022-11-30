package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserIntegrationTests {
    private final UserServiceImpl userService;
    private final UserMapper userMapper;
    private final User mockUser = User.builder()
            .id(1)
            .name("userName1")
            .email("userMail1@ya.ru")
            .build();

    @Test
    void testFindUserById() throws ValidationException {
        userService.save(userMapper.toUserDto(mockUser));

        Optional<UserDto> user = userService.findById(1);

        assertThat(user.isPresent(), is(true));
        assertThat(user.get().getId(), equalTo(mockUser.getId()));
        assertThat(user.get().getName(), equalTo(mockUser.getName()));
        assertThat(user.get().getEmail(), equalTo(mockUser.getEmail()));
    }
}