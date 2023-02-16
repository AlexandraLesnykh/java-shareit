package ru.practicum.shareit.user;

import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exeptions.ActionHasAlreadyDoneException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(
        value = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private int generator = 0;
    private List<String> emails = new ArrayList<>();

    private final JdbcTemplate jdbcTemplate;

    public UserController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        maxId();
    }

    @GetMapping
    public List<User> findAll() {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users");
        List<User> usersSQL = new ArrayList<>();
        while (userRows.next()) {
            usersSQL.add(getUserBD(userRows));
        }
        return usersSQL;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User findUser(@PathVariable("id") @NotNull Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id= ?", id);
        userRows.next();
        if (userRows.last()) {
            return getUserBD(userRows);
        } else {
            return null;
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User create(@RequestBody @Valid User user) throws ValidationException {
        addUser(user);
        if (!emails.isEmpty() && emails.contains(user.getEmail())) {
            generator = generator - 1;
            throw new ActionHasAlreadyDoneException("Wrong email");
        } else if (user.getEmail() == null || user.getName() == null) {
            generator = generator - 1;
            throw new ValidationException("Wrong request");
        }
        jdbcTemplate.update("INSERT INTO users VALUES (?,?,?)", user.getId(), user.getName(), user.getEmail());
        emailListChanges();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id= ?", user.getId());
        userRows.next();
        return getUserBD(userRows);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public User update(@RequestBody @NotNull @Valid UserDto user, @PathVariable("id") @NotNull Integer id) {
        if (user.getName() == null) {
            jdbcTemplate.update("UPDATE users SET email=? WHERE user_id=?", user.getEmail(), id);
        } else if (user.getEmail() == null) {
            jdbcTemplate.update("UPDATE users SET name=? WHERE user_id=?", user.getName(), id);
        } else {
            jdbcTemplate.update("UPDATE users SET name=?, email=? WHERE user_id=?", user.getName(), user.getEmail(), id);
        }
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id= ?", id);
        emailListChanges();
        if (userRows.next()) {
            return getUserBD(userRows);
        } else {
            return null;
        }
    }


    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable("id") @NotNull Integer id) {
        jdbcTemplate.update("DELETE FROM users WHERE user_id=?", id);
        emailListChanges();
    }

    private void maxId() {
        Integer i = jdbcTemplate.queryForObject("SELECT MAX(user_id) FROM users", Integer.class);
        if (i == null) {
            generator = 0;
        } else {
            generator = i;
        }
    }

    private void emailListChanges() {
        SqlRowSet userRowsCheck = jdbcTemplate.queryForRowSet("SELECT * FROM users");
        while (userRowsCheck.next()) {
            emails.clear();
            emails.add(getUserBD(userRowsCheck).getEmail());
        }
    }

    private void addUser(User user) {
        if (user.getId() == 0) {
            user.setId(++generator);
        }
    }

    private User getUserBD(SqlRowSet userRows) {
        User userSql = new User();
        userSql.setId(userRows.getInt("user_id"));
        userSql.setEmail(userRows.getString("email"));
        userSql.setName(userRows.getString("name"));
        return userSql;
    }
}
