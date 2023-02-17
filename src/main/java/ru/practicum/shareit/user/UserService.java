package ru.practicum.shareit.user;

import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    public List<User> findAll();

    public User findUser(Long id);

    public User create(User user) throws ValidationException;

    public User update(UserDto user, Long id);

    public void deleteUser(Long id);
}
