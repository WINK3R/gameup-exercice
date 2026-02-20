# Compte rendu de l'existant

## État des lieux général
Le projet fournit une base Spring Boot (API Java) et une API Python FastAPI de recommandation. L’API Spring expose des entités principales (jeu, auteur, éditeur, utilisateur, commandes) avec un début de contrôleur. L’API Python contient un endpoint fonctionnel, mais l’algorithme de recommandation reste factice. Le dépôt ne contient pas d’historique Git exploitable ni de documentation d’architecture.

### Points forts
- Structure Spring Boot classique en `controller`, `model`, `resources` et tests initiaux.
- Dépendances utiles déjà présentes : Spring Web, Spring Data JPA, MySQL, tests.
- Base FastAPI en place avec endpoints simples et modèles Pydantic.

### Points faibles
- Architecture incomplète : absence de couche `service`, DTOs et mapping.
- Sécurité non implémentée (rôles client/admin non gérés).
- Tests très limités et couverture insuffisante.
- Recommandation ML non implémentée (données fictives).
- Documentation technique et diagrammes absents.

## Audit de l’existant (API Spring)

### Évaluation critique
- Les entités sont présentes mais la séparation des responsabilités est incomplète (contrôleurs directs sans services).
- Les relations métier (commandes, lignes, stocks, avis) existent mais sans validation ni règles métiers explicites.
- La couche data est amorcée via JPA, sans repositories dédiés ni stratégie de requêtes.

### Points faibles identifiés
- **Modèles** : peu de validation métier, absence de contraintes explicites et d’annotations de sérialisation cohérentes.
- **Contrôleurs** : logique métier potentielle au niveau controller, endpoints incomplets.
- **Services** : inexistants, ce qui empêche la testabilité et le respect de SOLID.
- **DTOs** : non utilisés, ce qui expose les entités JPA directement.
- **Accès aux données** : pas de repositories explicites ni de requêtes de recherche dédiées.

## Cadrage et Exigences

### Analyse de l’énoncé et de la feuille de travail
- Refonte complète de l’API Spring en architecture REST solide et maintenable.
- Mise en place de Hibernate/JPA avec modèles cohérents et DTOs.
- Sécurisation via Spring Security avec gestion des rôles.
- Tests unitaires et d’intégration, couverture cible ≥ 70 %.
- Implémentation d’un algorithme de recommandation KNN dans l’API Python.
- Communication Spring → Python pour récupérer des recommandations.
- Documentation technique complète avec diagrammes UML et synthèse des bonnes pratiques.

### Exigences fonctionnelles
- CRUD complet sur les entités principales (jeux, utilisateurs, commandes, auteurs, éditeurs, catégories, inventaires, avis, listes de souhaits).
- Gestion des rôles `CLIENT` et `ADMIN` avec droits adaptés (lecture, gestion des clients, commandes).
- Fonctionnalités de recherche sur les jeux (par titre, auteur, catégorie, éditeur).
- Intégration d’un endpoint de recommandations basé sur les données utilisateur.

### Exigences non-fonctionnelles
- Respect des principes SOLID et d’une architecture REST claire.
- Mise en place de Spring Security pour l’authentification et l’autorisation.
- Objectif de couverture de tests ≥ 70 % avec tests unitaires et d’intégration.
