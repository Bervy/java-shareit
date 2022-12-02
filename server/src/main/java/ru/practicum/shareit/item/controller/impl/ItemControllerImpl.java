package ru.practicum.shareit.item.controller.impl;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.controller.ItemCrudController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemControllerImpl implements ItemCrudController<ItemFullDto> {

    private final ItemServiceImpl itemService;

    public ItemControllerImpl(ItemServiceImpl userService) {
        this.itemService = userService;
    }

    @Override
    @GetMapping
    public List<ItemFullDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam(value = "from", required = false, defaultValue = "0")
                                     int from,
                                     @RequestParam(value = "size", required = false, defaultValue = "10")
                                         int size) {
        return itemService.findAll(userId, from, size);
    }

    @Override
    @GetMapping("/{itemId}")
    public Optional<ItemFullDto> findById(@PathVariable("itemId") long itemId,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findById(itemId, userId);
    }

    @Override
    @PostMapping
    public Optional<ItemFullDto> save(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestBody ItemFullDto itemDto) {
        return itemService.save(userId, itemDto);
    }

    @Override
    @PatchMapping("/{itemId}")
    public Optional<ItemFullDto> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable("itemId") long itemId,
                                    @RequestBody ItemFullDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @Override
    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable("itemId") long itemId) {
        itemService.delete(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemFullDto> search(@RequestParam(value = "text") String text,
                                @RequestParam(value = "from", required = false, defaultValue = "0")
                                    int from,
                                @RequestParam(value = "size", required = false, defaultValue = "10")
                                        int size) {
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentFullDto addComment(@RequestHeader("X-Sharer-User-Id") Long authorId, @PathVariable long itemId,
                                     @RequestBody CommentDto commentDto) {
        return itemService.addComment(authorId, itemId, commentDto);
    }
}