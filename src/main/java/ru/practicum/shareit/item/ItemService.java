package ru.practicum.shareit.item;

import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    public Item create(Item item, long ownerId) throws ValidationException;

    public Item update(ItemDto item, long id, long ownerId);

    public List<Item> findAll(long ownerId);

    public Item findItem(long id);

    public Collection<Item> search(String text);
}
