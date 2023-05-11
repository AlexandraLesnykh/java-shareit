package ru.practicum.shareit.ServiceTest.BookingTest;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeptions.ObjectNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class BookingServiceTestUtils {
    public BookingServiceTestUtils() {
    }

    public static List<Booking> getBookings() {
        return Arrays.asList(
                getBookings(1L, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4), 1L, 1L, BookingStatus.APPROVED),
                getBookings(2L, LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(5), 1L, 3L, BookingStatus.WAITING),
                getBookings(3L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2), 2L, 2L, BookingStatus.APPROVED),
                getBookings(4L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2), 3L, 2L, BookingStatus.REJECTED)
        );
    }

    public static Booking getBooking(Long id) {
        return getBookings()
                .stream()
                .filter(booking -> booking.getId() == id).findAny().orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
    }

    public static List<Booking> getBookingsWithoutIds() {
        return getBookings()
                .stream()
                .peek(booking -> booking.setId(0L))
                .collect(toList());
    }

    private static Booking getBookings(Long id, LocalDateTime start, LocalDateTime end, Long itemId, Long bookerId,
                                       BookingStatus bookingStatus) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItemId(itemId);
        booking.setBookerId(bookerId);
        booking.setStatus(bookingStatus);
        return booking;
    }
}
