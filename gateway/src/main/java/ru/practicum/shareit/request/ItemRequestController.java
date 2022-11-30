package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> findAllByOwner(@Positive @RequestHeader("X-Sharer-User-Id") @Min(0) long userId) {
        return itemRequestClient.findAllByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllByAnotherUser(
            @RequestHeader("X-Sharer-User-Id") @Min(0) long userId,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0")
            int from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "10")
            int size) {
        return itemRequestClient.findAllByAnotherUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@Positive @PathVariable("requestId") @Min(0) long requestId,
                                           @Positive @RequestHeader("X-Sharer-User-Id") @Min(0) long userId) {
        return itemRequestClient.findById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> save(@Positive @RequestHeader("X-Sharer-User-Id") @Min(0) long userId,
                                       @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.save(userId, itemRequestDto);
    }
}