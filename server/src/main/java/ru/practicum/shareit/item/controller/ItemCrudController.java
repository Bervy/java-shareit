package ru.practicum.shareit.item.controller;

import java.util.Optional;

public interface ItemCrudController<T> extends ItemFindController<T> {

    Optional<T> save(long userId, T t);

    Optional<T> update(long userId, long id, T t);

    void delete(long userId, long id);
}