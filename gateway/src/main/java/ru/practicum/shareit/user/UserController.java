package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> findUser(@PathVariable("id") @NotNull Long id) {
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserRequestDto user) {
        return userClient.saveUser(user);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Object> update(@RequestBody @Valid UserRequestDto user, @PathVariable("id") @NotNull Long id) {
        return userClient.update(user, id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") @NotNull Long id) {
        return userClient.deleteUser(id);
    }


}
