package ru.practicum.shareit.ServiceTest.ItemTest;

import org.junit.jupiter.api.*;
import ru.practicum.shareit.ServiceTest.BookingTest.BookingServiceTestUtils;
import ru.practicum.shareit.ServiceTest.UserTest.UserServiceTestUtils;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.CommentRepository;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.exeptions.BadRequestException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemServiceTest {
    private ItemServiceImpl itemService;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private UserService userService;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        userService = new UserServiceImpl(userRepository);
        itemService = new ItemServiceImpl(itemRepository, bookingRepository, commentRepository, userService);
    }

    @Test
    @Order(1)
    @DisplayName("1.Get all items with ownerId")
    void findAllTest() {
        Long ownerId = 1L;
        Long id2 = 2L;
        Long id3 = 3L;
        when(itemRepository.findAll()).thenReturn(ItemServiceTestUtils.getItems());
        when(itemRepository.findById(id2)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id2)));
        when(itemRepository.findById(id3)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id3)));

        List<Booking> bookings = new ArrayList<>();
        when(bookingRepository.findBookingByItemId(id2)).thenReturn(bookings);
        when(bookingRepository.findBookingByItemId(id3)).thenReturn(bookings);
        List<Item> items = itemService.findAll(ownerId);

        Assertions.assertEquals(2, items.size());
    }

    @Test
    @Order(2)
    @DisplayName("2.Find item by id")
    void findItemTest() {
        Long id = 2L;
        Long ownerId = 1L;
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id)));
        Item item = itemService.findItem(id, ownerId);

        Assertions.assertEquals("An umbrella", item.getName());
        Assertions.assertEquals("Big umbrella", item.getDescription());
    }

    @Test
    @Order(3)
    @DisplayName("3.Find empty items list user 4")
    void findEmptyItemsListTest() {
        Long ownerId = 4L;
        Long id2 = 2L;
        Long id3 = 3L;
        when(itemRepository.findAll()).thenReturn(ItemServiceTestUtils.getItems());
        when(itemRepository.findById(id2)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id2)));
        when(itemRepository.findById(id3)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id3)));

        List<Booking> bookings = new ArrayList<>();
        when(bookingRepository.findBookingByItemId(id2)).thenReturn(bookings);
        when(bookingRepository.findBookingByItemId(id3)).thenReturn(bookings);
        List<Item> items = itemService.findAll(ownerId);

        Assertions.assertEquals(0, items.size());
    }

    @Test
    @Order(4)
    @DisplayName("4.Find item with id that doesn't exist")
    void shouldThrowExceptionWhileFindingItemWithWrongId() {
        Long id = 100L;
        Long ownerId = 2L;
        when(itemRepository.findById(id)).thenThrow(new ObjectNotFoundException("Wrong ID"));

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> itemService.findItem(id, ownerId));
        Assertions.assertEquals("Wrong ID", exception.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("5.Find user with ownerId that doesn't exist")
    void shouldThrowExceptionWhileFindingUserWithWrongId() {
        Long ownerId = 100L;
        Long id = 2L;
        when(itemRepository.findById(id)).thenThrow(new ObjectNotFoundException("Wrong ID"));
        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> itemService.findItem(id, ownerId));
        Assertions.assertEquals("Wrong ID", exception.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("6.Create item")
    void createTest() throws BadRequestException {

        Long ownerId = 1L;
        List<Item> itemsWithoutIds = ItemServiceTestUtils.getItemsWithoutIds();
        when(itemRepository.save(itemsWithoutIds.get(0))).thenReturn(ItemServiceTestUtils.getItem(1L));
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(UserServiceTestUtils.getUser(ownerId)));
        Item item = itemService.create(itemsWithoutIds.get(0), ownerId);
        Assertions.assertEquals(1, item.getId());
        Assertions.assertEquals("A book", item.getName());
    }

    @Test
    @Order(7)
    @DisplayName("7.Create item fail wrong ownerId")
    void shouldThrowExceptionWhileCreatingItemWrongOwnerId() {
        Long ownerId = 100L;
        List<Item> itemsWithoutIds = ItemServiceTestUtils.getItemsWithoutIds();
        when(itemRepository.save(itemsWithoutIds.get(0))).thenReturn(ItemServiceTestUtils.getItem(1L));
        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> itemService.create(itemsWithoutIds.get(0), ownerId));
        Assertions.assertEquals("Wrong ID", exception.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("8. Update test")
    void updateTest() {
        Long id = 2L;
        Long ownerId = 1L;
        Item item = new Item();
        item.setName("A colorful umbrella");
        item.setDescription("5 colors umbrella");
        item.setAvailable(false);
        item.setOwner(ownerId);
        item.setRequestId(0L);
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(UserServiceTestUtils.getUser(id)));
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id)));
        when(itemRepository.save(item)).thenReturn(ItemServiceTestUtils.getItemAfterUpdate(item, id));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemService.update(itemDto, id, ownerId);
        Item item1 = itemService.findItem(id, ownerId);

        Assertions.assertEquals(2, item1.getId());
        Assertions.assertEquals("A colorful umbrella", item1.getName());
        Assertions.assertEquals("5 colors umbrella", item1.getDescription());
    }

    @Test
    @Order(9)
    @DisplayName("9. Update test fail wrong id")
    void shouldThrowExceptionWhileUpdatingItemWrongId() {
        Long id = 100L;
        Long ownerId = 1L;
        Item item = new Item();
        item.setName("A colorful umbrella");
        item.setDescription("5 colors umbrella");
        item.setAvailable(false);
        item.setOwner(ownerId);
        item.setRequestId(0L);
        when(itemRepository.findById(id)).thenThrow(new ObjectNotFoundException("Wrong ID"));
        when(itemRepository.save(item)).thenThrow(new ObjectNotFoundException("Wrong ID"));
        ItemDto itemDto = ItemMapper.toItemDto(item);

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> itemService.update(itemDto, id, ownerId));
        Assertions.assertEquals("Wrong ID", exception.getMessage());
    }

    @Test
    @Order(10)
    @DisplayName("10. Update test fail wrong ownerId")
    void shouldThrowExceptionWhileUpdatingItemWrongOwnerId() {
        Long id = 2L;
        Long ownerId = 4L;
        Item item = new Item();
        item.setName("A colorful umbrella");
        item.setDescription("5 colors umbrella");
        item.setAvailable(false);
        item.setOwner(ownerId);
        item.setRequestId(0L);
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(UserServiceTestUtils.getUser(id)));
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id)));
        when(itemRepository.save(item)).thenThrow(new ObjectNotFoundException("Wrong owner id"));
        ItemDto itemDto = ItemMapper.toItemDto(item);

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> itemService.update(itemDto, id, ownerId));
        Assertions.assertEquals("Wrong owner id", exception.getMessage());
    }

    @Test
    @Order(11)
    @DisplayName("11. Update name test")
    void updateNameTest() {
        Long id = 2L;
        Long ownerId = 1L;
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(UserServiceTestUtils.getUser(id)));
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id)));
        Item item = itemService.findItem(id, ownerId);
        item.setName("A colorful umbrella");
        item.setDescription(null);
        item.setAvailable(true);
        item.setOwner(ownerId);
        item.setRequestId(0L);
        when(itemRepository.save(item)).thenReturn(ItemServiceTestUtils.getItemAfterUpdate(item, id));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemService.update(itemDto, id, ownerId);
        Item item1 = itemService.findItem(id, ownerId);

        Assertions.assertEquals(2, item1.getId());
        Assertions.assertEquals("A colorful umbrella", item1.getName());
    }

    @Test
    @Order(12)
    @DisplayName("12. Update description test")
    void updateDescriptionTest() {
        Long id = 2L;
        Long ownerId = 1L;
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(UserServiceTestUtils.getUser(id)));
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id)));
        Item item = itemService.findItem(id, ownerId);
        item.setName(null);
        item.setDescription("5 colors umbrella");
        item.setAvailable(true);
        item.setOwner(ownerId);
        item.setRequestId(0L);
        when(itemRepository.save(item)).thenReturn(ItemServiceTestUtils.getItemAfterUpdate(item, id));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemService.update(itemDto, id, ownerId);
        Item item1 = itemService.findItem(id, ownerId);

        Assertions.assertEquals(2, item1.getId());
        Assertions.assertEquals("5 colors umbrella", item1.getDescription());
    }

    @Test
    @Order(13)
    @DisplayName("13. Search test")
    void searchTest() {
        String text = "umBrelLa";
        when(itemRepository.findAll()).thenReturn(ItemServiceTestUtils.getItems());
        List<Item> itemList = itemService.search(text);
        Assertions.assertEquals(1, itemList.size());
    }

    @Test
    @Order(14)
    @DisplayName("14. Search wrong text test")
    void searchEmptyListTest() {
        String text = "car";
        when(itemRepository.findAll()).thenReturn(ItemServiceTestUtils.getItems());
        List<Item> itemList = itemService.search(text);
        Assertions.assertEquals(0, itemList.size());
    }

    @Test
    @Order(15)
    @DisplayName("15. Add comment")
    void addCommentTest() throws BadRequestException {
        Long ownerId = 1L;
        Long itemId = 1L;
        List<Comment> commentsWithoutIds = CommentUtils.getCommentsWithoutIds();
        Comment comment = commentsWithoutIds.get(0);
        Optional<Booking> booking = Optional.ofNullable(BookingServiceTestUtils.getBooking(1L));
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(UserServiceTestUtils.getUser(ownerId)));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(eq(ownerId), eq(itemId), any(LocalDateTime.class)))
                .thenReturn(booking);
        Comment comment0 = CommentUtils.getComments().get(0);
        comment0.setAuthor(UserServiceTestUtils.getUser(ownerId));
        when(commentRepository.save(comment)).thenReturn(comment0);
        CommentDto commentDto = itemService.addComment(comment, itemId, ownerId);
        Assertions.assertEquals(1, commentDto.getId());
        Assertions.assertEquals("Really interesting book", commentDto.getText());
    }

  /*  @Test
    @Order(16)
    @DisplayName("16. Add comment fail wrong id")
    void shouldThrowExceptionAddCommentWithWrongId() {
        Long ownerId = 2L;
        Long itemId = 1L;
        List<Comment> commentsWithoutIds = CommentUtils.getCommentsWithoutIds();
        Comment comment = commentsWithoutIds.get(0);
        Optional<Booking> booking = Optional.ofNullable(BookingServiceTestUtils.getBooking(1L));
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(UserServiceTestUtils.getUser(ownerId)));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(eq(ownerId), eq(itemId), any(LocalDateTime.class)))
                .thenAnswer(invocationOnMock -> new ValidationException("Wrong id"));
     //   Comment comment0 = CommentUtils.getComments().get(0);
       // comment0.setAuthor(UserServiceTestUtils.getUser(ownerId));
       // when(commentRepository.save(comment)).thenAnswer(invocationOnMock -> new ValidationException("Wrong id"));

        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemService.addComment(comment, itemId, ownerId));
        Assertions.assertEquals("Wrong id", exception.getMessage());
    }*/

    @Test
    @Order(17)
    @DisplayName("17. Add comment fail empty text")
    void shouldThrowExceptionAddCommentEmptyText() {
        Long ownerId = 1L;
        Long itemId = 1L;
        List<Comment> commentsWithoutIds = CommentUtils.getCommentsWithoutIds();
        Comment comment = commentsWithoutIds.get(0);
        comment.setText("");
        Optional<Booking> booking = Optional.ofNullable(BookingServiceTestUtils.getBooking(1L));
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(UserServiceTestUtils.getUser(ownerId)));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(ownerId, itemId, LocalDateTime.now()))
                .thenReturn(booking);
        comment.setAuthor(UserServiceTestUtils.getUser(ownerId));
        when(commentRepository.save(comment)).thenReturn(CommentUtils.getComment(1L));

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> itemService.addComment(comment, itemId, ownerId));
        Assertions.assertEquals("Text shouldn't be empty", exception.getMessage());
    }

    @Test
    @Order(18)
    @DisplayName("18.Find item by id with booking")
    void findItemWithBookingTest() {
        Long id = 2L;
        Long ownerId = 1L;
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id)));
        List<Booking> bookings = List.of(BookingServiceTestUtils.getBooking(1L), BookingServiceTestUtils.getBooking(2L));
        when(bookingRepository.findBookingByItemId(id)).thenReturn(bookings);
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(eq(id), eq(BookingStatus.APPROVED),
                any(LocalDateTime.class))).thenReturn(Optional.ofNullable(BookingServiceTestUtils.getBooking(1L)));
        Booking nextBooking = BookingServiceTestUtils.getBooking(2L);
        nextBooking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStart(eq(id), eq(BookingStatus.APPROVED),
                any(LocalDateTime.class))).thenReturn(Optional.of(nextBooking));
        Item item = itemService.findItem(id, ownerId);

        Assertions.assertEquals("An umbrella", item.getName());
        Assertions.assertEquals("Big umbrella", item.getDescription());
        Assertions.assertEquals(1L, item.getLastBooking().getId());
        Assertions.assertEquals(2L, item.getNextBooking().getId());
    }

    @Test
    @Order(19)
    @DisplayName("19.Find item by id with last booking null")
    void findItemWithLastBookingNullTest() {
        Long id = 2L;
        Long ownerId = 1L;
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id)));
        List<Booking> bookings = List.of(BookingServiceTestUtils.getBooking(1L), BookingServiceTestUtils.getBooking(2L));
        when(bookingRepository.findBookingByItemId(id)).thenReturn(bookings);
        Booking nextBooking = BookingServiceTestUtils.getBooking(2L);
        nextBooking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStart(eq(id), eq(BookingStatus.APPROVED),
                any(LocalDateTime.class))).thenReturn(Optional.of(nextBooking));
        Item item = itemService.findItem(id, ownerId);

        Assertions.assertEquals("An umbrella", item.getName());
        Assertions.assertEquals("Big umbrella", item.getDescription());
        Assertions.assertNull(item.getLastBooking());
        Assertions.assertEquals(2L, item.getNextBooking().getId());
    }

    @Test
    @Order(20)
    @DisplayName("20.Find item by id with with next booking null")
    void findItemWithNextBookingNullTest() {
        Long id = 2L;
        Long ownerId = 1L;
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id)));
        List<Booking> bookings = List.of(BookingServiceTestUtils.getBooking(1L), BookingServiceTestUtils.getBooking(2L));
        when(bookingRepository.findBookingByItemId(id)).thenReturn(bookings);
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(eq(id), eq(BookingStatus.APPROVED),
                any(LocalDateTime.class))).thenReturn(Optional.ofNullable(BookingServiceTestUtils.getBooking(1L)));
        Item item = itemService.findItem(id, ownerId);

        Assertions.assertEquals("An umbrella", item.getName());
        Assertions.assertEquals("Big umbrella", item.getDescription());
        Assertions.assertEquals(1L, item.getLastBooking().getId());
        Assertions.assertNull(item.getNextBooking());
    }

    @Test
    @Order(21)
    @DisplayName("21.Find item by id with next&last booking null")
    void findItemWithNextAndLastBookingNullTest() {
        Long id = 2L;
        Long ownerId = 1L;
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id)));
        List<Booking> bookings = List.of(BookingServiceTestUtils.getBooking(1L), BookingServiceTestUtils.getBooking(2L));
        when(bookingRepository.findBookingByItemId(id)).thenReturn(bookings);
        Item item = itemService.findItem(id, ownerId);

        Assertions.assertEquals("An umbrella", item.getName());
        Assertions.assertEquals("Big umbrella", item.getDescription());
        Assertions.assertNull(item.getLastBooking());
        Assertions.assertNull(item.getNextBooking());
    }

    @Test
    @Order(22)
    @DisplayName("22.Find item by id wrong owner Id")
    void findItemWithWrongOwnerIdTest() {
        Long id = 2L;
        Long ownerId = 3L;
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id)));
        List<Booking> bookings = List.of(BookingServiceTestUtils.getBooking(1L), BookingServiceTestUtils.getBooking(2L));
        when(bookingRepository.findBookingByItemId(id)).thenReturn(bookings);
        Item item = itemService.findItem(id, ownerId);

        Assertions.assertEquals("An umbrella", item.getName());
        Assertions.assertEquals("Big umbrella", item.getDescription());
        Assertions.assertNull(item.getLastBooking());
        Assertions.assertNull(item.getNextBooking());
    }

    @Test
    @Order(23)
    @DisplayName("23.Find item by id with comments")
    void findItemWithCommentsTest() {
        Long id = 1L;
        Long ownerId = 1L;
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id)));
        List<Comment> comments = List.of(CommentUtils.getComment(1L));
        List<Comment> commentList = List.of(CommentUtils.getComment(1L), CommentUtils.getComment(2L));
        when(commentRepository.findByAuthorIdAndItemId(ownerId, id)).thenReturn(comments);
        when(commentRepository.findByItemId(id)).thenReturn(commentList);
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(UserServiceTestUtils.getUser(ownerId)));
        Item item = itemService.findItem(id, ownerId);

        Assertions.assertEquals("A book", item.getName());
        Assertions.assertEquals("Amazing book", item.getDescription());
        Assertions.assertEquals("Really interesting book", item.getComments().get(0).getText());
    }

    @Test
    @Order(24)
    @DisplayName("24.Find item by id with comments by owner id")
    void findItemWithCommentsByOwnerIdTest() {
        Long id = 1L;
        Long ownerId = 3L;
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(UserServiceTestUtils.getUser(ownerId)));
        when(itemRepository.findById(id)).thenReturn(Optional.ofNullable(ItemServiceTestUtils.getItem(id)));
        List<Comment> comments = new ArrayList<>();
        List<Comment> commentList = List.of(CommentUtils.getComment(1L), CommentUtils.getComment(2L));
        when(commentRepository.findByAuthorIdAndItemId(ownerId, id)).thenReturn(comments);
        when(commentRepository.findByItemId(id)).thenReturn(commentList);

        Item item = itemService.findItem(id, ownerId);

        Assertions.assertEquals("A book", item.getName());
        Assertions.assertEquals("Amazing book", item.getDescription());
        Assertions.assertEquals("Boring", item.getComments().get(0).getText());
    }

}
