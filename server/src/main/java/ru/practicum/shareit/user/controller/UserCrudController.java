package ru.practicum.shareit.user.controller;

import java.util.Optional;

public interface UserCrudController<T> extends UserFindController<T> {

    Optional<T> save(T t);

    Optional<T> update(long userId, T t);

    void delete(long id);
}