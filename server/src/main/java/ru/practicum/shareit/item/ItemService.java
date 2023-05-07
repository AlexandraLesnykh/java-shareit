package ru.practicum.shareit.item;

import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.exeptions.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item, long ownerId) throws BadRequestException;

    Item update(ItemDto item, long id, long ownerId);

    List<Item> findAll(long ownerId);

    Item findItem(long id, long ownerId);

    List<Item> search(String text);

    CommentDto addComment(Comment comment, long itemId, long ownerId) throws BadRequestException;

    Item findById(long id);
}
