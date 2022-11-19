package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.error.ExceptionDescriptions.FROM_OR_SIZE_LESS_THAN_ZERO;
import static ru.practicum.shareit.error.ExceptionDescriptions.USER_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class ItemRequestUnitTests {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private ItemMapper itemMapper;
    @InjectMocks
    private ItemRequestService itemRequestService;

    private static final long ID_1 = 1L;
    private static final long ID_2 = 2L;
    private static final int FROM = 0;
    private static final int SIZE = 5;
    private static final Pageable PAGE = PageRequest.of(FROM, SIZE);
    private final User user1 = User.builder()
            .id(ID_1)
            .name("userName1")
            .email("userMail1@ya.ru")
            .build();
    private final User user2 = User.builder()
            .id(ID_2)
            .name("userName2")
            .email("userMail2@ya.ru")
            .build();
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(ID_1)
            .description("itemRequestDescription1")
            .created(LocalDateTime.now()).build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(ID_1)
            .description("itemRequestDescription1")
            .created(LocalDateTime.now())
            .build();
    private final Item item = Item.builder()
            .id(ID_1)
            .name("itemName1")
            .description("itemDescription1")
            .available(true)
            .owner(user1).build();
    private final ItemShortDto itemShortDto = ItemShortDto.builder()
            .id(ID_1)
            .name("itemName1")
            .description("itemDescription1")
            .available(true)
            .ownerId(ID_1)
            .requestId(ID_1)
            .build();

    @Test
    void findAllByOwner_shouldCallRepository() {
        when(itemRequestRepository.findAllByRequestorId(user1.getId())).thenReturn(List.of(itemRequest));
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByRequestId(itemRequestDto.getId())).thenReturn(List.of(item));

        List<ItemRequestDto>itemRequestsResult = itemRequestService.findAllByOwner(user1.getId());

        int expectedSize = 1;
        assertNotNull(itemRequestsResult);
        assertEquals(expectedSize, itemRequestsResult.size());
        verify(itemRequestRepository, times(1)).findAllByRequestorId(user1.getId());
    }

    @Test
    void findAllByAnotherUser_shouldCallRepository() {
        List<ItemRequest> items = Collections.singletonList(itemRequest);
        when(itemRequestRepository.findAllByRequestorNotLikeOrderByCreatedAsc(user2, PAGE))
                .thenReturn(new PageImpl<>(items, PAGE, items.size()));
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(itemRepository.findAllByRequestId(itemRequestDto.getId())).thenReturn(List.of(item));

        List<ItemRequestDto>itemRequestsResult = itemRequestService.findAllByAnotherUser(user2.getId(), FROM, SIZE);

        int expectedSize = 1;
        assertNotNull(itemRequestsResult);
        assertEquals(expectedSize, itemRequestsResult.size());
        verify(itemRequestRepository, times(1))
                .findAllByRequestorNotLikeOrderByCreatedAsc(user2, PAGE);
    }

    @Test
    void findById_shouldCallRepository() {
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(itemMapper.toItemShortDto(item)).thenReturn(itemShortDto);
        when(itemRepository.findAllByRequestId(itemRequestDto.getId())).thenReturn(List.of(item));

        Optional<ItemRequestDto> itemRequestDtoResult = itemRequestService.findById(user1.getId(), itemRequest.getId());

        assertTrue(itemRequestDtoResult.isPresent());
        assertEquals(ID_1, itemRequestDtoResult.get().getId());
        verify(itemRequestRepository, times(1)).findById(ID_1);
    }

    @Test
    void save_shouldCallRepository() {
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);
        when(itemRequestMapper.fromItemRequestDto(itemRequestDto, user1)).thenReturn(itemRequest);
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        Optional<ItemRequestDto> itemRequestDtoResult = itemRequestService.save(user1.getId(), itemRequestDto);

        assertTrue(itemRequestDtoResult.isPresent());
        assertEquals(ID_1, itemRequestDtoResult.get().getId());
        verify(itemRequestRepository, times(1)).save(itemRequest);
    }

    @Test
    void save_shouldThrowValidationException_fromOrSizeLessThanZero() throws ValidationException {
        Exception exception = assertThrows(ValidationException.class, () ->
                itemRequestService.findAllByAnotherUser(ID_1, -1, SIZE));

        assertEquals(FROM_OR_SIZE_LESS_THAN_ZERO.getTitle(), exception.getMessage());
    }

    @Test
    void save_shouldThrowNotFoundException_userNotFound() throws ValidationException {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () ->
                itemRequestService.findAllByOwner(ID_1));

        assertEquals(USER_NOT_FOUND.getTitle(), exception.getMessage());
    }
}