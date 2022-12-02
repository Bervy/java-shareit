package ru.practicum.shareit.item.controller;

import java.util.List;
import java.util.Optional;

public interface ItemFindController<T> {

    List<T> findAll(long userId, int from, int size);

    Optional<T> findById(long id, long userId);
}