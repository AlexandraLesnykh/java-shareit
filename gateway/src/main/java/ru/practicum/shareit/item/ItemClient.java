package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemRequestDto itemDto, long ownerId) {
        if (!itemDto.isAvailable() || itemDto.getName().isEmpty() || itemDto.getDescription() == null) {
            return new ResponseEntity<>("Wrong request", HttpStatus.BAD_REQUEST);
        }
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> update(ItemRequestDto itemDto, long id, long ownerId) {
        return patch("/" + id, ownerId, itemDto);
    }

    public ResponseEntity<Object> getById(long id, long ownerId) {
        return get("/" + id, ownerId);
    }

    public ResponseEntity<Object> findAll(long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> search(String text, long ownerId) {
        Map<String, Object> parameters = Map.of(
                "text", text);
        return get("/search?text={text}", ownerId, parameters);
    }

    public ResponseEntity<Object> addComment(CommentRequestDto commentDto, long id, long ownerId) {
        if (commentDto.getText().isEmpty()) {
            return new ResponseEntity<>("Wrong request", HttpStatus.BAD_REQUEST);
        }
        return post("/" + id + "/comment", ownerId, commentDto);
    }
}
