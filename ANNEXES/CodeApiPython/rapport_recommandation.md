# Système de recommandation GamesUP

## Travail réalisé pour mettre en place le service

1. **Architecture FastAPI dédiée** (`main.py`). Création d'un micro-service indépendant qui expose deux routes (`GET /` pour la supervision, `POST /recommendations/` pour la prédiction) et valide les entrées grâce aux modèles Pydantic (`models.py`).
2. **Chargement unifié des données** (`data_loader.py`). Mise en place d'un connecteur SQLAlchemy vers la base MySQL `GamesUP`, avec construction paresseuse et mise en cache (`@lru_cache`) du DataFrame de notes pour éviter de recharger toute la table à chaque requête.
3. **Préparation de la matrice utilisateur / jeu** (`recommendation.py`). Transformation des avis en matrice pivotée afin de représenter chaque joueur comme un vecteur de préférences. Les jeux absents de la base sont ajoutés dynamiquement pour rester compatibles avec les payloads entrants.
4. **Algorithme de recommandation KNN**. Calcul de la similarité cosinus entre le profil reçu et tous les utilisateurs connus, sélection des `k` voisins les plus proches (3 par défaut), puis agrégation pondérée de leurs notes pour scorer les jeux non achetés. Chaque recommandation retourne `game_id`, `game_name` et un score normalisé.
5. **Stratégie de repli**. Lorsque l'utilisateur n'a aucun achat ou aucune similarité significative, un fallback renvoie les jeux les mieux notés globalement afin de garantir une réponse exploitable.
6. **Intégration avec l'API Spring Boot**. L'API Java appelle ce service via `recommendation.service.base-url`, ce qui découple la logique de scoring du reste de la plateforme tout en partageant la même base transactionnelle.

## Bonnes pratiques appliquées

- **Séparation des responsabilités** : acquisition des données, modèle métier et calcul des recommandations vivent dans des modules distincts, ce qui facilite les évolutions.
- **Variables d'environnement** : la connexion MySQL est pilotée par `RECOMMENDATION_DATABASE_URL` ou des variables `GAMESUP_DB_*`, évitant de figer des secrets dans le code.
- **Fallback explicite et messages clairs** : les erreurs de connexion ou d'absence de données remontent des exceptions métiers (`RatingsDataError`) compréhensibles.
- **Mise en cache des lectures** : l'utilisation d'`@lru_cache` sur le chargement du DataFrame limite la pression sur la base pour des requêtes successives identiques.
- **Validation stricte du contrat d'entrée** : Pydantic garantit que chaque requête contient un `user_id` et une liste d'achats correctement typés, réduisant les risques d'injection de données incohérentes.

## Points perfectibles / mauvaises pratiques constatées

- **Secrets par défaut dans le code** : l'utilisateur et le mot de passe MySQL par défaut (`root` / `12345678`) figurent encore dans le code source, ce qui n'est acceptable que pour une démo locale.
- **Couplage fort à MySQL** : aucune source alternative (CSV, cache en mémoire, réplication) n'est prévue ; toute indisponibilité de la base coupe les recommandations.
- **Absence de tests automatisés** : aucun test unitaire ou d'intégration ne valide les calculs KNN ni les cas d'erreur, rendant les régressions difficiles à détecter.
- **Pas de monitoring ni de limites de charge** : le service ne trace pas les temps de réponse ni ne protège la base contre un afflux de requêtes (pas de pagination, pas de throttling).
- **Gestion limitée de la fraîcheur des données** : la mise en cache du DataFrame n'est jamais invalidée automatiquement ; les nouvelles notes ne sont donc pas visibles tant que le processus n'est pas redémarré.
- **Manque de personnalisation avancée** : seules les notes explicites sont considérées, sans pondération temporelle ni signaux implicites (temps de jeu, catégories préférées), ce qui limite la pertinence à long terme.

## Axes d'amélioration proposés

1. Externaliser totalement la configuration sensible (vault, secrets manager) et activer TLS sur la connexion MySQL.
2. Introduire des tests (unitaires pour `_cosine_similarity` et le fallback, tests d'intégration contre une base de données de démonstration).
3. Ajouter une couche de cache invalidable (Redis, TTL) ou une tâche d'actualisation asynchrone pour garder les données fraîches.
4. Instrumenter l'API (logs structurés, métriques Prometheus) et prévoir un mécanisme de limitation de débit.
5. Étendre les signaux pris en compte (genres préférés, popularité globale, hybridation contenu/collaboratif) et permettre de régler dynamiquement `k`/`top_n` selon l'utilisateur.
