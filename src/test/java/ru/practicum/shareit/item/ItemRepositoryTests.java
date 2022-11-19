package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private static final Pageable PAGE = PageRequest.of(0, 5);
    private final User user1 = User.builder()
            .name("userName1")
            .email("userMail1@ya.ru").build();
    private final User user2 = User.builder()
            .name("userName2")
            .email("userMail2@ya.ru").build();
    private final ItemRequest request = ItemRequest
            .builder()
            .description("itemRequestDescription1").build();
    private final Item item1 = Item.builder()
            .name("itemName1")
            .description("itemDescription1")
            .available(true)
            .request(request)
            .owner(user1).build();
    private final Item item2 = Item.builder()
            .name("itemName2")
            .description("itemDescription2")
            .available(true)
            .request(request)
            .owner(user2).build();

    @BeforeEach
    void saveData() {
        itemRequestRepository.save(request);
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @Test
    void findAllByOwnerId_shouldReturnCollectionOfItems() {
        Page<Item> items = itemRepository.findAllByOwnerId(user1.getId(), PAGE);
        Optional<Item> firstElement = items.stream().findFirst();

        int expectedSize = 1;
        assertNotNull(items);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, items.getTotalElements());
        assertEquals(item1.getId(), firstElement.get().getId());
    }

    @Test
    void findAllByRequestId_shouldReturnCollectionOfItems() {
        Collection<Item> items = itemRepository.findAllByRequestId(request.getId());
        Optional<Item> firstElement = items.stream().findFirst();

        int expectedSize = 2;
        assertNotNull(items);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, items.size());
        assertEquals(item1.getId(), firstElement.get().getId());
    }

    @Test
    void search_shouldReturnCollectionOfItems() {
        Page<Item> items = itemRepository.search("itemDescription1", PAGE);
        Optional<Item> firstElement = items.stream().findFirst();

        int expectedSize = 1;
        assertNotNull(items);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, items.getTotalElements());
        assertEquals(item1.getId(), firstElement.get().getId());
    }
}