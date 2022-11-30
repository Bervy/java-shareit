package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemIntegrationTests {
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;
    private final BookingService bookingService;
    private final UserMapper userMapper;

    private final LocalDateTime start = LocalDateTime.of(2022, 11, 5, 1, 1);
    private final LocalDateTime end = LocalDateTime.of(2022, 11, 7, 1, 1);
    private static final long ID_1 = 1L;
    private static final long ID_2 = 2L;
    private static final int FROM = 0;
    private static final int SIZE = 5;
    private final User owner = User.builder()
            .name("userName1")
            .email("userMail1@ya.ru")
            .build();
    private final ItemFullDto itemFullDto = ItemFullDto.builder()
            .name("itemName1")
            .description("itemDescription1")
            .available(true)
            .owner(owner)
            .build();
    private final BookingDto booking = BookingDto.builder()
            .itemId(ID_1)
            .start(start)
            .end(end)
            .build();
    private final User booker = User.builder()
            .name("userName2")
            .email("userMail2@ya.ru")
            .build();
    private final CommentDto commentDto = CommentDto.builder()
            .text("Comment")
            .build();
    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(ID_1)
            .start(start)
            .end(end).build();

    @Test
    void search_shouldReturnCollectionOfItems() throws ValidationException {
        userService.save(userMapper.toUserDto(owner));
        itemService.save(ID_1, itemFullDto);

        Collection<ItemFullDto> items = itemService.search("itemDescription1", FROM, SIZE);

        int expectedSize = 1;
        assertThat(items, hasSize(expectedSize));
        assertThat(items.stream().findFirst().isPresent(), is(true));
        assertThat(items.stream().findFirst().get().getId(), equalTo(1L));
        assertThat(items.stream().findFirst().get().getName(), equalTo(itemFullDto.getName()));
        assertThat(items.stream().findFirst().get().getDescription(), equalTo(itemFullDto.getDescription()));
    }

    @Test
    void findAll_shouldReturnCollectionOfItems() throws ValidationException {
        userService.save(userMapper.toUserDto(owner));
        userService.save(userMapper.toUserDto(booker));
        itemService.save(ID_1, itemFullDto);
        bookingService.create(ID_2, booking);

        Collection<ItemFullDto> items = itemService.findAll(ID_1, FROM, SIZE);

        int expectedSize = 1;
        assertThat(items, hasSize(expectedSize));
        assertThat(items.stream().findFirst().isPresent(), is(true));
        assertThat(items.stream().findFirst().get().getId(), equalTo(1L));
        assertThat(items.stream().findFirst().get().getName(), equalTo(itemFullDto.getName()));
        assertThat(items.stream().findFirst().get().getDescription(), equalTo(itemFullDto.getDescription()));
    }

    @Test
    void addComment_shouldReturnComment() throws ValidationException {
        userService.save(userMapper.toUserDto(owner));
        userService.save(userMapper.toUserDto(booker));
        itemService.save(ID_1, itemFullDto);
        bookingService.create(ID_2, bookingDto);
        bookingService.confirmation(ID_1, ID_1, true);

        CommentFullDto commentFullDto = itemService.addComment(ID_2, ID_1, commentDto);

        assertThat(commentFullDto.getAuthorName(), is(booker.getName()));
    }
}