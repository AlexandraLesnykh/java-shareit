package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.ActionHasAlreadyDoneException;
import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private long generator = 0;
    private List<String> emails = new ArrayList<>();

    private final JdbcTemplate jdbcTemplate;

    public UserServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        maxId();
    }

    @Override
    public List<User> findAll() {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users");
        List<User> usersSQL = new ArrayList<>();
        while (userRows.next()) {
            usersSQL.add(getUserBD(userRows));
        }
        return usersSQL;
    }

    @Override
    public User findUser(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE user_id= ?";
        List<User> usersList = jdbcTemplate.query(sqlQuery, UserRowMapper::mapRowToUser, id);
        if (usersList.size() != 1) {
            throw new ObjectNotFoundException("hb");
        }
        return usersList.get(0);
    }

    @Override
    public User create(User user) throws ValidationException {
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

    @Override
    public User update(UserDto user, Long id) {
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

    @Override
    public void deleteUser(Long id) {
        jdbcTemplate.update("DELETE FROM users WHERE user_id=?", id);
        emailListChanges();
    }

    private void maxId() {
        Long i = jdbcTemplate.queryForObject("SELECT MAX(user_id) FROM users", Long.class);
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
        userSql.setId(userRows.getLong("user_id"));
        userSql.setEmail(userRows.getString("email"));
        userSql.setName(userRows.getString("name"));
        return userSql;
    }
}
