package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {

    long id;

    @Size(max = 256)
    String text;

    @NotNull
    long itemId;

    @NotNull
    long authorId;

    LocalDateTime created;
}

