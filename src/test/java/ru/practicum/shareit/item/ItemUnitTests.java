package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.error.ExceptionDescriptions.*;

@ExtendWith(MockitoExtension.class)
class ItemUnitTests {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private ItemServiceImpl itemService;

    private static final long ID_1 = 1L;
    private static final int FROM = 0;
    private static final int SIZE = 5;
    private User user1 = new User();
    private Item item1 = new Item();
    private ItemFullDto itemFullDto = new ItemFullDto();
    private Booking booking = new Booking();
    private ItemRequest itemRequest = new ItemRequest();
    private Comment comment = new Comment();
    private CommentFullDto commentFullDto = new CommentFullDto();
    private CommentDto commentDto = new CommentDto();

    @BeforeEach
    public void init() {
        comment = Comment.builder()
                .id(ID_1)
                .text("Comment")
                .item(item1)
                .author(user1)
                .created(LocalDateTime.now())
                .build();
        commentFullDto = CommentFullDto.builder()
                .id(ID_1)
                .text("Comment")
                .authorName(user1.getName())
                .created(LocalDateTime.now())
                .build();
        commentDto = CommentDto.builder()
                .text("Comment")
                .build();
        itemRequest = ItemRequest.builder()
                .id(ID_1)
                .description("itemRequestDescription1")
                .requestor(user1)
                .created(LocalDateTime.of(2022, 11, 5, 1, 1))
                .build();
        user1 = User.builder()
                .id(ID_1)
                .name("userName1")
                .email("userMail1@ya.ru").build();
        item1 = Item.builder()
                .id(ID_1)
                .name("itemName1")
                .description("itemDescription1")
                .available(true)
                .owner(user1).build();
        itemFullDto = ItemFullDto.builder()
                .id(ID_1)
                .name("itemName1")
                .description("itemDescription1")
                .available(true)
                .requestId(ID_1)
                .owner(user1).build();
        booking = Booking.builder()
                .id(ID_1)
                .start(LocalDateTime.of(2022, 11, 5, 1, 1))
                .end(LocalDateTime.of(2022, 11, 7, 1, 1))
                .booker(user1)
                .item(item1)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void findAll_shouldCallRepository() {
        List<Item> items = Collections.singletonList(item1);
        when(itemRepository.findAllByOwnerId(anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(items, PageRequest.of(FROM / SIZE, SIZE), items.size()));
        when(bookingRepository.findAllByItemIdOrderByStartAsc(item1.getId())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByItemIdOrderByStartDesc(item1.getId())).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(ID_1)).thenReturn(List.of(comment));
        when(itemMapper.toItemFullDto(item1)).thenReturn(itemFullDto);

        List<ItemFullDto> itemsResult = itemService.findAll(user1.getId(), FROM, SIZE);

        int expectedSize = 1;
        assertNotNull(itemsResult);
        assertEquals(expectedSize, itemsResult.size());
        verify(itemRepository, times(1))
                .findAllByOwnerId(user1.getId(), PageRequest.of(FROM / SIZE, SIZE));
    }

    @Test
    void findById_shouldCallRepository() {
        when(bookingRepository.findAllByItemIdOrderByStartAsc(item1.getId())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByItemIdOrderByStartDesc(item1.getId())).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(ID_1)).thenReturn(List.of(comment));
        when(itemRepository.findById(ID_1)).thenReturn(Optional.ofNullable(item1));
        when(commentMapper.toCommentFullDto(comment)).thenReturn(commentFullDto);
        when(itemMapper.toItemFullDto(item1)).thenReturn(itemFullDto);

        Optional<ItemFullDto> itemResult = itemService.findById(item1.getId(), user1.getId());

        assertTrue(itemResult.isPresent());
        assertEquals(ID_1, itemResult.get().getId());
        verify(itemRepository, times(1)).findById(ID_1);
    }

    @Test
    void save_shouldCallRepository() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(itemMapper.fromItemDto(itemFullDto)).thenReturn(item1);
        when(itemRequestRepository.findById(ID_1)).thenReturn(Optional.ofNullable(itemRequest));
        when(itemMapper.toItemFullDto(item1)).thenReturn(itemFullDto);
        when(itemRepository.save(item1)).thenReturn(item1);

        Optional<ItemFullDto> itemResult = itemService.save(user1.getId(), itemFullDto);

