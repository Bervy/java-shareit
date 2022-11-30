package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.error.ExceptionDescriptions.*;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    public List<ItemRequestDto> findAllByOwner(long userId) {
        validateUser(userId);
        List<ItemRequestDto> itemRequests = itemRequestRepository.findAllByRequestorId(userId)
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        setItems(itemRequests);
        return itemRequests;
    }

    public List<ItemRequestDto> findAllByAnotherUser(long userId, int from, int size) {
        User user = getValidUser(userId);
        List<ItemRequestDto> itemRequests = itemRequestRepository
                .findAllByRequestorIsNot(user, PageRequest.of(from / size, size
                        , Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        setItems(itemRequests);
        return itemRequests;

    }

    public Optional<ItemRequestDto> findById(long userId, long requestId) {
        validateUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(
                requestId).orElseThrow(() -> new NotFoundException(ITEM_REQUEST_NOT_FOUND.getTitle()));
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        List<Item> items = itemRepository.findAllByRequestId(itemRequestDto.getId());
        itemRequestDto.setItems(items
                .stream()
                .map(itemMapper::toItemShortDto)
                .collect(Collectors.toList()));
        return Optional.of(itemRequestDto);
    }

    public Optional<ItemRequestDto> save(long userId, ItemRequestDto itemRequestDto) {
        User user = getValidUser(userId);
        return Optional.ofNullable(itemRequestMapper.toItemRequestDto(
                itemRequestRepository.save(itemRequestMapper.fromItemRequestDto(itemRequestDto, user))));
    }

    private void validateUser(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(USER_NOT_FOUND.getTitle());
        }
    }

    private User getValidUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getTitle()));
    }

    private void setItems(List<ItemRequestDto> itemRequests) {
        for (ItemRequestDto itemRequestDto : itemRequests) {
            itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId())
                    .stream()
                    .map(itemMapper::toItemShortDto)
                    .collect(Collectors.toList()));
        }
    }
}