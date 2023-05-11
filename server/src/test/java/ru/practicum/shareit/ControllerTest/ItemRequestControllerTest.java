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
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private User user;
    private User user1;
    private Item item;
    private Booking booking;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();
        mapper.registerModule(new JavaTimeModule());
        user = new User(1L, "Mary", "mary@yandex.ru");
        user1 = new User(2L, "Dom", "dom@google.com");
        item = new Item(1L, "A book", "Amazing book", true, 2L, null,
                null, null, new ArrayList<CommentDto>(), new HashSet<>());
        booking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(4), 1L,
                item, 1L, user, BookingStatus.APPROVED);
        itemRequest = new ItemRequest(1L, "Need a book", 2L, LocalDateTime.now(), user1,
                new ArrayList<Item>());
    }

    @Test
    @SneakyThrows
    void createTest() {
        Long ownerId = 1L;
        when(itemRequestService.create(any(), eq(1L))).thenReturn(itemRequest);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));
    }

    @Test
    @SneakyThrows
    void getAllRequestsTest() {
        Long ownerId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<ItemRequestDto> list = List.of(ItemRequestMapper.toItemRequestDto(itemRequest));
        when(itemRequestService.getAllRequests(eq(ownerId), any())).thenReturn(list);
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())));
    }

    @Test
    @SneakyThrows
    void getAllRequestsByOwnerTest() {
        Long ownerId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<ItemRequestDto> list = List.of(ItemRequestMapper.toItemRequestDto(itemRequest));
        when(itemRequestService.getAllRequestsByOwner(eq(ownerId), any())).thenReturn(list);
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())));
    }

    @Test
    @SneakyThrows
    void getRequestsByIdTest() {
        Long ownerId = 1L;
        Long id = 1L;
        Integer from = 0;
        Integer size = 10;
        when(itemRequestService.getRequestsById(ownerId, id)).thenReturn(ItemRequestMapper.toItemRequestDto(itemRequest));

        mvc.perform(get("/requests/{id}", id)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));
    }

}
