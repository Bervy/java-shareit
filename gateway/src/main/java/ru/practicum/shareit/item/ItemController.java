package ru.practicum.shareit.item;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.ArrayList;

import static ru.practicum.shareit.error.ExceptionDescriptions.*;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAll(@Positive @RequestHeader("X-Sharer-User-Id") @Min(0) long userId,
                                          @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0")
                                          int from,
                                          @Positive @RequestParam(name = "size", required = false, defaultValue = "10")
                                          int size) {
        return itemClient.findAll(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@Positive @PathVariable("itemId") @Min(0) long itemId,
                                           @Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.findById(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> save(@Positive @RequestHeader("X-Sharer-User-Id") @Min(0) long userId,
                                       @RequestBody @Valid ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException(AVAILABLE_NOT_FOUND.getTitle());
        }
        if (!StringUtils.isNotBlank(itemDto.getName())) {
            throw new ValidationException(EMPTY_NAME.getTitle());
        }
        if (itemDto.getDescription() == null) {
            throw new ValidationException(DESCRIPTION_NOT_FOUND.getTitle());
        }
        return itemClient.save(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Positive @RequestHeader("X-Sharer-User-Id") @Min(0) long userId,
                                         @Positive @PathVariable("itemId") @Min(0) long itemId,
                                         @RequestBody @Valid ItemDto itemDto) {

        return itemClient.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@Positive @RequestHeader("X-Sharer-User-Id") @Min(0) long userId,
                       @Positive @PathVariable("itemId") @Min(0) long itemId) {
        return itemClient.delete(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(value = "text") String text,
                                         @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0")
                                         int from,
                                         @Positive @RequestParam(name = "size", required = false, defaultValue = "10")
                                         int size) {
        if (!StringUtils.isNotBlank(text)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long authorId, @PathVariable long itemId,
                                             @RequestBody CommentDto commentDto) {
        if (!StringUtils.isNotBlank(commentDto.getText())) {
            throw new ValidationException(EMPTY_NAME.getTitle());
        }
        return itemClient.addComment(authorId, itemId, commentDto);
    }
}