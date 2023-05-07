package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.exeptions.BadRequestException;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User findUser(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
        return user;
    }

    @Override
    public User create(User user) throws BadRequestException {
        if (user.getEmail() == null || user.getName() == null) {
            throw new BadRequestException("Wrong request");
        }
        return repository.save(user);
    }

    @Override
    public User update(User user, Long id) {
        User user1 = repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
        if (user.getName() == null) {
            user1.setEmail(user.getEmail());
        } else if (user.getEmail() == null) {
            user1.setName(user.getName());
        } else {
            user1.setEmail(user.getEmail());
            user1.setName(user.getName());
        }
        return repository.save(user1);
    }

    @Override
    public void deleteUser(Long id) {
        User user = repository.getById(id);
        repository.delete(user);
    }
}
