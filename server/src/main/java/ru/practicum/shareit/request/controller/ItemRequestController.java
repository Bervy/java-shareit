package ru.practicum.shareit.request.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping
    public List<ItemRequestDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findAllByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllByAnotherUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0")
            int from,
            @RequestParam(value = "size", required = false, defaultValue = "10")
            int size) {
        return itemRequestService.findAllByAnotherUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public Optional<ItemRequestDto> findById(@PathVariable("requestId") long requestId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findById(userId, requestId);
    }

    @PostMapping
    public Optional<ItemRequestDto> save(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.save(userId, itemRequestDto);
    }
}