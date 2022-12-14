package ru.practicum.shareit.user.controller.impl;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.controller.UserCrudController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserCrudService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/users")
public class UserControllerImpl implements UserCrudController<UserDto> {

    private final UserCrudService<UserDto> userService;

    public UserControllerImpl(UserCrudService<UserDto> userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @Override
    @GetMapping("/{userId}")
    public Optional<UserDto> findById(@PathVariable("userId") long userId) {
        return userService.findById(userId);
    }

    @Override
    @PostMapping
    public Optional<UserDto> save(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @Override
    @PatchMapping("/{userId}")
    public Optional<UserDto> update(@PathVariable("userId") long userId,
                                    @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @Override
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") long userId) {
        userService.delete(userId);
    }
}