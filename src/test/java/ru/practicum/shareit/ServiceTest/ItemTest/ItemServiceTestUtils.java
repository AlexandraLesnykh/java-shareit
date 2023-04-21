package ru.practicum.shareit.ServiceTest.ItemTest;

import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ItemServiceTestUtils {

    public ItemServiceTestUtils() {
    }

    public static List<Item> getItems() {
        return Arrays.asList(
                getItems(1L, "A book", "Amazing book", true, 2L, null),
                getItems(2L, "An umbrella", "Big umbrella", true, 1L, null),
                getItems(3L, "A sofa", "Soft sofa", true, 1L, null)
        );
    }

    public static Item getItem(Long id) {
        return getItems()
                .stream()
                .filter(item -> item.getId() == id).findAny().orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
    }

    public static List<Item> getItemsWithoutIds() {
        return getItems()
                .stream()
                .peek(item -> item.setId(0L))
                .peek(item -> item.setOwner(0L))
                .collect(toList());
    }

    public static Item getItemAfterUpdate(Item item, long id) {
        Item itemForReturn = getItem(id);
        itemForReturn.setName(item.getName());
        itemForReturn.setDescription(item.getDescription());
        itemForReturn.setAvailable(item.isAvailable());
        itemForReturn.setRequestId(item.getRequestId());
        return itemForReturn;
    }

    private static Item getItems(Long id, String name, String description, boolean available, Long ownerId, Long requestId) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(ownerId);
        item.setRequestId(requestId);
        return item;
    }
}
