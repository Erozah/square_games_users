package fr.games.square.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.games.square.dto.UserCreationDto;
import fr.games.square.dto.UserDto;
import fr.games.square.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void userLifecycle_create_get_valid_delete() throws Exception {
        // 1. Create User
        UserCreationDto creationDto = new UserCreationDto("alice", "alice@example.com");
        String createResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andReturn().getResponse().getContentAsString();

        UserDto createdUser = objectMapper.readValue(createResponse, UserDto.class);
        UUID userId = createdUser.getId();
        assertNotNull(userId);

        // 2. Get User by ID
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("alice"));

        // 3. Check User Valid
        mockMvc.perform(get("/users/{id}/valid", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // 4. Check Non-existing User Valid
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(get("/users/{id}/valid", fakeId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("false"));

        // 5. Delete User
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        // 6. Verify User is now Not Found & Not Valid
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/users/{id}/valid", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_MissingUsername_ReturnsBadRequest() throws Exception {
        UserCreationDto creationDto = new UserCreationDto("", "test@example.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationDto)))
                .andExpect(status().isBadRequest());
    }
}
