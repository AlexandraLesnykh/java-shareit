package ru.practicum.shareit.ServiceTest.UserTest;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.exeptions.BadRequestException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.ServiceTest.UserTest.UserServiceTestUtils.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {
    private UserServiceImpl userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    @Order(1)
    @DisplayName("1.Get all users")
    void findAllTest() {
        when(userRepository.findAll()).thenReturn(getUsers());
        List<User> users = userService.findAll();

        Assertions.assertEquals(5, users.size());
    }

    @Test
    @Order(2)
    @DisplayName("2.Find user by id")
    void findUserTest() {
        Long id = 2L;
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(getUser(id)));
        User user = userService.findUser(id);

        Assertions.assertEquals("Mary", user.getName());
        Assertions.assertEquals("mary@mail.ru", user.getEmail());

    }

    @Test
    @Order(3)
    @DisplayName("3.Find user with id that doesn't exist")
    void shouldThrowExceptionWhileFindingUserWithWrongId() {
        Long id = 100L;
        when(userRepository.findById(id)).thenThrow(new ObjectNotFoundException("Wrong ID"));

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> userService.findUser(id));
        Assertions.assertEquals("Wrong ID", exception.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("4.Create user")
    void createTest() throws BadRequestException {

        List<User> usersWithoutIds = getUsersWithoutIds();
        when(userRepository.save(usersWithoutIds.get(0))).thenReturn(getUser(1L));
        User user = userService.create(usersWithoutIds.get(0));
        Assertions.assertEquals(1, user.getId());
        Assertions.assertEquals("Alex", user.getName());
    }

    @Test
    @Order(5)
    @DisplayName("5.Create user fail with empty name")
    void shouldThrowExceptionWhileCreatingUserWithEmptyName() {
        User user = getUsersWithoutIds().get(0);
        user.setName(null);
        when(userRepository.save(user)).thenAnswer(invocationOnMock -> new BadRequestException("Wrong request"));

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> userService.create(user));
        Assertions.assertEquals("Wrong request", exception.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("6.Create user fail with empty email")
    void shouldThrowExceptionWhileCreatingUserWithEmptyEmail() {
        User user = getUsersWithoutIds().get(0);
        user.setEmail(null);
        when(userRepository.save(user)).thenAnswer(invocationOnMock -> new BadRequestException("Wrong request"));

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> userService.create(user));
        Assertions.assertEquals("Wrong request", exception.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("7. Update test")
    void updateTest() {
        Long id = 2L;
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@ya.ru");
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(getUser(id)));
        when(userRepository.save(user)).thenReturn(getUserAfterUpdate(user, id));
        userService.update(user, id);
        User user1 = userService.findUser(id);

        Assertions.assertEquals(2, user1.getId());
        Assertions.assertEquals("Alice", user1.getName());
        Assertions.assertEquals("alice@ya.ru", user1.getEmail());
    }

    @Test
    @Order(8)
    @DisplayName("8. Update user name")
    void updateUserNameTest() {
        Long id = 2L;
        User user = new User();
        user.setName("Alice");

        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(getUser(id)));
        when(userRepository.save(user)).thenReturn(getUserAfterUpdate(user, id));

        userService.update(user, id);
        User user1 = userService.findUser(id);

        Assertions.assertEquals(2, user1.getId());
        Assertions.assertEquals("Alice", user1.getName());
        Assertions.assertEquals("mary@mail.ru", user1.getEmail());

    }

    @Test
    @Order(9)
    @DisplayName("9. Update user email")
    void updateUserEmailTest() {
        Long id = 2L;
        User user = new User();
        user.setEmail("alice@ya.ru");

        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(getUser(id)));
        when(userRepository.save(user)).thenReturn(getUserAfterUpdate(user, id));

        userService.update(user, id);
        User user1 = userService.findUser(id);

        Assertions.assertEquals(2, user1.getId());
        Assertions.assertEquals("Mary", user1.getName());
        Assertions.assertEquals("alice@ya.ru", user1.getEmail());

    }

    @Test
    @Order(10)
    @DisplayName("10. Delete user")
    void deleteUserTest() {
        Long id = 2L;
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(getUser(id)));
        when(userRepository.getById(id)).thenReturn(getUser(id));
        User user = userService.findUser(id);
        userService.deleteUser(user.getId());
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }
}
