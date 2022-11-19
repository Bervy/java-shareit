package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserCrudService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.error.ExceptionDescriptions.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserCrudService<UserDto> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> findById(long userId) {
        return Optional.ofNullable(userMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow((() -> new NotFoundException(USER_NOT_FOUND.getTitle())))));
    }

    @Override
    public Optional<UserDto> save(UserDto userDto) {
        validationSave(userDto);
        User user = userRepository.save(userMapper.fromUserDto(userDto));
        return Optional.ofNullable(userMapper.toUserDto(user));
    }

    @Override
    public Optional<UserDto> update(long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getTitle()));
        validationUpdate(user, userDto);
        return Optional.ofNullable(userMapper.toUserDto(Optional.of(userRepository.save(user))
                .orElseThrow((() -> new NotFoundException(USER_NOT_FOUND.getTitle())))));
    }

    @Override
    public void delete(long userId) {
        Optional<User> user = userRepository.findById(userId);
        user.ifPresent(userRepository::delete);
    }

    private void validationSave(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new ValidationException(EMPTY_EMAIL.getTitle());
        }
        if (userDto.getName() == null) {
            throw new ValidationException(EMPTY_NAME.getTitle());
        }
    }

    private void validationUpdate(User user, UserDto userDto) {
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
    }
}