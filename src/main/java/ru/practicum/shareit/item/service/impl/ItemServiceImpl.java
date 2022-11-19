package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemCrudService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.error.ExceptionDescriptions.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemCrudService<ItemFullDto> {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Override
    public List<ItemFullDto> findAll(long userId, int from, int size) {
        validateFromAndSize(from, size);
        List<ItemFullDto> itemDtoList = itemRepository.findAllByOwnerId(
                        userId, PageRequest.of(from / size, size))
                .stream().map(itemMapper::toItemFullDto).collect(Collectors.toList());
        for (ItemFullDto itemDto : itemDtoList) {
            setLastAndNextBooking(itemDto);
            setComments(itemDto, itemDto.getId());
        }
        return itemDtoList;
    }

    @Override
    public Optional<ItemFullDto> findById(long itemId, long ownerId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND.getTitle()));
        ItemFullDto itemDto = itemMapper.toItemFullDto(item);
        setComments(itemDto, itemId);
        if (item.getOwner().getId() == ownerId) {
            setLastAndNextBooking(itemDto);
        }
        return Optional.of(itemDto);
    }

    @Override
    public Optional<ItemFullDto> save(long userId, ItemFullDto itemDto) {
        validationSave(userId, itemDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getTitle()));
        itemDto.setOwner(user);
        Item item = itemMapper.fromItemDto(itemDto);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(ITEM_REQUEST_NOT_FOUND.getTitle()));
            item.setRequest(itemRequest);
        }
        return Optional.ofNullable(itemMapper.toItemFullDto(itemRepository.save(item)));
    }

    @Override
    public Optional<ItemFullDto> update(long userId, long itemId, ItemFullDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND.getTitle()));
        validationUpdate(item, itemDto, userId);
        return Optional.ofNullable(itemMapper.toItemFullDto(Optional.of(itemRepository.save(item))
                .orElseThrow((() -> new NotFoundException(ITEM_NOT_FOUND.getTitle())))));
    }

    @Override
    public void delete(long userId, long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent() && item.get().getOwner().getId() == userId) {
            itemRepository.delete(item.get());
        }
    }

    public List<ItemFullDto> search(String text, int from, int size) {
        if (!StringUtils.isNotBlank(text)) {
            return new ArrayList<>();
        }
        validateFromAndSize(from, size);
        return itemRepository.search(text, PageRequest.of(from / size, size))
                .stream()
                .filter(Item::isAvailable)
                .map(itemMapper::toItemFullDto)
                .collect(Collectors.toList());
    }

    public CommentFullDto addComment(Long authorId, Long itemId, CommentDto dto) {
        if (!StringUtils.isNotBlank(dto.getText())) {
            throw new ValidationException(EMPTY_NAME.getTitle());
        }
        Booking booking = bookingRepository.findAllByBookerId(authorId).stream()
                .filter(b -> b.getItem().getId() == itemId)
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .findFirst()
                .orElseThrow(() -> new ValidationException(NO_MATCHING_BOOKINGS.getTitle()));
        if (booking.getStatus() == BookingStatus.APPROVED) {
            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new ValidationException(USER_NOT_FOUND.getTitle()));
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ValidationException(ITEM_NOT_FOUND.getTitle()));
            return commentMapper.toCommentFullDto(commentRepository.save(commentMapper.fromCommentDto(dto, item, author)));
        } else {
            throw new ValidationException(FORBIDDEN_TO_ADD_COMMENTS.getTitle());
        }
    }

    private void validationSave(long userId, ItemFullDto itemDto) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(USER_NOT_FOUND.getTitle());
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException(AVAILABLE_NOT_FOUND.getTitle());
        }
        if (!StringUtils.isNotBlank(itemDto.getName())) {
            throw new ValidationException(EMPTY_NAME.getTitle());
        }
        if (itemDto.getDescription() == null) {
            throw new ValidationException(DESCRIPTION_NOT_FOUND.getTitle());
        }
    }

    private void validationUpdate(Item item, ItemFullDto itemDto, long userId) {
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException(OWNER_NOT_FOUND.getTitle());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }

    private void setLastAndNextBooking(ItemFullDto itemDto) {
        List<Booking> lastBooking = bookingRepository.findAllByItemIdOrderByStartAsc(itemDto.getId());
        List<Booking> nextBooking = bookingRepository.findAllByItemIdOrderByStartDesc(itemDto.getId());
        itemDto.setLastBooking(lastBooking.isEmpty() ? null : bookingMapper.toBookingShortDto(lastBooking.get(0)));
        itemDto.setNextBooking(itemDto.getLastBooking() == null ? null : bookingMapper.toBookingShortDto(nextBooking.get(0)));
    }

    private void setComments(ItemFullDto itemDto, long itemId) {
        itemDto.setComments(commentRepository.findAllByItemId(itemId)
                .stream().map(commentMapper::toCommentFullDto).collect(Collectors.toList()));
    }

    private void validateFromAndSize(int from, int size) {
        if (from < 0 || size < 0) {
            throw new ValidationException(FROM_OR_SIZE_LESS_THAN_ZERO.getTitle());
        }
    }
}