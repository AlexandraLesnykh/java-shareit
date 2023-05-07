package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    public static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ItemRequestDto itemDto,
                                         @RequestHeader(OWNER_ID_HEADER) @NotNull long ownerId) {
        return itemClient.create(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody @Valid ItemRequestDto itemDto,
                                         @RequestHeader(OWNER_ID_HEADER) @NotNull long ownerId,
                                         @PathVariable long id) {
        return itemClient.update(itemDto, id, ownerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id, @RequestHeader(OWNER_ID_HEADER) @NotNull long ownerId) {
        return itemClient.getById(id, ownerId);
    }

    @GetMapping()
    public ResponseEntity<Object> findAll(@RequestHeader(OWNER_ID_HEADER) @NotNull long ownerId) {
        return itemClient.findAll(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(OWNER_ID_HEADER) @NotNull long ownerId,
                                         @RequestParam String text) {
        return itemClient.search(text, ownerId);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentRequestDto commentDto,
                                             @PathVariable long id,
                                             @RequestHeader(OWNER_ID_HEADER) @NotNull long ownerId) {
        return itemClient.addComment(commentDto, id, ownerId);

    }
}
