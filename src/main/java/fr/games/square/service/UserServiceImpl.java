package fr.games.square.service;

import fr.games.square.dao.UserDao;
import fr.games.square.dto.UserCreationDto;
import fr.games.square.dto.UserDto;
import fr.games.square.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDto createUser(UserCreationDto creationDto) {
        UserEntity entity = new UserEntity(UUID.randomUUID(), creationDto.username(), creationDto.email());
        UserEntity saved = userDao.save(entity);
        return new UserDto(saved.getId(), saved.getUsername(), saved.getEmail());
    }

    @Override
    public Optional<UserDto> getUserById(UUID id) {
        return userDao.findById(id)
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail()));
    }

    @Override
    public void deleteUser(UUID id) {
        userDao.deleteById(id);
    }

    @Override
    public boolean isUserValid(UUID id) {
        return userDao.existsById(id);
    }
}
