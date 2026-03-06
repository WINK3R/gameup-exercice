# Explications sur le respect des principes SOLID et les bonnes pratiques

## Architecture globale
L'API est organisée suivant les couches classiques Spring Boot : les **controllers** orchestrent les requêtes HTTP, les **services** portent la logique métier, les **repositories** encapsulent l'accès aux données et les **DTO** véhiculent les données d'entrée/sortie. Chaque package remplit un rôle clairement identifié, ce qui simplifie la compréhension du projet et limite les effets de bord.

## Respect des principes SOLID
### S — Single Responsibility
- Les services sont spécialisés par agrégat métier (`GameService`, `WishlistService`, etc.) et regroupent validations, conversions et règles de cohérence. Par exemple, `GameService` centralise les validations métier et la résolution des entités liées avant persistance.
- Les repositories Spring Data ne font qu'exposer les opérations CRUD déclaratives, sans logique supplémentaire.

### O — Open/Closed
- Les spécifications JPA (`GameSpecifications`) permettent d'enrichir les critères de recherche sans modifier la logique des services : ajouter un nouveau filtre revient à compléter la spécification, les autres couches restent inchangées.
- L'utilisation des DTO (`GameRequest`, `GameSearchRequest`, `AuthRequest`, etc.) protège les services et entités des changements d'API en encapsulant les attributs exposés.

### L — Liskov Substitution
- Les entités et repositories reposent exclusivement sur les contrats Spring Data `JpaRepository`. Toute implémentation compatible peut être substituée sans modifier les consommateurs car seules les interfaces publiques sont utilisées.
- Les contrôleurs consomment les services via leurs contrats publics (constructeurs) et n'ont aucune dépendance sur les détails d'implémentation.

### I — Interface Segregation
- Les contrôleurs REST segmentent clairement les responsabilités (ex. un contrôleur par ressource : `GameController`, `PurchaseController`, etc.), ce qui évite des interfaces gonflées côté client.
- Les DTO regroupent uniquement les champs nécessaires à l'opération correspondante (`AuthRequest` ne contient que l'email et le mot de passe, `GameSearchRequest` seulement les filtres utilisables).

### D — Dependency Inversion
- Toutes les dépendances sont injectées via les constructeurs, ce qui facilite le remplacement par des doubles de test et limite le couplage fort.
- Les services ne manipulent jamais l'implémentation concrète du contexte de sécurité ou de persistance ; ils dépendent d'abstractions fournies par Spring (ex. `AuthenticationManager`, `PasswordEncoder`, `JpaRepository`).
- Les services critiques exposent désormais des interfaces (`GameService`, `PurchaseService`, `PurchaseLineService`), ce qui rend explicites leurs contrats et autorise la substitution facile d'implémentations.

## Bonnes pratiques mises en place
- **Validation systématique** : les services utilisent `ResponseStatusException` pour remonter des erreurs métier cohérentes (ex. validations de prix et de stock dans `GameService`).
- **Transactions maîtrisées** : l'annotation `@Transactional` est utilisée pour garantir une cohérence d'écriture et préserver les opérations en lecture seule.
- **Séparation des préoccupations** : les contrôleurs se contentent d'orchestrer les requêtes HTTP, toute la logique est poussée dans des services qui peuvent être testés isolément.
- **Sécurité centralisée** : l'authentification JWT et l'encodage des mots de passe sont encapsulés respectivement dans `JwtService` et `UserService`.

## Améliorations réalisées
1. **Extraction d'un service de contexte utilisateur (`CurrentUserService`)** : la logique de récupération de l'utilisateur connecté était dupliquée dans `PurchaseService` et `PurchaseLineService` avec des `IllegalArgumentException`. Nous avons introduit `CurrentUserService` pour appliquer le principe DRY, renforcer la responsabilité unique et homogénéiser les erreurs (`401 Unauthorized`).
2. **Refactor des services d'achat** : `PurchaseService` et `PurchaseLineService` dépendent désormais de `CurrentUserService`. Ils n'exposent plus de méthodes privées redondantes et peuvent se concentrer sur leur logique métier.
3. **Introduction d'interfaces de services critiques** : `GameService`, `PurchaseService` et `PurchaseLineService` disposent d'un contrat clair et d'implémentations `*Impl`. Cela documente leurs responsabilités, favorise le remplacement (mocks/tests) et renforce l'application du principe d'inversion de dépendances.
