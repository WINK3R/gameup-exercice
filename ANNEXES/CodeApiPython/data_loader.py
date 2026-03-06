from __future__ import annotations

import os
from functools import lru_cache
from typing import Dict
from urllib.parse import quote_plus

import pandas as pd

try:
    from sqlalchemy import create_engine, text
    from sqlalchemy.engine import Engine

    SQLALCHEMY_AVAILABLE = True
except ImportError:  # pragma: no cover - executed only when dependency missing.
    create_engine = None  # type: ignore
    text = None  # type: ignore
    Engine = None  # type: ignore
    SQLALCHEMY_AVAILABLE = False


EXPECTED_COLUMNS = {"user_id", "game_id", "game_name", "rating"}


class RatingsDataError(RuntimeError):
    """Raised when ratings data cannot be loaded from the configured source."""


@lru_cache(maxsize=1)
def load_ratings_dataframe() -> pd.DataFrame:
    """Load the ratings dataset from the transactional database."""

    return _load_ratings_from_database()


@lru_cache(maxsize=1)
def game_lookup() -> Dict[int, str]:
    df = load_ratings_dataframe()
    unique_games = df.drop_duplicates("game_id")[["game_id", "game_name"]]
    return unique_games.set_index("game_id")["game_name"].to_dict()


def _load_ratings_from_database() -> pd.DataFrame:
    engine = _create_engine()
    query = text(
        """
        SELECT r.user_id, r.game_id, g.title AS game_name, r.note AS rating
        FROM reviews AS r
        INNER JOIN games AS g ON g.id = r.game_id
        WHERE r.note IS NOT NULL
        """
    )

    try:
        with engine.connect() as connection:
            df = pd.read_sql_query(query, connection)
    except Exception as exc:  # pragma: no cover - depends on DB connectivity.
        raise RatingsDataError(f"Impossible de charger les avis depuis la base de données: {exc}") from exc

    if df.empty:
        raise RatingsDataError("Aucun avis trouvé dans la base de données pour construire les recommandations")

    _validate_columns(df)
    return df


def _create_engine() -> Engine:
    if not SQLALCHEMY_AVAILABLE:
        raise RatingsDataError(
            "SQLAlchemy n'est pas installé. Installez les dépendances avec 'pip install sqlalchemy mysql-connector-python'"
        )

    database_url = _database_url()
    try:  # pragma: no cover - relies on external DB/driver.
        return create_engine(database_url, pool_pre_ping=True)
    except Exception as exc:  # pragma: no cover - DB driver/config errors.
        raise RatingsDataError(f"Impossible de créer une connexion vers {database_url}: {exc}") from exc


def _database_url() -> str:
    explicit_url = os.getenv("RECOMMENDATION_DATABASE_URL") or os.getenv("DATABASE_URL")
    if explicit_url:
        return explicit_url

    host = os.getenv("GAMESUP_DB_HOST") or os.getenv("MYSQL_HOST", "localhost")
    port = os.getenv("GAMESUP_DB_PORT", "3306")
    name = os.getenv("GAMESUP_DB_NAME", "GamesUP")
    user = os.getenv("GAMESUP_DB_USER", "root")
    password = os.getenv("GAMESUP_DB_PASSWORD", "12345678")

    safe_user = quote_plus(user)
    safe_password = quote_plus(password)
    return f"mysql+mysqlconnector://{safe_user}:{safe_password}@{host}:{port}/{name}"


def _validate_columns(df: pd.DataFrame, origin: str | None = None) -> None:
    if not EXPECTED_COLUMNS.issubset(df.columns):
        location = f" dans {origin}" if origin else ""
        raise ValueError(
            f"Le dataset{location} doit contenir les colonnes {EXPECTED_COLUMNS}, trouvé {set(df.columns)}"
        )
