package ru.practicum.shareit.user.repository.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserDao;

import java.util.*;

import static ru.practicum.shareit.error.ExceptionDescriptions.USER_NOT_FOUND;

@Repository
public class UserDaoImpl implements UserDao {

    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> save(User user) {
        if (!users.containsValue(user)) {
            users.put(++id, user);
            user.setId(id);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> update(User user) {
        users.put(user.getId(), user);
        return Optional.of(user);

    }

    @Override
    public void delete(long id) {
        if (users.remove(id) == null) {
            throw new NotFoundException(USER_NOT_FOUND.getTitle());
        }
    }

    @Override
    public Optional<User> findById(long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        } else {
            throw new NotFoundException(USER_NOT_FOUND.getTitle());
        }
    }
}