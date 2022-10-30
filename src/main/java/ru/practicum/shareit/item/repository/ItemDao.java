package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {

    List<Item> findAll(long userId);

    Optional<Item> save(long userId, Item item);

    Optional<Item> update(long itemId, Item item);

    void delete(long userId, long id);

    Optional<Item> findById(long id);

    List<Item> search(String text);
}