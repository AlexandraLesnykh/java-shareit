package ru.practicum.shareit.ServiceTest.UserTest;

import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class UserServiceTestUtils {

    public UserServiceTestUtils() {
    }

    public static List<User> getUsers() {
        return Arrays.asList(
                getUsers(1L, "Alex", "alex@ya.ru"),
                getUsers(2L, "Mary", "mary@mail.ru"),
                getUsers(3L, "Sam", "sam@google.com"),
                getUsers(4L, "Bob", "bob@icloud.com"),
                getUsers(5L, "Kate", "kate@rambler.ru")
        );
    }

    public static User getUser(Long id) {
        return getUsers()
                .stream()
                .filter(user -> user.getId() == id).findAny().orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
    }

    public static List<User> getUsersWithoutIds() {
        return getUsers()
                .stream()
                .peek(user -> user.setId(0L))
                .collect(toList());
    }

    public static User getUserAfterUpdate(User user, long id) {
        User userForReturn = getUser(id);
        userForReturn.setName(user.getName());
        userForReturn.setEmail(user.getEmail());
        return userForReturn;
    }

    private static User getUsers(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
