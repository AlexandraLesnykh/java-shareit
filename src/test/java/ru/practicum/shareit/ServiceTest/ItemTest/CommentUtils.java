package ru.practicum.shareit.ServiceTest.ItemTest;

import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.exeptions.ObjectNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class CommentUtils {
    public CommentUtils() {
    }

    public static List<Comment> getComments() {
        return Arrays.asList(
                getComments(1L, "Really interesting book", 1L, 1L, LocalDateTime.now().minusDays(3)),
                getComments(2L, "Boring", 1L, 3L, LocalDateTime.now()),
                getComments(3L, "Helpful", 2L, 2L, LocalDateTime.now().minusDays(1))
        );
    }

    public static Comment getComment(Long id) {
        return getComments()
                .stream()
                .filter(comment -> comment.getId() == id).findAny().orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
    }

    public static List<Comment> getCommentsWithoutIds() {
        return getComments()
                .stream()
                .peek(comment -> comment.setId(0L))
                .peek(comment -> comment.setCreated(null))
                .collect(toList());
    }

    private static Comment getComments(Long id, String text, Long itemId, Long authorId, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setItemId(itemId);
        comment.setAuthorId(authorId);
        comment.setCreated(created);
        return comment;
    }
}
