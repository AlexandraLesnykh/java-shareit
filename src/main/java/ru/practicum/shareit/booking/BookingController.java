package ru.practicum.shareit.booking;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeptions.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
    public BookingDto create(@RequestBody @Valid Booking booking, @RequestHeader(value = "X-Sharer-User-Id") long ownerId) throws ValidationException {
        return bookingService.create(booking, ownerId);
    }

    @PatchMapping(value = "/{id}")
    public BookingDto update(@PathVariable("id") @NotNull long bookingId,
                             @RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                             @RequestParam(value = "approved") boolean approved) throws ValidationException {
        return bookingService.update(bookingId, approved, ownerId);
    }

    @GetMapping(value = "/{id}")
    public BookingDto getBookingById(@PathVariable("id") @NotNull long bookingId,
                                     @RequestHeader(value = "X-Sharer-User-Id") long ownerId) throws ValidationException {
        BookingDto bookingDto = bookingService.getBookingById(bookingId, ownerId);
        if (bookingDto == null) {
            throw new ValidationException("Wrong id");
        }
        return bookingDto;
    }

    @GetMapping(value = "/owner")
    public List<BookingDto> findAllWithOwner(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                             @RequestParam(value = "state", defaultValue = "ALL", required = false)
                                             String state) throws ValidationException {
        return bookingService.findAllWithOwner(state, ownerId);
    }

    @GetMapping()
    public List<BookingDto> findAll(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                    @RequestParam(value = "state", defaultValue = "ALL", required = false)
                                    String state) throws ValidationException {
        return bookingService.findAll(state, ownerId);
    }

}
