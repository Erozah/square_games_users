package fr.games.square.dto;

import java.util.UUID;

public record UserDto(UUID id, String username, String email) {

}
