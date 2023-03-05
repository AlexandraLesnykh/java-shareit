package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingByItemId(long itemId);

    Booking findFirstByItemIdAndStatusAndEndBeforeOrderByEnd(long itemId, BookingStatus bookingStatus, LocalDateTime now);

    Booking findFirstByItemIdAndStatusAndStartAfterOrderByStart(long itemId, BookingStatus bookingStatus, LocalDateTime now);

    Booking findByBookerIdAndItemIdAndEndBefore(long id, long itemId, LocalDateTime now);
}

