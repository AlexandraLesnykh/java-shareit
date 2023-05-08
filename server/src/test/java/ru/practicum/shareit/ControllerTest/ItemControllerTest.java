package ru.practicum.shareit.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.comments.CommentMapper;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        user = new User(1L, "Mary", "mary@yandex.ru");
        item = new Item(1L, "A book", "Amazing book", true, 2L, null,
                null, null, new ArrayList<CommentDto>(), new HashSet<>());
    }

    @Test
    @SneakyThrows
    void createTest() {
        Long ownerId = 2L;
        when(itemService.create(any(), eq(2L))).thenReturn(item);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())));
    }

    @Test
    @SneakyThrows
    void findAllTest() {
        Long ownerId = 2L;
        List<Item> itemList = List.of(item);
        when(itemService.findAll(ownerId)).thenReturn(itemList);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())));
    }

    @Test
    @SneakyThrows
    void findItemTest() {
        Long ownerId = 2L;
        Long id = 1L;
        when(itemService.findItem(id, ownerId)).thenReturn(item);

        mvc.perform(get("/items/{id}", id)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())));
    }

    @Test
    @SneakyThrows
    void updateTest() {
        Long ownerId = 2L;
        Long id = 1L;
        item.setRequestId(1L);
        item.setName("A magazine");
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(itemService.update(any(ItemDto.class), eq(id), eq(ownerId))).thenReturn(item);

        mvc.perform(patch("/items/{id}", id)
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())));
    }

    @Test
    @SneakyThrows
    void searchTest() {
        List<Item> itemList = List.of(item);
        Long id = 1L;
        String text = "book";
        when(itemService.search(any(String.class))).thenReturn(itemList);

        mvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())));
    }

    @Test
    @SneakyThrows
    void addCommentTest() {
        mapper.registerModule(new JavaTimeModule());
        Comment comment = new Comment(1L, "Really interesting book", 1L, 1L, item,
                user, LocalDateTime.now().minusDays(3));
        Long ownerId = 2L;
        Long id = 1L;
        when(itemService.addComment(any(), eq(id), eq(ownerId))).thenReturn(CommentMapper.toCommentDto(comment));
        mvc.perform(post("/items/{id}/comment", id)
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())));
    }

}
