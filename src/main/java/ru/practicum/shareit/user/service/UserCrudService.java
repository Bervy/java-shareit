package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;

@Service
public interface UserCrudService<T> extends UserFindService<T> {
}