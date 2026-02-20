# Diagramme d'architecture Games Up

```mermaid
flowchart LR
    subgraph Clients
        U1[Client web / mobile]
        U2[Outils internes]
    end

    subgraph JavaAPI[Java Spring Boot API\n(ANNEXES/gamesUP)]
        C[Controllers REST]
        S[Services métiers]
        R[Repositories / JDBC]
    end

    subgraph DB[(MySQL GamesUP)]
        Tables[(Jeux, utilisateurs, commandes, avis, wishlists...)]
    end

    subgraph PythonAPI[FastAPI Recommandations\n(ANNEXES/CodeApiPython)]
        Entry[/POST /recommendations/]
        Engine[[Moteur de recommandation]]
    end

    U1 -- HTTP/JSON --> C
    U2 -- HTTP/JSON --> C

    C -- mapping DTO & validation --> S
    S -- règles métier --> R
    R -- requêtes SQL --> DB

    S -- POST /api/recommendations --> Entry
    Entry --> Engine
    Engine -- jeux recommandés --> S

    S -- réponses agrégées --> C
    C -- réponses JSON --> U1
    C -- réponses JSON --> U2
```
