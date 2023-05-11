package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeptions.BadRequestException;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Validated
@RestController
@RequestMapping(value = "/bookings",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public BookingDto create(@RequestBody Booking booking,
                             @RequestHeader(value = "X-Sharer-User-Id") long ownerId) throws BadRequestException {
        return bookingService.create(booking, ownerId);
    }

    @PatchMapping(value = "/{id}")
    public BookingDto update(@PathVariable("id") long bookingId,
                             @RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                             @RequestParam(value = "approved") boolean approved) throws BadRequestException {
        return bookingService.update(bookingId, approved, ownerId);
    }

    @GetMapping(value = "/{id}")
    public BookingDto getBookingById(@PathVariable("id") long bookingId,
                                     @RequestHeader(value = "X-Sharer-User-Id") long ownerId) throws BadRequestException {
        BookingDto bookingDto = bookingService.getBookingById(bookingId, ownerId);
        if (bookingDto == null) {
            throw new BadRequestException("Wrong id");
        }
        return bookingDto;
    }

    @GetMapping(value = "/owner")
    public List<BookingDto> findAllWithOwner(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                             @RequestParam(value = "state", defaultValue = "ALL", required = false)
                                             String state,
                                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @RequestParam(name = "size", defaultValue = "10") Integer size) throws BadRequestException {
        return bookingService.findAllWithOwner(state, ownerId, PageRequest.of(from / size, size));
    }

    @GetMapping()
    public List<BookingDto> findAll(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                    @RequestParam(value = "state", defaultValue = "ALL", required = false)
                                    String state,
                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", defaultValue = "10") Integer size) throws BadRequestException {
        Integer pageNumber = from;
        return bookingService.findAll(state, ownerId, PageRequest.of(from / size, size));
    }
}
