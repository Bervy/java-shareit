package ru.practicum.shareit.item.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemCrudService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.error.ExceptionDescriptions.*;

@Service
public class ItemServiceImpl implements ItemCrudService<ItemDto> {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<ItemDto> findAll(long userId) {
        List<ItemDto> itemDtoList = itemRepository.findAllByOwnerId(userId).
                stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        for (ItemDto itemDto : itemDtoList) {
            setLastAndNextBooking(itemDto);
            itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId())
                    .stream().map(CommentMapper::toCommentFullDto).collect(Collectors.toList()));
        }
        return itemDtoList;
    }

    @Override
    public Optional<ItemDto> findById(long itemId, long ownerId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND.getTitle()));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(commentRepository.findAllByItemId(itemId)
                .stream().map(CommentMapper::toCommentFullDto).collect(Collectors.toList()));
        if (item.getOwner().getId() == ownerId) {
            setLastAndNextBooking(itemDto);
        }
        return Optional.of(itemDto);
    }

    @Override
    public Optional<ItemDto> save(long userId, ItemDto itemDto) {
        validationSave(userId, itemDto);
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            itemDto.setOwner(user.get());
            return Optional.ofNullable(ItemMapper.toItemDto(Optional.of(
                            itemRepository.save(ItemMapper.fromItemDto(itemDto)))
                    .orElseThrow((() -> new AlreadyExistsException(ITEM_ALREADY_EXISTS.getTitle())))));
        } else {
            throw new NotFoundException(USER_NOT_FOUND.getTitle());
        }
    }

    @Override
    public Optional<ItemDto> update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND.getTitle()));
        validationUpdate(item, itemDto, userId);
        return Optional.ofNullable(ItemMapper.toItemDto(Optional.of(itemRepository.save(item))
                .orElseThrow((() -> new NotFoundException(ITEM_NOT_FOUND.getTitle())))));
    }

    @Override
    public void delete(long userId, long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent() && item.get().getOwner().getId() == userId) {
            itemRepository.delete(item.get());
        }
    }

    public List<ItemDto> search(String text) {
        if (!StringUtils.isNotBlank(text)) {
            return new ArrayList<>();
        }
        return itemRepository.search(text)
                .stream()
                .filter(Item::isAvailable)
                .map(ItemMapper::toItemDto)
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
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new ValidationException(USER_NOT_FOUND.getTitle()));
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ValidationException(ITEM_NOT_FOUND.getTitle()));
            return CommentMapper.toCommentFullDto(commentRepository.save(CommentMapper.fromCommentDto(dto, item, author)));
        } else {
            throw new ValidationException(FORBIDDEN_TO_ADD_COMMENTS.getTitle());
        }
    }

    private void validationSave(long userId, ItemDto itemDto) {
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

    private void validationUpdate(Item item, ItemDto itemDto, long userId) {
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

    private void setLastAndNextBooking(ItemDto itemDto) {
        List<Booking> lastBooking = bookingRepository.findAllByItemIdOrderByStartAsc(itemDto.getId());
        List<Booking> nextBooking = bookingRepository.findAllByItemIdOrderByStartDesc(itemDto.getId());
        itemDto.setLastBooking(lastBooking.isEmpty() ? null : BookingMapper.toBookingShortDto(lastBooking.get(0)));
        itemDto.setNextBooking(itemDto.getLastBooking() == null ? null :
                BookingMapper.toBookingShortDto(nextBooking.get(0)));
    }
}