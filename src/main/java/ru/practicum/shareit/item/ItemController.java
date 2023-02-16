package ru.practicum.shareit.item;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
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
    public List<Item> findAll(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        int ownerId = Integer.parseInt(request.getHeader("X-Sharer-User-Id"));

        return itemService.findAll(ownerId);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Item findItem(@PathVariable("id") @NotNull Integer id) {
        return itemService.findItem(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Item create(@RequestBody @Valid Item item, HttpServletRequest request, HttpServletResponse response) throws ValidationException {

        if (!item.isAvailable() || item.getName().isEmpty()) {
            throw new ValidationException("Error");
        }
        response.setContentType("text/html");
        int ownerId = Integer.parseInt(request.getHeader("X-Sharer-User-Id"));
        return itemService.create(item, ownerId);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Item update(@RequestBody @NotNull @Valid ItemDto item, @PathVariable("id") @NotNull Integer id,
                       HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        int ownerId = Integer.parseInt(request.getHeader("X-Sharer-User-Id"));
        return itemService.update(item, id, ownerId);
    }

    @GetMapping(value = "/search")
    public Collection<Item> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
