package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingByItemId(long itemId);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(long itemId, BookingStatus bookingStatus, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStart(long itemId, BookingStatus bookingStatus, LocalDateTime now);

    Optional<Booking> findFirstByBookerIdAndItemIdAndEndBefore(long id, long itemId, LocalDateTime now);

  //  Page<Booking> findAllByOrderByStartDesc(Pageable pageRequest);
    List<Booking> findAllByOrderByStartDesc();

}

