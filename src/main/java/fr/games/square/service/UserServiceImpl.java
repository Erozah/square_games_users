package fr.games.square.service;

import fr.games.square.dao.UserDao;
import fr.games.square.dto.UserCreationDto;
import fr.games.square.dto.UserDto;
import fr.games.square.entity.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
        if (creationDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les informations de l'utilisateur sont obligatoires");
        }
        if (creationDto.getUsername() == null || creationDto.getUsername().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom d'utilisateur (username) est obligatoire");
        }
        if (creationDto.getEmail() == null || creationDto.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'adresse email est obligatoire");
        }

        UUID id = creationDto.getId() != null ? creationDto.getId() : UUID.randomUUID();
        UserEntity entity = new UserEntity(id, creationDto.getUsername().trim(), creationDto.getEmail().trim());
        UserEntity saved = userDao.save(entity);

        return toDto(saved);
    }

    @Override
    public Optional<UserDto> getUserById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return userDao.findById(id).map(this::toDto);
    }

    @Override
    public void deleteUser(UUID id) {
        if (id == null || !userDao.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable");
        }
        userDao.deleteById(id);
    }

    @Override
    public boolean isUserValid(UUID id) {
        if (id == null) {
            return false;
        }
        return userDao.existsById(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    private UserDto toDto(UserEntity entity) {
        return new UserDto(entity.getId(), entity.getUsername(), entity.getEmail());
    }
}
