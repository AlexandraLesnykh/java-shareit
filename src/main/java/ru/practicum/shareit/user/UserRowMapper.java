package ru.practicum.shareit.user;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper {

    public static User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        return user;
    }
}
