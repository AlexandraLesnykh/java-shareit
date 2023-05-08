package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.validator.StateValidation;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    @Validated
    public ResponseEntity<Object> findAll(long userId, @StateValidation BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    @Validated
    public ResponseEntity<Object> findAllWithOwner(long userId, @StateValidation BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> create(BookItemRequestDto requestDto, long userId) {
        if (requestDto.getStart() == null || requestDto.getEnd() == null || requestDto.getStart().equals(requestDto.getEnd())) {
            return new ResponseEntity<>("Wrong request", HttpStatus.BAD_REQUEST);
        }
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(Long bookingId, long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> update(long bookingId, boolean approved, long ownerId) {
        return patch("/" + bookingId + "?approved=" + approved, ownerId);
    }


}
