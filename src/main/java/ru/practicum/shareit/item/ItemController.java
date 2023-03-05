package ru.practicum.shareit.item;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> findAll(HttpServletRequest request, HttpServletResponse response) {
        long ownerId = getOwnerId(request, response);

        return itemService.findAll(ownerId);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemDto findItem(@PathVariable("id") @NotNull Long id, HttpServletRequest request, HttpServletResponse response) {
        long ownerId = getOwnerId(request, response);
        return itemService.findItem(id, ownerId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Item create(@RequestBody @Valid Item item, HttpServletRequest request, HttpServletResponse response) throws ValidationException {

        if (!item.isAvailable() || item.getName().isEmpty() || item.getDescription() == null) {
            throw new ValidationException("Error");
        }
        long ownerId = getOwnerId(request, response);
        return itemService.create(item, ownerId);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Item update(@RequestBody @NotNull @Valid ItemDto item, @PathVariable("id") @NotNull Long id,
                       HttpServletRequest request, HttpServletResponse response) {
        long ownerId = getOwnerId(request, response);
        return itemService.update(item, id, ownerId);
    }

    @GetMapping(value = "/search")
    public List<Item> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping(value = "/{id}/comment")
    public CommentDto addComment(@RequestBody @NotNull @Valid Comment comment, @PathVariable("id") @NotNull Long id,
                                 HttpServletRequest request, HttpServletResponse response) throws ValidationException {
        long ownerId = getOwnerId(request, response);
        return itemService.addComment(comment, id, ownerId);
    }

    private long getOwnerId(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        long ownerId = Integer.parseInt(request.getHeader("X-Sharer-User-Id"));
        return ownerId;
    }
}
