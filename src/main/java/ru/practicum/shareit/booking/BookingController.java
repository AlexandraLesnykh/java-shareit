package ru.practicum.shareit.booking;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeptions.ValidationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public BookingDto create(@RequestBody @Valid Booking booking, HttpServletRequest request, HttpServletResponse response) throws ValidationException {
        long ownerId = getOwnerId(request, response);
        return bookingService.create(booking, ownerId);
    }

    @PatchMapping(value = "/{id}")
    public BookingDto update(@PathVariable("id") @NotNull Long bookingId,
                             HttpServletRequest request, HttpServletResponse response) throws ValidationException {
        response.setContentType("text/html");
        Boolean approved = Boolean.parseBoolean(request.getParameter("approved"));
        long ownerId = getOwnerId(request, response);
        return bookingService.update(bookingId, approved, ownerId);
    }

    @GetMapping(value = "/{id}")
    public BookingDto getBookingById(@PathVariable("id") @NotNull Long bookingId,
                                     HttpServletRequest request, HttpServletResponse response) throws ValidationException {
        long ownerId = getOwnerId(request, response);
        BookingDto bookingDto = bookingService.getBookingById(bookingId, ownerId);
        if (bookingDto == null) {
            throw new ValidationException("f");
        }
        return bookingDto;
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllWithOwner(@RequestParam(value = "state", defaultValue = "ALL", required = false)
                                             String state, HttpServletRequest request, HttpServletResponse response) throws ValidationException {
        long ownerId = getOwnerId(request, response);
        return bookingService.findAllWithOwner(state, ownerId);
    }

    @GetMapping
    public List<BookingDto> findAll(@RequestParam(value = "state", defaultValue = "ALL", required = false)
                                    String state, HttpServletRequest request, HttpServletResponse response) throws ValidationException {
        long ownerId = getOwnerId(request, response);
        return bookingService.findAll(state, ownerId);
    }

    private long getOwnerId(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        long ownerId = Integer.parseInt(request.getHeader("X-Sharer-User-Id"));
        return ownerId;
    }

}
