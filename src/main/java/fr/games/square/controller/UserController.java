package fr.games.square.controller;

import fr.games.square.dto.UserCreationDto;
import fr.games.square.dto.UserDto;
import fr.games.square.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "API de gestion des utilisateurs")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un utilisateur", description = "Crée un nouvel utilisateur avec nom d'utilisateur et email")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres de création invalides")
    })
    public UserDto createUser(@RequestBody UserCreationDto creationDto) {
        return userService.createUser(creationDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur par identifiant", description = "Retourne les informations d'un utilisateur existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    public UserDto getUserById(@Parameter(description = "Identifiant UUID de l'utilisateur") @PathVariable UUID id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable pour l'id : " + id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un utilisateur", description = "Supprime définitivement un utilisateur par son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    public void deleteUser(@Parameter(description = "Identifiant UUID de l'utilisateur") @PathVariable UUID id) {
        userService.deleteUser(id);
    }

    @GetMapping("/{id}/valid")
    @Operation(summary = "Vérifier la validité d'un utilisateur", description = "Vérifie si un identifiant d'utilisateur existe en base (utilisé par l'application de jeux)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur valide et existant"),
            @ApiResponse(responseCode = "404", description = "Utilisateur inexistant")
    })
    public ResponseEntity<Boolean> isUserValid(@Parameter(description = "Identifiant UUID de l'utilisateur") @PathVariable UUID id) {
        boolean valid = userService.isUserValid(id);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs", description = "Retourne l'ensemble des utilisateurs enregistrés")
    @ApiResponse(responseCode = "200", description = "Liste des utilisateurs")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
