package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTests {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    private final User user1 = User.builder()
            .id(1)
            .name("User1")
            .email("User1@host.com")
            .build();
    private final User user2 = User.builder()
            .id(2)
            .name("User2")
            .email("User2@host.com")
            .build();
    private final ItemRequest itemRequest1 = ItemRequest.builder()
            .id(1L)
            .description("Description1")
            .requestor(user1)
            .created(LocalDateTime.now())
            .build();
    private final ItemRequest itemRequest2 = ItemRequest
            .builder()
            .id(2L)
            .description("Description2")
            .requestor(user2)
            .created(LocalDateTime.now().plusDays(1))
            .build();

    @BeforeEach
    void saveData() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
    }

    @Test
    void findAllByRequestorId_shouldReturnCollectionOfItemRequests() {
        Collection<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(1L);
        Optional<ItemRequest> firstElement = itemRequests.stream().findFirst();

        int expectedSize = 1;
        assertNotNull(itemRequests);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, itemRequests.size());
        assertEquals(itemRequest1.getId(), firstElement.get().getId());
    }

    @Test
    void findAllByRequestorNotLikeOrderByCreatedAsc_shouldReturnCollectionOfItemRequests() {
        Sort sortById = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(0, 5, sortById);
        Page<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorNotLikeOrderByCreatedAsc(user1,page);
        Optional<ItemRequest> firstElement = itemRequests.stream().findFirst();

        int expectedSize = 1;
        assertNotNull(itemRequests);
        assertTrue(firstElement.isPresent());
        assertEquals(expectedSize, itemRequests.getTotalElements());
        assertEquals(itemRequest2.getId(), firstElement.get().getId());
    }
}