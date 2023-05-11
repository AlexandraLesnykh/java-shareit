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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
        mapper.registerModule(new JavaTimeModule());
        user = new User(1L, "Mary", "mary@yandex.ru");
        item = new Item(1L, "A book", "Amazing book", true, 2L, null,
                null, null, new ArrayList<CommentDto>(), new HashSet<>());
        booking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(4), 1L,
                item, 1L, user, BookingStatus.APPROVED);
    }

    @Test
    @SneakyThrows
    void createTest() {
        Long ownerId = 1L;
        when(bookingService.create(any(), eq(1L))).thenReturn(BookingMapper.toBookingDto(booking));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(booking.getItemId()), Long.class));
    }

    @Test
    @SneakyThrows
    void updateTest() {
        Long id = 1L;
        Long ownerId = 1L;
        boolean approved = false;
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingService.update(id, approved, ownerId)).thenReturn(BookingMapper.toBookingDto(booking));

        mvc.perform(patch("/bookings/{id}", id)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", String.valueOf(approved))
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(String.valueOf(booking.getStatus()))));
    }

    @Test
    @SneakyThrows
    void getBookingByIdTest() {
        Long id = 1L;
        Long ownerId = 1L;
        when(bookingService.getBookingById(id, ownerId)).thenReturn(BookingMapper.toBookingDto(booking));

        mvc.perform(get("/bookings/{id}", id)
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(String.valueOf(booking.getStatus()))));
    }

    @Test
    @SneakyThrows
    void getBookingByIdTestFail() {
        Long id = 10L;
        Long ownerId = 10L;
        when(bookingService.getBookingById(id, ownerId)).thenReturn(null);

        mvc.perform(get("/bookings/{id}", id)
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    void findAllWithOwnerTest() {
        Long id = 1L;
        Long ownerId = 1L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;
        List<BookingDto> list = List.of(BookingMapper.toBookingDto(booking));
        when(bookingService.findAllWithOwner(state, ownerId, PageRequest.of(from / size, size))).thenReturn(list);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(booking.getStatus()))));
    }

    @Test
    @SneakyThrows
    void findAllTest() {
        Long id = 1L;
        Long ownerId = 1L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;
        List<BookingDto> list = List.of(BookingMapper.toBookingDto(booking));
        when(bookingService.findAll(state, ownerId, PageRequest.of(from / size, size))).thenReturn(list);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(booking.getStatus()))));
    }
}
