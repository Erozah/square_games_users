package fr.games.square.service;

import fr.games.square.dao.UserDao;
import fr.games.square.dto.UserCreationDto;
import fr.games.square.dto.UserDto;
import fr.games.square.entity.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserCreationDto creationDto) {
        String username = creationDto.username().trim();
        String email = creationDto.email().trim();

        if (userDao.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce nom d'utilisateur est déjà utilisé");
        }
        if (userDao.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cette adresse email est déjà utilisée");
        }

        UserEntity entity = new UserEntity(UUID.randomUUID(), username, email);
        String hashedPassword = passwordEncoder.encode(creationDto.password());
        entity.setPassword(hashedPassword);
        UserEntity saved = userDao.save(entity);
        return new UserDto(saved.getId(), saved.getUsername(), saved.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserById(UUID id) {
        return userDao.findById(id)
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findByIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return Optional.empty();
        }
        String trimmed = identifier.trim();

        // 1. Tenter par UUID
        try {
            UUID id = UUID.fromString(trimmed);
            Optional<UserDto> byId = getUserById(id);
            if (byId.isPresent()) {
                return byId;
            }
        } catch (IllegalArgumentException ignored) {
            // Pas un format UUID, on continue
        }

        // 2. Tenter par username ou email
        return userDao.findByUsernameOrEmail(trimmed, trimmed)
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail()));
    }

    @Override
    public void deleteUser(UUID id) {
        userDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserValid(UUID id) {
        return userDao.existsById(id);
    }
}
