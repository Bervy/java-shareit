package ru.practicum.shareit.item.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDao;
import ru.practicum.shareit.item.service.ItemCrudService;
import ru.practicum.shareit.user.repository.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.error.ExceptionDescriptions.*;

@Service
public class ItemServiceImpl implements ItemCrudService<ItemDto> {

    private final ItemDao itemDao;
    private final UserDao userDao;

    public ItemServiceImpl(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public List<ItemDto> findAll(long userId) {
        return itemDao.findAll(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ItemDto> findById(long itemId) {
        return Optional.ofNullable(ItemMapper.toItemDto(itemDao.findById(itemId)
                .orElseThrow((() -> new NotFoundException(ITEM_NOT_FOUND.getTitle())))));
    }

    @Override
    public Optional<ItemDto> save(long userId, ItemDto itemDto) {
        validationSave(userId, itemDto);
        return Optional.ofNullable(ItemMapper.toItemDto(itemDao.save(userId, ItemMapper.fromItemDto(itemDto))
                .orElseThrow((() -> new AlreadyExistsException(ITEM_ALREADY_EXISTS.getTitle())))));
    }

    @Override
    public Optional<ItemDto> update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND.getTitle()));
        validationUpdate(item, itemDto, userId);
        return Optional.ofNullable(ItemMapper.toItemDto(itemDao.update(itemId, item)
                .orElseThrow((() -> new NotFoundException(ITEM_NOT_FOUND.getTitle())))));
    }

    @Override
    public void delete(long userId, long itemId) {
        itemDao.delete(userId, itemId);
    }

    public List<ItemDto> search(String text) {
        if (text.equals("")) {
            return new ArrayList<>();
        }
        return itemDao.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validationSave(long userId, ItemDto itemDto) {
        if (userDao.findById(userId).isEmpty()) {
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
        if (item.getOwner() != userId) {
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
}