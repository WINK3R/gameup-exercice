SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE wishlists;
TRUNCATE TABLE reviews;
TRUNCATE TABLE purchase_lines;
TRUNCATE TABLE purchases;
TRUNCATE TABLE games;
TRUNCATE TABLE authors;
TRUNCATE TABLE publishers;
TRUNCATE TABLE categories;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

START TRANSACTION;
INSERT INTO categories (id, name, slug, description) VALUES
    (1, 'Strategy', 'strategy', 'Jeux de stratégie modernes et classiques.'),
    (2, 'Family', 'family', 'Jeux accessibles pour tous les âges.'),
    (3, 'Cooperative', 'cooperative', 'Les joueurs gagnent ou perdent ensemble.');

INSERT INTO authors (id, full_name, country, biography) VALUES
    (1, 'Klaus Teuber', 'Allemagne', 'Créateur de Catan, figure majeure du jeu de plateau.'),
    (2, 'Elizabeth Hargrave', 'États-Unis', 'Connue pour Wingspan et son approche naturaliste.'),
    (3, 'Antoine Bauza', 'France', 'Auteur polyvalent (7 Wonders, Hanabi, etc.).');

INSERT INTO publishers (id, name, country, website) VALUES
    (1, 'Days of Wonder', 'États-Unis', 'https://www.daysofwonder.com'),
    (2, 'Stonemaier Games', 'États-Unis', 'https://stonemaiergames.com'),
    (3, 'Repos Production', 'Belgique', 'https://www.rprod.com');

INSERT INTO games (
    id, title, description, genre, price, min_players, max_players,
    average_duration, release_date, rating, stock,
    category_id, publisher_id, author_id
) VALUES
    (1, 'Catan', 'Commercez et colonisez une île riche en ressources.', 'Strategy',
        44.90, 3, 4, 90, '1995-01-01', 4.6, 120, 1, 1, 1),
    (2, 'Wingspan', 'Construisez une réserve naturelle pour oiseaux.', 'Engine builder',
        59.90, 1, 5, 70, '2019-03-22', 4.8, 80, 3, 2, 2),
    (3, '7 Wonders Duel', 'Affrontez votre rival dans une civilisation antique.', 'Card drafting',
        32.00, 2, 2, 30, '2015-10-26', 4.7, 150, 1, 3, 3),
    (4, 'Azul', 'Créez une fresque de carreaux portugais colorés.', 'Abstract',
        39.00, 2, 4, 45, '2017-10-10', 4.5, 95, 2, 1, 3);

INSERT INTO users (id, email, display_name, password, role) VALUES
    (1, 'admin@gamesup.com', 'Admin', '$2a$12$s1Vzm67azdDhUoCsWQqC7e/h35UlXgskO0bmMdsAyHhAb4u/1Frl.', 'ADMIN'),
    (2, 'lea@gamesup.com', 'Léa Martin', '$2a$12$s1Vzm67azdDhUoCsWQqC7e/h35UlXgskO0bmMdsAyHhAb4u/1Frl.', 'CLIENT'),
    (3, 'marc@gamesup.com', 'Marc Dubois', '$2a$12$s1Vzm67azdDhUoCsWQqC7e/h35UlXgskO0bmMdsAyHhAb4u/1Frl.', 'CLIENT');

INSERT INTO purchases (id, user_id, `date`, paid, delivered, archived) VALUES
    (1, 2, '2025-02-15 10:15:00', 1, 1, 0),
    (2, 3, '2025-02-28 18:45:00', 1, 0, 0);

INSERT INTO purchase_lines (id, purchase_id, game_id, quantity, prix) VALUES
    (1, 1, 1, 1, 44.90),
    (2, 1, 3, 1, 32.00),
    (3, 2, 2, 1, 59.90),
    (4, 2, 4, 2, 78.00);

INSERT INTO reviews (id, game_id, user_id, commentaire, note, created_at) VALUES
    (1, 1, 2, 'Classique indémodable, parfait pour initier des amis.', 9, '2025-02-16 12:00:00'),
    (2, 2, 3, 'Magnifique matériel et mécanique fluide.', 10, '2025-03-02 09:20:00'),
    (3, 3, 2, 'Très tactique, parties rapides et tendues.', 8, '2025-03-05 21:10:00');

INSERT INTO wishlists (id, user_id, game_id, created_at) VALUES
    (1, 2, 2, '2025-03-10 08:30:00'),
    (2, 2, 4, '2025-03-12 14:05:00'),
    (3, 3, 1, '2025-03-14 17:45:00');

COMMIT;
