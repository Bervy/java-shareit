package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import static ru.practicum.shareit.error.ExceptionDescriptions.EMPTY_EMAIL;
import static ru.practicum.shareit.error.ExceptionDescriptions.EMPTY_NAME;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@Positive @PathVariable("userId") @Min(0) long userId) {
        return userClient.findById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new ValidationException(EMPTY_EMAIL.getTitle());
        }
        if (userDto.getName() == null) {
            throw new ValidationException(EMPTY_NAME.getTitle());
        }
        return userClient.save(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Positive @PathVariable("userId") @Min(0) long userId,
                                         @RequestBody @Valid UserDto userDto) {
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@Positive @PathVariable("userId") @Min(0) long userId) {
        return userClient.delete(userId);
    }
}