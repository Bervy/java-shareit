package ru.practicum.shareit.user.service.impl;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
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
public class UserServiceImpl implements UserCrudService<UserDto> {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> findById(long userId) {
        return Optional.ofNullable(UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow((() -> new NotFoundException(USER_NOT_FOUND.getTitle())))));
    }

    @Override
    public Optional<UserDto> save(UserDto userDto) {
        validationSave(userDto);
        return Optional.ofNullable(UserMapper.toUserDto(Optional.of(userRepository.save(UserMapper.fromUserDto(userDto)))
                .orElseThrow((() -> new AlreadyExistsException(USER_ALREADY_EXISTS.getTitle())))));
    }

    @Override
    public Optional<UserDto> update(long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getTitle()));
        validationUpdate(user, userDto);
        return Optional.ofNullable(UserMapper.toUserDto(Optional.of(userRepository.save(user))
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