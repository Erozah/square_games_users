package fr.games.square.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.games.square.dto.UserCreationDto;
import fr.games.square.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "alice")
    void userLifecycle_create_get_valid_delete() throws Exception {
        // 1. Create User (Public endpoint)
        UserCreationDto creationDto = new UserCreationDto("alice", "alice@example.com", "password123");
        String createResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andReturn().getResponse().getContentAsString();

        UserDto createdUser = objectMapper.readValue(createResponse, UserDto.class);
        UUID userId = createdUser.id();
        assertNotNull(userId);

        // 2. Get User by ID (Authorized because username == authentication.name)
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("alice"));

        // 3. Check User Valid (Public endpoint)
        mockMvc.perform(get("/users/{id}/valid", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // 4. Check Non-existing User Valid (Public endpoint)
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(get("/users/{id}/valid", fakeId))
                .andExpect(status().isNotFound());

        // 5. Delete without ADMIN role should be Forbidden (403)
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUser_WithAdminRole_Succeeds() throws Exception {
        UserCreationDto creationDto = new UserCreationDto("bob", "bob@example.com", "password123");
        String createResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UserDto createdUser = objectMapper.readValue(createResponse, UserDto.class);
        UUID userId = createdUser.id();

        // Admin can delete
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "charlie")
    void getUser_AsDifferentUserWithoutAdmin_Forbidden() throws Exception {
        UserCreationDto creationDto = new UserCreationDto("dave", "dave@example.com", "password123");
        String createResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UserDto createdUser = objectMapper.readValue(createResponse, UserDto.class);
        UUID userId = createdUser.id();

        // charlie tries to access dave's profile -> PostAuthorize fails with 403 Forbidden!
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isForbidden());
    }

    @Test
    void createUser_MissingUsername_ReturnsBadRequest() throws Exception {
        UserCreationDto creationDto = new UserCreationDto("", "test@example.com", "password123");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationDto)))
                .andExpect(status().isBadRequest());
    }
}
