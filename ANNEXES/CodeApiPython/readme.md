# API de recommandation (FastAPI + KNN)

Cette API expose un algorithme de recommandation très léger basé sur les *k plus proches voisins*. Les données sont désormais récupérées directement depuis la base MySQL `GamesUP` (tables `reviews` et `games`).

## Démarrage complet (API Java + FastAPI)

1. **Préparer MySQL**
   - Créez la base `GamesUP` si elle n'existe pas : `CREATE DATABASE GamesUP CHARACTER SET utf8mb4;`.
   - Importez les données de démo :

     ```bash
     mysql -u root -p GamesUP < ../gamesUP/seed/seed-gamesup.sql
     ```

     (adaptez l'utilisateur/host selon votre installation MySQL).
2. **Identifiants disponibles** après le seed (mot de passe commun `password`) :
   - Admin : `admin@gamesup.com`
   - Client : `lea@gamesup.com`
   - Client : `marc@gamesup.com`
3. **Lancer l'API Spring Boot** (toutes les routes sont sécurisées par JWT et nécessitent un token obtenu via `/api/auth/login`).

   ```bash
   cd ../gamesUP
   export MYSQL_HOST=localhost   # ou configurez spring.datasource.* directement
   ./mvnw spring-boot:run
   ```

   - Authentifiez-vous :

     ```bash
     curl -X POST http://localhost:8080/api/auth/login \
          -H "Content-Type: application/json" \
          -d '{"email":"lea@gamesup.com","password":"password"}'
     ```

     Réutilisez le token reçu (`Authorization: Bearer ...`) pour appeler les autres routes.
   - Récupérez les recommandations RESTful :

     ```bash
     curl -X GET http://localhost:8080/api/users/2/recommendations \
          -H "Authorization: Bearer <TOKEN>"
     ```

     Les clients ne peuvent interroger que leur propre `{userId}` ; seuls les administrateurs peuvent consulter les recommandations d'un autre utilisateur.
4. **Configurer et lancer l'API FastAPI** (recommandations) en pointant vers la même base : voir sections « Installation rapide », « Connexion à la base GamesUP » et « Lancer l'API ». L'API écoute sur `http://localhost:8000` et expose `/recommendations/`, consommé par le projet Spring via `recommendation.service.base-url`.

## Installation rapide

```bash
pip install fastapi uvicorn pydantic pandas numpy sqlalchemy mysql-connector-python
```

## Lancer l'API FastAPI

1. Exporter les variables nécessaires (voir section suivante) pour que l'API puisse se connecter à la base MySQL (par exemple en utilisant un fichier `.env`).
2. Lancer l'API :

   ```bash
   uvicorn main:app --reload
   ```

## Connexion à la base GamesUP

Le service lit **exclusivement** les notes directement dans la base MySQL `GamesUP`. Aucun CSV n'est embarqué : si la connexion échoue ou que les tables sont vides, l'API renvoie une erreur explicite pour éviter des recommandations obsolètes.

Il suffit de réutiliser les mêmes variables que le projet Java :

```bash
export MYSQL_HOST=localhost       # ou GAMESUP_DB_HOST
export GAMESUP_DB_USER=root
export GAMESUP_DB_PASSWORD=12345678
export GAMESUP_DB_NAME=GamesUP
```

Vous pouvez également fournir une URL complète via `RECOMMENDATION_DATABASE_URL`/`DATABASE_URL`
(`mysql+mysqlconnector://user:pass@host:port/db`).

Astuce : placez ces variables dans `CodeApiPython/.env`, puis chargez-les avant de démarrer :

```bash
set -a && source .env && set +a
```

## Résumé des variables utilisées

| Variable | Description | Valeur par défaut |
| --- | --- | --- |
| `MYSQL_HOST` / `GAMESUP_DB_HOST` | Hôte MySQL partagé avec l'API Spring | `localhost` |
| `GAMESUP_DB_PORT` | Port MySQL | `3306` |
| `GAMESUP_DB_NAME` | Nom de la base | `GamesUP` |
| `GAMESUP_DB_USER` | Utilisateur MySQL | `root` |
| `GAMESUP_DB_PASSWORD` | Mot de passe | `12345678` |
| `RECOMMENDATION_DATABASE_URL` / `DATABASE_URL` | URL SQLAlchemy complète (optionnelle) | dérivée des variables ci-dessus |

Endpoints disponibles :

- `GET /` : ping de santé.
- `POST /recommendations/` : envoie un payload `UserData` et récupère les recommandations calculées.

Exemple de payload:

```json
{
  "user_id": 999,
  "purchases": [
    {"game_id": 101, "rating": 4.5},
    {"game_id": 105, "rating": 5.0}
  ]
}
```

## Algorithme

1. Chargement des données (`data_loader.py`) et construction d'une matrice utilisateur/jeu.
2. Conversion du profil utilisateur reçu en vecteur.
3. Calcul de la similarité cosinus entre le profil et tous les utilisateurs connus.
4. Pondération des notes des voisins les plus proches (K=3 par défaut) pour scorer les jeux non encore achetés.
5. Fallback : si l'utilisateur n'a aucune similarité significative, l'API renvoie les jeux les mieux notés globalement.
