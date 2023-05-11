package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    public static final String OWNER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(OWNER_ID_HEADER) long userId,
                                      @Valid @RequestBody ItemRequestRequestDto itemRequestDto) {
        return itemRequestClient.create(itemRequestDto, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequestsById(@PathVariable long id,
                                          @RequestHeader(OWNER_ID_HEADER) long ownerId) {
        return itemRequestClient.getRequestsById(ownerId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByOwner(@RequestHeader(OWNER_ID_HEADER) long ownerId,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestClient.getAllRequestsByOwner(ownerId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(OWNER_ID_HEADER) long ownerId,
                                                 @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemRequestClient.getAllRequests(ownerId, from, size);
    }
}
