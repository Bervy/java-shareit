package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.impl.ItemControllerImpl;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemControllerImpl.class)
class ItemControllerTests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemServiceImpl itemService;

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private static final long ID_1 = 1L;
    private static final LocalDateTime CREATED =
            LocalDateTime.of(2022, 12, 5, 1, 1, 1);
    private final User owner = User
            .builder()
            .id(ID_1)
            .name("123")
            .email("123@email.ru").build();
    private final CommentFullDto commentFullDto = CommentFullDto
            .builder()
            .id(ID_1)
            .text("text")
            .authorName("author")
            .created(CREATED)
            .build();
    private final ItemFullDto itemFullDto = ItemFullDto
            .builder()
            .id(ID_1)
            .name("Book")
            .description("Book description")
            .available(true)
            .owner(owner)
            .build();

    @Test
    void findAll() throws Exception {
        when(itemService.findAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemFullDto));
        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, ID_1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemFullDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemFullDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemFullDto.getAvailable())))
                .andExpect(jsonPath("$.[0].owner.id", is(itemFullDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.[0].owner.name", is(itemFullDto.getOwner().getName())))
                .andExpect(jsonPath("$.[0].owner.email", is(itemFullDto.getOwner().getEmail())));
    }

    @Test
    void getByIdTest() throws Exception {
        when(itemService.findById(anyLong(), anyLong()))
                .thenReturn(Optional.of(itemFullDto));
        mockMvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, ID_1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemFullDto.getName())))
                .andExpect(jsonPath("$.description", is(itemFullDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemFullDto.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(itemFullDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(itemFullDto.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(itemFullDto.getOwner().getEmail())));
    }

    @Test
    void createTest() throws Exception {
        when(itemService.save(anyLong(), any()))
                .thenReturn(Optional.of(itemFullDto));
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemFullDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, ID_1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemFullDto.getName())))
                .andExpect(jsonPath("$.description", is(itemFullDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemFullDto.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(itemFullDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(itemFullDto.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(itemFullDto.getOwner().getEmail())));
    }

    @Test
    void updateTest() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(Optional.of(itemFullDto));
        mockMvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemFullDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, ID_1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemFullDto.getName())))
                .andExpect(jsonPath("$.description", is(itemFullDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemFullDto.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(itemFullDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(itemFullDto.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(itemFullDto.getOwner().getEmail())));
    }

    @Test
    void searchTest() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemFullDto));
        mockMvc.perform(get("/items/search?text='Book'")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, ID_1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemFullDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemFullDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemFullDto.getAvailable())))
                .andExpect(jsonPath("$.[0].owner.id", is(itemFullDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.[0].owner.name", is(itemFullDto.getOwner().getName())))
                .andExpect(jsonPath("$.[0].owner.email", is(itemFullDto.getOwner().getEmail())));
    }

    @Test
    void createCommentTest() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentFullDto);
        mockMvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(commentFullDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, ID_1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentFullDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentFullDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentFullDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/1").header(HEADER_USER_ID, 1)).andExpect(status().isOk());
    }
}