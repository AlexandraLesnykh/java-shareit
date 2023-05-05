package ru.practicum.shareit.ServiceTest.ItemRequestTest;

import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ItemRequestServiceTestUtils {
    public ItemRequestServiceTestUtils() {
    }

    public static List<ItemRequest> getItemRequest() {
        return Arrays.asList(
                getItemRequests(1L, "Need a car", 1L, LocalDateTime.now().minusDays(1)),
                getItemRequests(2L, "Need a computer", 2L, LocalDateTime.now()),
                getItemRequests(3L, "Need a book", 3L, LocalDateTime.now())
        );
    }

    public static ItemRequest getItemRequest(Long id) {
        return getItemRequest()
                .stream()
                .filter(itemRequest -> itemRequest.getId() == id).findAny().orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
    }

    public static List<ItemRequest> getItemRequestWithoutIds() {
        return getItemRequest()
                .stream()
                .peek(itemRequest -> itemRequest.setId(0L))
                .collect(toList());
    }

    private static ItemRequest getItemRequests(Long id, String description, Long requestor, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);
        return itemRequest;

    }
}
