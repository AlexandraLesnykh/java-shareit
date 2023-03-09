package ru.practicum.shareit.user;

import ru.practicum.shareit.exeptions.ValidationException;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findUser(Long id);

    User create(User user) throws ValidationException;

    User update(User user, Long id);

    void deleteUser(Long id);
}
