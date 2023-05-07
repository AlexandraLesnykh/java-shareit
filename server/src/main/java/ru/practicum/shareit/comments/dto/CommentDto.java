package ru.practicum.shareit.comments.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class CommentDto {
    long id;
    String text;
    String authorName;
    LocalDateTime created;
}
