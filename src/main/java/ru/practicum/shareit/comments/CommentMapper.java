package ru.practicum.shareit.comments;

import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        } else {
            return new CommentDto(
                    comment.getId(),
                    comment.getText(),
                    comment.getAuthor().getName(),
                    comment.getCreated()
            );
        }
    }
}
