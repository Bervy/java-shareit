package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;

@Service
public interface ItemCrudService<T> extends ItemFindService<T> {
}