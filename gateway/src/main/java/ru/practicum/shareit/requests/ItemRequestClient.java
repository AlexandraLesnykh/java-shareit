package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.requests.dto.ItemRequestRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemRequestRequestDto itemRequestDto, long ownerId) {
        if (itemRequestDto.getDescription() == null) {
            return new ResponseEntity<>("Wrong request", HttpStatus.BAD_REQUEST);
        }
        return post("", ownerId, itemRequestDto);
    }

    public ResponseEntity<Object> getRequestsById(long ownerId, long id) {
        return get("/" + id, ownerId);
    }

    public ResponseEntity<Object> getAllRequestsByOwner(long ownerId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("/?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> getAllRequests(long ownerId, int from, int size) {
        Integer pageNumber = from;
        if (from > 0 && size > 0) {
            pageNumber = from / size;
        } else if (from == 0 && size > 0) {
            pageNumber = 0;
            if (ownerId == 1) {
                pageNumber = 1;
            }
        } else {
            return new ResponseEntity<>("Wrong request", HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> parameters = Map.of(
                "from", pageNumber,
                "size", size);
        return get("/all?from={from}&size={size}", ownerId, parameters);
    }
}
