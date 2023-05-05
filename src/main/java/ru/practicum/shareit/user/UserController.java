package ru.practicum.shareit.user;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exeptions.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(
        value = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class UserController {

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User findUser(@PathVariable("id") @NotNull Long id) {
        return userService.findUser(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User create(@RequestBody @Valid User user) throws ValidationException {
        return userService.create(user);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public User update(@RequestBody @Valid User user, @PathVariable("id") @NotNull Long id) {
        return userService.update(user, id);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable("id") @NotNull Long id) {
        userService.deleteUser(id);
    }

}
