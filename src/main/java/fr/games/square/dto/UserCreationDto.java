package fr.games.square.dto;

import java.util.UUID;

public class UserCreationDto {
    private UUID id;
    private String username;
    private String email;

    public UserCreationDto() {
    }

    public UserCreationDto(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public UserCreationDto(UUID id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
