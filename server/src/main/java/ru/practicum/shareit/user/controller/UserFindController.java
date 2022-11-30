package ru.practicum.shareit.user.controller;

import java.util.List;
import java.util.Optional;

public interface UserFindController<T> {

    List<T> findAll();

    Optional<T> findById(long id);
}