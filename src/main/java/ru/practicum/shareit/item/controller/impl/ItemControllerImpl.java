package ru.practicum.shareit.item.controller.impl;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.controller.ItemCrudController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemControllerImpl implements ItemCrudController<ItemDto> {

    private final ItemServiceImpl itemService;

    public ItemControllerImpl(ItemServiceImpl userService) {
        this.itemService = userService;
    }

    @Override
    @GetMapping
    public List<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") @Min(0) long userId) {
        return itemService.findAll(userId);
    }

    @Override
    @GetMapping("/{itemId}")
    public Optional<ItemDto> findById(@PathVariable("itemId") @Min(0) long itemId,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findById(itemId, userId);
    }

    @Override
    @PostMapping
    public Optional<ItemDto> save(@RequestHeader("X-Sharer-User-Id") @Min(0) long userId,
                                  @RequestBody @Valid ItemDto itemDto) {
        return itemService.save(userId, itemDto);
    }

    @Override
    @PatchMapping("/{itemId}")
    public Optional<ItemDto> update(@RequestHeader("X-Sharer-User-Id") @Min(0) long userId,
                                    @PathVariable("itemId") @Min(0) long itemId,
                                    @RequestBody @Valid ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @Override
    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") @Min(0) long userId,
                       @PathVariable("itemId") @Min(0) long itemId) {
        itemService.delete(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(value = "text") String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentFullDto addComment(@RequestHeader("X-Sharer-User-Id") Long authorId, @PathVariable long itemId,
                                     @RequestBody CommentDto commentDto) {
        return itemService.addComment(authorId, itemId, commentDto);
    }

}