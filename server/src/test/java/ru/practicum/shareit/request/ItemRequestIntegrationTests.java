package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestIntegrationTests {
    private final UserServiceImpl userService;
    private final ItemRequestService itemRequestService;
    private final UserMapper userMapper;
    private final ItemServiceImpl itemService;

    private static final long ID_1 = 1L;
    private final User user = User.builder()
            .id(ID_1)
            .name("userName1")
            .email("userMail1@ya.ru")
            .build();
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(ID_1)
            .description("itemRequestDescription1")
            .created(LocalDate.now().atStartOfDay())
            .build();
    private final ItemFullDto itemFullDto = ItemFullDto
            .builder()
            .id(ID_1)
            .name("itemName1")
            .description("itemDescription1")
            .available(true)
            .requestId(ID_1)
            .owner(user)
            .build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(ID_1)
            .description("itemRequestDescription1")
            .created(LocalDateTime.now()).build();

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testFindItemRequestById() throws ValidationException {
        userService.save(userMapper.toUserDto(user));
        itemRequest.setRequestor(user);
        itemRequestService.save(user.getId(), itemRequestDto);
        itemService.save(ID_1, itemFullDto);

        Optional<ItemRequestDto> itemRequest = itemRequestService.findById(user.getId(), this.itemRequest.getId());

        assertTrue(itemRequest.isPresent());
        assertThat(itemRequest.get().getId(), equalTo(this.itemRequest.getId()));
        assertThat(itemRequest.get().getDescription(), equalTo(this.itemRequest.getDescription()));
    }
}