package ru.practicum.shareit.user.service.impl;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserDao;
import ru.practicum.shareit.user.repository.impl.UserDaoImpl;
import ru.practicum.shareit.user.service.UserCrudService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.error.ExceptionDescriptions.*;

@Service
public class UserServiceImpl implements UserCrudService<UserDto> {

    private final UserDao userDao;

    public UserServiceImpl(UserDaoImpl userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<UserDto> findAll() {
        return userDao.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> findById(long userId) {
        return Optional.ofNullable(UserMapper.toUserDto(userDao.findById(userId)
                .orElseThrow((() -> new NotFoundException(USER_NOT_FOUND.getTitle())))));
    }

    @Override
    public Optional<UserDto> save(UserDto userDto) {
        validationSave(userDto);
        return Optional.ofNullable(UserMapper.toUserDto(userDao.save(UserMapper.fromUserDto(userDto))
                .orElseThrow((() -> new AlreadyExistsException(USER_ALREADY_EXISTS.getTitle())))));
    }

    @Override
    public Optional<UserDto> update(long userId, UserDto userDto) {
        User user = userDao.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getTitle()));
        validationUpdate(user, userDto);
        return Optional.ofNullable(UserMapper.toUserDto(userDao.update(user)
                .orElseThrow((() -> new NotFoundException(USER_NOT_FOUND.getTitle())))));
    }

    @Override
    public void delete(long userId) {
        userDao.delete(userId);
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
        if (userDto.getEmail() != null) {
            if (userDao.findAll()
                    .stream()
                    .anyMatch(user1 -> user1.getEmail().equals(userDto.getEmail()))) {
                throw new AlreadyExistsException(EMAIL_ALREADY_EXISTS.getTitle());
            }
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
    }
}