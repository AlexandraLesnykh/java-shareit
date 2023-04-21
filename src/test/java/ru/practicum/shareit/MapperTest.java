package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.CommentMapper;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

public class MapperTest {
    private User user;
    private User user1;
    private Item item;
    private Booking booking;
    private Comment comment;
    private ItemRequest itemRequest;


    @BeforeEach
    void setUp() {
        user = new User(1L, "Mary", "mary@yandex.ru");
        user1 = new User(2L, "Dom", "dom@google.com");
        item = new Item(1L, "A book", "Amazing book", true, 2L, null,
                null, null, new ArrayList<CommentDto>(), new HashSet<>());
        booking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(4), 1L,
                item, 1L, user, BookingStatus.APPROVED);
        comment = new Comment(1L, "Really interesting book", 1L, 1L, item,
                user, LocalDateTime.now().minusDays(3));
        itemRequest = new ItemRequest(1L, "Need a book", 2L, LocalDateTime.now(), user1,
                new ArrayList<Item>());
    }

    @Test
    void toItemDtoTest() {
        item.setRequestId(5L);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Assertions.assertAll(
                () -> Assertions.assertEquals(itemDto.getId(), item.getId()),
                () -> Assertions.assertEquals(itemDto.getName(), item.getName()),
                () -> Assertions.assertEquals(itemDto.getDescription(), item.getDescription())
        );
    }

    @Test
    void toBookingDtoTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        Assertions.assertAll(
                () -> Assertions.assertEquals(bookingDto.getId(), booking.getId()),
                () -> Assertions.assertEquals(bookingDto.getStatus(), booking.getStatus()),
                () -> Assertions.assertEquals(bookingDto.getItem().getId(), booking.getItemId()),
                () -> Assertions.assertEquals(bookingDto.getBooker().getId(), booking.getBookerId())
        );
    }

    @Test
    void toCommentDtoTest() {
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        Assertions.assertAll(
                () -> Assertions.assertEquals(commentDto.getId(), comment.getId()),
                () -> Assertions.assertEquals(commentDto.getText(), comment.getText())
        );
    }

    @Test
    void toItemRequestDtoTest() {
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        Assertions.assertAll(
                () -> Assertions.assertEquals(itemRequestDto.getId(), itemRequest.getId()),
                () -> Assertions.assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription()),
                () -> Assertions.assertEquals(itemRequestDto.getItems(),
                        itemRequest.getItems())

        );
    }
}


