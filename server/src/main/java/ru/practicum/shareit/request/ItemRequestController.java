package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequest create(@RequestBody ItemRequest itemRequest,
                              @RequestHeader(value = "X-Sharer-User-Id") long ownerId) {

        return itemRequestService.create(itemRequest, ownerId);
    }

    @GetMapping(value = "/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getAllRequests(ownerId, PageRequest.of(from, size));
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsByOwner(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                                      @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                      @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return itemRequestService.getAllRequestsByOwner(ownerId, PageRequest.of(from, size));
    }

    @GetMapping(value = "/{id}")
    public ItemRequestDto getRequestsById(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                          @PathVariable("id") Long id) {
        return itemRequestService.getRequestsById(ownerId, id);
    }
}
