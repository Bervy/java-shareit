package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.error.ExceptionDescriptions.EMPTY_EMAIL;
import static ru.practicum.shareit.error.ExceptionDescriptions.EMPTY_NAME;

@ExtendWith(MockitoExtension.class)
class UserUnitTests {
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private static final long ID_1 = 1L;
    private static final long ID_2 = 2L;
    private final User user1 = User.builder()
            .id(ID_1)
            .name("userName1")
            .email("userMail1@ya.ru")
            .build();
    private final UserDto userDto = UserDto.builder()
            .id(ID_1)
            .name("userName1")
            .email("userMail1@ya.ru")
            .build();
    private final User user2 = User.builder()
            .id(ID_2)
            .name("userName2")
            .email("userMail2@ya.ru")
            .build();

    @Test
    void testAddUser() {
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);
        Mockito.when(userMapper.fromUserDto(userDto)).thenReturn(user1);
        Mockito.when(userMapper.toUserDto(user1)).thenReturn(userDto);

        Optional<UserDto> userDtoResult = userService.save(userDto);

        assertTrue(userDtoResult.isPresent());
        assertEquals(ID_1, userDtoResult.get().getId());
        verify(userRepository,times(1)).save(user1);
    }

    @Test
    void testGetUserById() throws ValidationException {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        Mockito.when(userMapper.toUserDto(user1)).thenReturn(userDto);

        Optional<UserDto> userDtoResult =  userService.findById(ID_1);

        assertTrue(userDtoResult.isPresent());
        assertEquals(ID_1, userDtoResult.get().getId());
        verify(userRepository,times(1)).findById(ID_1);
    }

    @Test
    void testFindAll() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> usersResult = userService.findAll();

        int expectedSize = 2;
        Assertions.assertNotNull(usersResult);
        assertEquals(expectedSize, usersResult.size());
        verify(userRepository,times(1)).findAll();
    }

    @Test
    void testUpdateUser() throws ValidationException {
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        Mockito.when(userMapper.toUserDto(user1)).thenReturn(userDto);

        Optional<UserDto> userDtoResult =  userService.update(ID_1, userDto);

        assertTrue(userDtoResult.isPresent());
        assertEquals(ID_1, userDtoResult.get().getId());
        verify(userRepository,times(1)).save(user1);
    }

    @Test
    void testDeleteUser() throws ValidationException {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        userService.delete(ID_1);

        Mockito.verify(userRepository, times(1)).delete(user1);
    }

    @Test
    void save_shouldThrowValidationException_emptyEmail() throws ValidationException {
        userDto.setEmail(null);
        Exception exception = assertThrows(ValidationException.class, () -> userService.save(userDto));

        assertEquals(EMPTY_EMAIL.getTitle(), exception.getMessage());
    }

    @Test
    void save_shouldThrowValidationException_emptyName() throws ValidationException {
        userDto.setName(null);
        Exception exception = assertThrows(ValidationException.class, () -> userService.save(userDto));

        assertEquals(EMPTY_NAME.getTitle(), exception.getMessage());
    }
}