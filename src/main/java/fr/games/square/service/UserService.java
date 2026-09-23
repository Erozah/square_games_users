package fr.games.square.service;

import fr.games.square.dto.UserCreationDto;
import fr.games.square.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserDto createUser(UserCreationDto creationDto);
    Optional<UserDto> getUserById(UUID id);
    void deleteUser(UUID id);
    boolean isUserValid(UUID id);
    List<UserDto> getAllUsers();
}
