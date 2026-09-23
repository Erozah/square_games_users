# Users API - Service de gestion des utilisateurs

Application Spring Boot dédiée à la gestion des utilisateurs pour la plateforme Square Games (Itération 4).

## Fonctionnalités

- **API REST CRUD** pour les utilisateurs (`/users`).
- **Endpoint de validation** `/users/{id}/valid` permettant aux services externes (notamment l'API de jeux) de vérifier l'existence d'un joueur.
- **Architecture multicouche** : Controller -> Service -> DAO -> Repository JPA.
- **Persistance** :
  - Profil `h2` par défaut (base fichier persistante dans `./data/usersdb`).
  - Profil `mysql` configurable via `application-mysql.properties`.
- **Documentation OpenAPI / Swagger** intégrée.

## Endpoints

| Méthode | URL | Description | Statut succès |
|---|---|---|---|
| `POST` | `/users` | Crée un utilisateur (`username`, `email`) | `201 Created` |
| `GET` | `/users/{id}` | Récupère les informations d'un utilisateur | `200 OK` |
| `DELETE` | `/users/{id}` | Supprime un utilisateur | `204 No Content` |
| `GET` | `/users/{id}/valid` | Vérifie si un identifiant existe | `200 OK` (`true`) ou `404 Not Found` |
| `GET` | `/users` | Liste tous les utilisateurs | `200 OK` |

## Démarrage

### Prérequis
- Java 21+

### Lancement en mode H2 (par défaut sur le port 8081)
```bash
./mvnw spring-boot:run
```

L'application démarre sur le port `8081` :
- API : `http://localhost:8081/users`
- Swagger UI : `http://localhost:8081/swagger-ui/index.html`
- Console H2 : `http://localhost:8081/h2-console` (JDBC URL: `jdbc:h2:file:./data/usersdb`, User: `sa`, Password: vide)

### Lancement avec profil MySQL
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

### Exécution des tests
```bash
./mvnw test
```