        assertTrue(itemResult.isPresent());
        assertEquals(item1.getId(), itemResult.get().getId());
        verify(itemRepository, times(1)).save(item1);
    }

    @Test
    void update_shouldCallRepository() {
        when(itemRepository.findById(ID_1)).thenReturn(Optional.ofNullable(item1));
        when(itemMapper.toItemFullDto(item1)).thenReturn(itemFullDto);
        when(itemRepository.save(item1)).thenReturn(item1);

        Optional<ItemFullDto> itemResult = itemService.update(user1.getId(), ID_1, itemFullDto);

        assertTrue(itemResult.isPresent());
        assertEquals(ID_1, itemResult.get().getId());
        verify(itemRepository, times(1)).save(item1);
    }

    @Test
    void delete_shouldCallRepository() {
        when(itemRepository.findById(ID_1)).thenReturn(Optional.ofNullable(item1));

        itemService.delete(user1.getId(), ID_1);

        verify(itemRepository).delete(item1);
    }

    @Test
    void search_shouldCallRepository() {
        List<Item> items = Collections.singletonList(item1);
        when(itemMapper.toItemFullDto(item1)).thenReturn(itemFullDto);
        when(itemRepository.search("text", PageRequest.of(FROM / SIZE, SIZE)))
                .thenReturn(new PageImpl<>(items, PageRequest.of(FROM / SIZE, SIZE), items.size()));

        List<ItemFullDto> itemsResult = itemService.search("text", FROM, SIZE);

        int expectedSize = 1;
        assertNotNull(itemsResult);
        assertEquals(expectedSize, itemsResult.size());
        verify(itemRepository, times(1))
                .search("text", PageRequest.of(FROM / SIZE, SIZE));
    }

    @Test
    void addComment_shouldCallRepository() {
        when(bookingRepository.findAllByBookerId(ID_1)).thenReturn(List.of(booking));
        when(itemRepository.findById(ID_1)).thenReturn(Optional.ofNullable(item1));
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(commentMapper.fromCommentDto(commentDto, item1, user1)).thenReturn(comment);
        when(commentMapper.toCommentFullDto(comment)).thenReturn(commentFullDto);
        when(commentRepository.save(comment)).thenReturn(comment);

        CommentFullDto commentFullDtoResult = itemService.addComment(user1.getId(), ID_1, commentDto);

        assertNotNull(commentFullDtoResult);
        assertEquals(ID_1, commentFullDtoResult.getId());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void search_shouldReturnNewArraylist() throws ValidationException {
        List<ItemFullDto> items = itemService.search("", FROM, SIZE);

        assertEquals(0, items.size());
    }

    @Test
    void addComment_shouldThrowValidationException() throws ValidationException {
        commentDto.setText("");
        Exception exception = assertThrows(ValidationException.class, () ->
                itemService.addComment(ID_1, ID_1, commentDto));

        assertEquals(EMPTY_NAME.getTitle(), exception.getMessage());
    }

    @Test
    void addComment_shouldThrowValidationException_BookingStatusNotApproved() throws ValidationException {
        when(bookingRepository.findAllByBookerId(ID_1)).thenReturn(List.of(booking));
        booking.setStatus(BookingStatus.CANCELED);
        Exception exception = assertThrows(ValidationException.class, () ->
                itemService.addComment(ID_1, ID_1, commentDto));

        assertEquals(FORBIDDEN_TO_ADD_COMMENTS.getTitle(), exception.getMessage());
    }

    @Test
    void save_shouldThrowNotFoundException_userNotFound() throws ValidationException {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () ->
                itemService.save(ID_1, itemFullDto));

        assertEquals(USER_NOT_FOUND.getTitle(), exception.getMessage());
    }

    @Test
    void save_shouldThrowValidationException_availableNotFound() throws ValidationException {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        itemFullDto.setAvailable(null);
        Exception exception = assertThrows(ValidationException.class, () ->
                itemService.save(ID_1, itemFullDto));

        assertEquals(AVAILABLE_NOT_FOUND.getTitle(), exception.getMessage());
    }

    @Test
    void save_shouldThrowValidationException_nameBlank() throws ValidationException {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        itemFullDto.setName("");
        Exception exception = assertThrows(ValidationException.class, () ->
                itemService.save(ID_1, itemFullDto));

        assertEquals(EMPTY_NAME.getTitle(), exception.getMessage());
    }

    @Test
    void save_shouldThrowValidationException_descriptionNotFound() throws ValidationException {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        itemFullDto.setDescription(null);
        Exception exception = assertThrows(ValidationException.class, () ->
                itemService.save(ID_1, itemFullDto));

        assertEquals(DESCRIPTION_NOT_FOUND.getTitle(), exception.getMessage());
    }

    @Test
    void update_shouldThrowNotFoundException_ownerNotFound() throws ValidationException {
        when(itemRepository.findById(ID_1)).thenReturn(Optional.ofNullable(item1));
        Exception exception = assertThrows(NotFoundException.class, () ->
                itemService.update(3, ID_1, itemFullDto));

        assertEquals(OWNER_NOT_FOUND.getTitle(), exception.getMessage());
    }

    @Test
    void save_shouldThrowValidationException_fromOrSizeLessThanZero() throws ValidationException {
        Exception exception = assertThrows(ValidationException.class, () ->
                itemService.findAll(ID_1, -1, SIZE));

        assertEquals(FROM_OR_SIZE_LESS_THAN_ZERO.getTitle(), exception.getMessage());
    }
}