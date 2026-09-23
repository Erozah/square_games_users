package fr.games.square.dao;

import fr.games.square.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDao {
    List<UserEntity> findAll();
    Optional<UserEntity> findById(UUID id);
    UserEntity save(UserEntity user);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
