package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserRequestDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getUser(long id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> saveUser(UserRequestDto requestDto) {
        if (requestDto.getEmail() == null || requestDto.getName() == null) {
            return new ResponseEntity<>("Wrong request", HttpStatus.BAD_REQUEST);
        }
        return post("", requestDto);
    }

    public ResponseEntity<Object> findAll() {
        return get("");
    }

    public ResponseEntity<Object> update(UserRequestDto requestDto, long id) {
        return patch("/" + id, requestDto);
    }

    public ResponseEntity<Object> deleteUser(long id) {
        return delete("/" + id);
    }
}
