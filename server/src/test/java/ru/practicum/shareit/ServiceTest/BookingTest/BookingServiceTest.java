package ru.practicum.shareit.ServiceTest.BookingTest;

import org.junit.jupiter.api.*;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.CommentRepository;
import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.exeptions.BadRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.ServiceTest.ItemTest.ItemServiceTestUtils.getItem;
import static ru.practicum.shareit.ServiceTest.UserTest.UserServiceTestUtils.getUser;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingServiceTest {

    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private ItemService itemService;
    private UserRepository userRepository;
    private UserService userService;
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        commentRepository = mock(CommentRepository.class);
        userService = new UserServiceImpl(userRepository);
        itemService = new ItemServiceImpl(itemRepository, bookingRepository, commentRepository, userService);
        bookingService = new BookingServiceImpl(bookingRepository, userService, itemService);
    }

    @Test
    @Order(1)
    @DisplayName("1.Get all bookings state ALL")
    void findAllTest() throws BadRequestException {
        Long ownerId = 1L;
        Long itemId = 1L;
        List<Booking> bookingList = Arrays.asList(BookingServiceTestUtils.getBooking(1L));
        when(bookingRepository.findAllByOrderByStartDesc()).thenReturn(bookingList);
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(getUser(ownerId)));
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(getItem(itemId)));

        List<BookingDto> bookings = bookingService.findAll("ALL", ownerId, PageRequest.of(0, 10));
        Assertions.assertEquals(1, bookings.size());
    }

    @Test
    @Order(3)
    @DisplayName("2.Get all wrong bookerId")
    void shouldThrowExceptionWrongBookerId() {
        Long bookerId = 10L;
        Long itemId = 1L;
        List<Booking> bookingList = Arrays.asList(BookingServiceTestUtils.getBooking(1L));
        when(bookingRepository.findAllByOrderByStartDesc()).thenReturn(bookingList);
        when(userRepository.findById(bookerId)).thenThrow(new ObjectNotFoundException("Wrong ID"));
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(getItem(itemId)));

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.findAll("ALL", bookerId, PageRequest.of(0, 10)));
        Assertions.assertEquals("Wrong ID", exception.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("4.Get all bookings state FUTURE")
    void findAllTestStateFuture() throws BadRequestException {
        Long ownerId = 3L;
        Long itemId = 1L;
        List<Booking> bookingList = Arrays.asList(BookingServiceTestUtils.getBooking(2L));
        when(bookingRepository.findAllByOrderByStartDesc()).thenReturn(bookingList);
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(getUser(ownerId)));
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(getItem(itemId)));

        List<BookingDto> bookings = bookingService.findAll("FUTURE", ownerId, PageRequest.of(0, 10));
        Assertions.assertEquals(1, bookings.size());
    }

    @Test
    @Order(5)
    @DisplayName("5.Get all bookings state PAST")
    void findAllTestStatePast() throws BadRequestException {
        Long ownerId = 1L;
        Long itemId = 1L;
        List<Booking> bookingList = Arrays.asList(BookingServiceTestUtils.getBooking(1L));
        when(bookingRepository.findAllByOrderByStartDesc()).thenReturn(bookingList);
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(getUser(ownerId)));
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(getItem(itemId)));

        List<BookingDto> bookings = bookingService.findAll("PAST", ownerId, PageRequest.of(0, 10));
        Assertions.assertEquals(1, bookings.size());
    }

    @Test
    @Order(6)
    @DisplayName("6.Get all bookings state WAITING")
    void findAllTestStateWaiting() throws BadRequestException {
        Long ownerId = 3L;
        Long itemId = 1L;
        List<Booking> bookingList = Arrays.asList(BookingServiceTestUtils.getBooking(2L));
        when(bookingRepository.findAllByOrderByStartDesc()).thenReturn(bookingList);
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(getUser(ownerId)));
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(getItem(itemId)));

        List<BookingDto> bookings = bookingService.findAll("WAITING", ownerId, PageRequest.of(0, 10));
        Assertions.assertEquals(1, bookings.size());
    }

    @Test
    @Order(7)
    @DisplayName("7.Get all bookings state REJECTED")
    void findAllTestStateRejected() throws BadRequestException {
        Long ownerId = 2L;
        Long itemId = 3L;
        List<Booking> bookingList = Arrays.asList(BookingServiceTestUtils.getBooking(4L));
        when(bookingRepository.findAllByOrderByStartDesc()).thenReturn(bookingList);
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(getUser(ownerId)));
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(getItem(itemId)));

        List<BookingDto> bookings = bookingService.findAll("REJECTED", ownerId, PageRequest.of(0, 10));
        Assertions.assertEquals(1, bookings.size());
    }

    @Test
    @Order(8)
    @DisplayName("8.Get all wrong itemId")
    void shouldThrowExceptionWrongItemId() {
        Long bookerId = 1L;
        Long itemId = 10L;
        List<Booking> bookingList = Arrays.asList(BookingServiceTestUtils.getBooking(1L));
        when(bookingRepository.findAllByOrderByStartDesc()).thenReturn(bookingList);
        when(userRepository.findById(bookerId)).thenReturn(Optional.ofNullable(getUser(bookerId)));
        when(itemRepository.findById(itemId)).thenThrow(new ObjectNotFoundException("Wrong id"));

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.findAll("ALL", bookerId, PageRequest.of(0, 10)));
        Assertions.assertEquals("Wrong id", exception.getMessage());
    }

    @Test
    @Order(9)
    @DisplayName("9.Get all bookings with ownerId")
    void findAllWithOwnerTest() throws BadRequestException {
        Long ownerId = 1L;
        Long itemId = 3L;
        List<Booking> bookingList = Arrays.asList(BookingServiceTestUtils.getBooking(4L));
        when(bookingRepository.findAllByOrderByStartDesc()).thenReturn(bookingList);
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(getUser(ownerId)));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(getUser(2L)));
        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(getItem(itemId)));

        List<BookingDto> bookings = bookingService.findAllWithOwner("ALL", ownerId, PageRequest.of(0, 10));
        Assertions.assertEquals(1, bookings.size());
    }

    @Test
    @Order(10)
    @DisplayName("10.Find booking by id")
    void findBookingByIdTest() {
        Long id = 2L;
        Long ownerId = 3L;
        when(bookingRepository.findById(id)).thenReturn(Optional.ofNullable(BookingServiceTestUtils.getBooking(id)));
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(getUser(ownerId)));
        when(itemRepository.findById(BookingServiceTestUtils.getBooking(id).getItemId()))
                .thenReturn(Optional.ofNullable(getItem(BookingServiceTestUtils.getBooking(id).getItemId())));

        BookingDto booking = bookingService.getBookingById(id, ownerId);
        Assertions.assertEquals("A book", booking.getItem().getName());
    }

    @Test
    @Order(11)
    @DisplayName("11.Find booking by wrong id")
    void shouldThrowExceptionWrongId() {
        Long id = 20L;
        Long ownerId = 3L;
        when(bookingRepository.findById(id)).thenThrow(new ObjectNotFoundException("Wrong ID"));

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(id, ownerId));
        Assertions.assertEquals("Wrong ID", exception.getMessage());
    }

    @Test
    @Order(12)
    @DisplayName("12.Find booking by wrong OwnerId")
    void shouldThrowExceptionWhileFindingBookingWrongOwnerId() {
        Long id = 2L;
        Long ownerId = 4L;
        when(bookingRepository.findById(id)).thenReturn(Optional.ofNullable(BookingServiceTestUtils.getBooking(id)));
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(getUser(ownerId)));
        when(userRepository.findById(BookingServiceTestUtils.getBooking(id).getBookerId()))
                .thenReturn(Optional.ofNullable(getUser(BookingServiceTestUtils.getBooking(id).getBookerId())));
        when(itemRepository.findById(BookingServiceTestUtils.getBooking(id).getItemId()))
                .thenReturn(Optional.ofNullable(getItem(BookingServiceTestUtils.getBooking(id).getItemId())));

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(id, ownerId));
        Assertions.assertEquals("Wrong id", exception.getMessage());
    }

    @Test
    @Order(13)
    @DisplayName("13.Create test")
    void createTest() throws BadRequestException {
        Long id = 1L;
        List<Booking> bookingsWithoutIds = BookingServiceTestUtils.getBookingsWithoutIds();
        Item item = getItem(1L);
        when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(getUser(1L)));
        when(bookingRepository.save(bookingsWithoutIds.get(0))).thenReturn((BookingServiceTestUtils.getBooking(id)));
        BookingDto bookingDto = bookingService.create(bookingsWithoutIds.get(0), BookingServiceTestUtils.getBooking(id).getBookerId());
        Assertions.assertEquals(1, bookingDto.getId());
        Assertions.assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    @Order(17)
    @DisplayName("17.Create test fail status is not available")
    void shouldThrowExceptionNotAvailable() {
        Long id = 1L;
        List<Booking> bookingsWithoutIds = BookingServiceTestUtils.getBookingsWithoutIds();
        Item item = getItem(1L);
        when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(getUser(1L)));
        Booking booking = bookingsWithoutIds.get(0);
        item.setAvailable(false);
        when(bookingRepository.save(booking)).thenAnswer(invocationOnMock -> new BadRequestException("The item should be available"));

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.create(booking, BookingServiceTestUtils.getBooking(id).getBookerId()));
        Assertions.assertEquals("The item should be available", exception.getMessage());
    }

    @Test
    @Order(18)
    @DisplayName("18.Create test fail end before start")
    void shouldThrowExceptionEndBeforeStart() {
        Long id = 1L;
        List<Booking> bookingsWithoutIds = BookingServiceTestUtils.getBookingsWithoutIds();
        Item item = getItem(1L);
        when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(getUser(1L)));
        Booking booking = bookingsWithoutIds.get(0);
        booking.setEnd(booking.getStart().minusDays(2));
        when(bookingRepository.save(booking)).thenAnswer(invocationOnMock -> new BadRequestException("The item should be available"));

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.create(booking, BookingServiceTestUtils.getBooking(id).getBookerId()));
        Assertions.assertEquals("The item should be available", exception.getMessage());
    }

    @Test
    @Order(19)
    @DisplayName("19.Create test fail wrong user id")
    void shouldThrowExceptionWrongUserId() {
        Long id = 1L;
        Long userId = 10L;
        List<Booking> bookingsWithoutIds = BookingServiceTestUtils.getBookingsWithoutIds();
        Item item = getItem(1L);
        when(itemRepository.findById(userId))
                .thenReturn(Optional.ofNullable(getItem(1L)));
        when(userRepository.findById(1L))
                .thenThrow(new ObjectNotFoundException("Wrong id"));
        Booking booking = bookingsWithoutIds.get(0);
        when(bookingRepository.save(booking)).thenReturn((BookingServiceTestUtils.getBooking(id)));

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(booking, BookingServiceTestUtils.getBooking(id).getBookerId()));
        Assertions.assertEquals("Wrong id", exception.getMessage());
    }

    @Test
    @Order(20)
    @DisplayName("20.Create test fail wrong item id")
    void shouldThrowExceptionWhileCreateWrongItemId() {
        Long id = 1L;
        Long itemId = 1L;
        List<Booking> bookingsWithoutIds = BookingServiceTestUtils.getBookingsWithoutIds();
        when(itemRepository.findById(itemId))
                .thenThrow(new ObjectNotFoundException("Wrong id"));
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(getUser(1L)));
        Booking booking = bookingsWithoutIds.get(0);
        when(bookingRepository.save(booking)).thenReturn((BookingServiceTestUtils.getBooking(id)));

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(booking, BookingServiceTestUtils.getBooking(id).getBookerId()));
        Assertions.assertEquals("Wrong id", exception.getMessage());
    }

    @Test
    @Order(21)
    @DisplayName("21.Create test fail user books own item")
    void shouldThrowExceptionUserBooksOwnItem() {
        Long id = 1L;
        Long itemId = 1L;
        List<Booking> bookingsWithoutIds = BookingServiceTestUtils.getBookingsWithoutIds();
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(getItem(itemId)));
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(getUser(1L)));
        Booking booking = bookingsWithoutIds.get(0);
        when(bookingRepository.save(booking)).thenReturn((BookingServiceTestUtils.getBooking(id)));

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(booking, 2L));
        Assertions.assertEquals("Wrong ID", exception.getMessage());
    }

    @Test
    @Order(22)
    @DisplayName("22.Update test")
    void updateTest() throws BadRequestException {
        Long id = 2L;
        boolean approved = false;
        Booking booking = BookingServiceTestUtils.getBooking(id);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        User user = getUser(booking.getBookerId());
        when(userRepository.findById(booking.getBookerId())).thenReturn(Optional.ofNullable(user));
        Item item = getItem(booking.getItemId());
        when(itemRepository.findById(booking.getItemId()))
                .thenReturn(Optional.ofNullable(item));
        booking.setUser(user);
        booking.setItem(item);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto bookingDto = bookingService.update(id, approved, 2L);
        Assertions.assertEquals(2, bookingDto.getId());
        Assertions.assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());
    }

    @Test
    @Order(23)
    @DisplayName("23.Update test fail nothing to update")
    void shouldThrowExceptionNothingToUpdate() {
        Long id = 2L;
        boolean approved = true;
        Booking booking = BookingServiceTestUtils.getBooking(id);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        User user = getUser(booking.getBookerId());
        when(userRepository.findById(booking.getBookerId())).thenReturn(Optional.ofNullable(user));
        Item item = getItem(booking.getItemId());
        when(itemRepository.findById(booking.getItemId()))
                .thenReturn(Optional.ofNullable(item));
        booking.setUser(user);
        booking.setItem(item);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.update(id, approved, 2L));
        Assertions.assertEquals("Wrong id", exception.getMessage());
    }

    @Test
    @Order(24)
    @DisplayName("24.Update test fail booker is owner")
    void shouldThrowExceptionWhileUpdateWrongBookerId() {
        Long id = 2L;
        boolean approved = true;
        Booking booking = BookingServiceTestUtils.getBooking(id);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        User user = getUser(booking.getBookerId());
        when(userRepository.findById(booking.getBookerId())).thenReturn(Optional.ofNullable(user));
        Item item = getItem(booking.getItemId());
        when(itemRepository.findById(booking.getItemId()))
                .thenReturn(Optional.ofNullable(item));
        booking.setUser(user);
        booking.setItem(item);
        when(bookingRepository.save(booking)).thenReturn(booking);

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.update(id, approved, 3L));
        Assertions.assertEquals("Wrong id", exception.getMessage());
    }

    @Test
    @Order(25)
    @DisplayName("25.Update test fail wrong owner id")
    void shouldThrowExceptionWhileUpdateWrongOwnerId() {
        Long id = 2L;
        boolean approved = true;
        Booking booking = BookingServiceTestUtils.getBooking(id);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        User user = getUser(booking.getBookerId());
        when(userRepository.findById(booking.getBookerId())).thenReturn(Optional.ofNullable(user));
        Item item = getItem(booking.getItemId());
        when(itemRepository.findById(booking.getItemId()))
                .thenReturn(Optional.ofNullable(item));
        booking.setUser(user);
        booking.setItem(item);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.update(id, approved, 1L));
        Assertions.assertEquals("Wrong id", exception.getMessage());
    }

    @Test
    @Order(26)
    @DisplayName("26.Update test with approved status")
    void updateTest2() throws BadRequestException {
        Long id = 4L;
        boolean approved = true;
        Booking booking = BookingServiceTestUtils.getBooking(id);
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        User user = getUser(booking.getBookerId());
        when(userRepository.findById(booking.getBookerId())).thenReturn(Optional.ofNullable(user));
        Item item = getItem(booking.getItemId());
        when(itemRepository.findById(booking.getItemId()))
                .thenReturn(Optional.ofNullable(item));
        booking.setUser(user);
        booking.setItem(item);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto bookingDto = bookingService.update(id, approved, 1L);
        Assertions.assertEquals(4, bookingDto.getId());
        Assertions.assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }
}


