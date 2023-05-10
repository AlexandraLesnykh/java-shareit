package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state" + stateParam));
        return bookingClient.findAll(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllWithOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state" + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findAllWithOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                                         @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.create(requestDto, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(bookingId, userId);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") @NotNull long bookingId,
                                         @RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                         @RequestParam(value = "approved") boolean approved) {
        log.info("Update booking with id{}, userId={}", bookingId, ownerId);
        return bookingClient.update(bookingId, approved, ownerId);
    }
}
