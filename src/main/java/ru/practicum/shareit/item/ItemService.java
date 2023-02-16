package ru.practicum.shareit.item;

import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    public Item create(Item item, int ownerId) throws ValidationException;

    public Item update(ItemDto item, int id, int ownerId);

    public List<Item> findAll(int ownerId);

    public Item findItem(int id);

    public Collection<Item> search(String text);
}
