package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeptions.BadRequestException;

import java.util.List;

public interface BookingService {
    BookingDto create(Booking booking, long ownerId) throws BadRequestException;

    BookingDto update(long bookingId, boolean approved, long ownerId) throws BadRequestException;

    BookingDto getBookingById(long bookingId, long ownerId);

    List<BookingDto> findAll(String state, long ownerId, PageRequest of) throws BadRequestException;

    List<BookingDto> findAllWithOwner(String state, long ownerId, PageRequest of) throws BadRequestException;
}
