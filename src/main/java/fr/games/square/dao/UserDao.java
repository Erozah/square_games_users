package fr.games.square.dao;

import fr.games.square.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDao {
    UserEntity save(UserEntity user);
    Optional<UserEntity> findById(UUID id);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
