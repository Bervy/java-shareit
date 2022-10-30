package ru.practicum.shareit.item.repository.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDao;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.error.ExceptionDescriptions.ITEM_NOT_FOUND;
import static ru.practicum.shareit.error.ExceptionDescriptions.OWNER_NOT_FOUND;

@Repository
public class ItemDaoImpl implements ItemDao {

    private final Map<Long, Item> items = new HashMap<>();
    private long id = 0;

    @Override
    public List<Item> findAll(long userId) {
        List<Item> itemsList = new ArrayList<>(items.values());
        return itemsList.stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> save(long userId, Item item) {
        if (!items.containsValue(item)) {
            items.put(++id, item);
            item.setId(id);
            item.setOwner(userId);
            return Optional.of(item);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Item> update(long itemId, Item item) {
        items.put(itemId, item);
        return Optional.of(item);
    }

    @Override
    public void delete(long userId, long itemId) {
        if (items.containsKey(itemId)) {
            if (items.get(itemId).getOwner() == userId) {
                items.remove(itemId);
            } else {
                throw new NotFoundException(OWNER_NOT_FOUND.getTitle());
            }
        } else {
            throw new NotFoundException(ITEM_NOT_FOUND.getTitle());
        }
    }

    @Override
    public Optional<Item> findById(long id) {
        if (items.containsKey(id)) {
            return Optional.of(items.get(id));
        } else {
            throw new NotFoundException(ITEM_NOT_FOUND.getTitle());
        }
    }

    @Override
    public List<Item> search(String text) {
        return new ArrayList<>(items.values()).stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())
                                && item.getAvailable())
                .collect(Collectors.toList());
    }
}