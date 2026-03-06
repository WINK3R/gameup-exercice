# Architecture cible

## Architecture REST
L’API expose des ressources métiers sous forme d’URI cohérentes et de verbes HTTP standards.

### Ressources principales
- `/api/games` : gestion du catalogue de jeux.
- `/api/users` : gestion des utilisateurs.
- `/api/orders` : commandes et lignes de commande.
- `/api/authors`, `/api/publishers`, `/api/categories` : référentiels.
- `/api/wishlists` et `/api/reviews` : fonctionnalités complémentaires.
- `/api/recommendations` : recommandations issues de l’API Python.
- `/api/auth/login` : authentification stateless et émission d'un token JWT.

### Verbes HTTP
- `GET /api/games` : liste paginée + filtres de recherche.
- `GET /api/games/{id}` : détail d’un jeu.
- `POST /api/games` : création (ADMIN).
- `PUT /api/games/{id}` : mise à jour complète (ADMIN).
- `PATCH /api/games/{id}` : mise à jour partielle (ADMIN).
- `DELETE /api/games/{id}` : suppression (ADMIN).
- `POST /api/recommendations` : recommandations pour un utilisateur.
- `POST /api/auth/login` : vérifie l'email/mot de passe puis renvoie un Bearer token.

## Séparation des responsabilités
- **Controller** : validation HTTP, mapping des entrées/sorties, codes de réponse.
- **Service** : logique métier, règles de validation, orchestration des appels externes.
- **Repository** : accès aux données via Spring Data JPA.

## Stratégie DTOs et Mappers
- DTOs distincts pour **entrée** et **sortie** (ex. `GameCreateRequest`, `GameResponse`).
- Entités JPA non exposées directement en API.
- Mapping via MapStruct ou mappers manuels (classe `*Mapper`).
- Inclusion contrôlée des relations (éviter les cycles et la surcharge JSON).

## Conception des entités et relations
- **User** (1) — (N) **Purchase**
- **Purchase** (1) — (N) **PurchaseLine**
- **Game** (1) — (N) **PurchaseLine**
- **Game** (N) — (1) **Publisher**
- **Game** (N) — (N) **Author**
- **Game** (N) — (N) **Category**
- **User** (1) — (N) **Review**
- **Game** (1) — (N) **Review**
- **User** (1) — (N) **Wishlist** ; **Wishlist** (N) — (N) **Game**

Les relations seront matérialisées avec JPA (`@OneToMany`, `@ManyToOne`, `@ManyToMany`) et des clés étrangères explicites en base.
