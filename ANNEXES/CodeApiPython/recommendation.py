from __future__ import annotations

from typing import Dict, List

import numpy as np
import pandas as pd

from data_loader import game_lookup, load_ratings_dataframe
from models import UserData

DEFAULT_K = 3
DEFAULT_TOP_N = 5


def generate_recommendations(user_data: UserData, k: int = DEFAULT_K, top_n: int = DEFAULT_TOP_N) -> List[Dict]:
    """Compute KNN recommendations for the given user profile."""
    if not user_data.purchases:
        return _top_overall_games(user_data, top_n)

    ratings_df = load_ratings_dataframe()
    user_matrix = ratings_df.pivot_table(index="user_id", columns="game_id", values="rating", aggfunc="mean").fillna(0.0)

    # Ensure the matrix contains the same features as the incoming payload
    for purchase in user_data.purchases:
        if purchase.game_id not in user_matrix.columns:
            user_matrix[purchase.game_id] = 0.0
    user_matrix = user_matrix.sort_index(axis=1)

    user_vector = pd.Series(0.0, index=user_matrix.columns)
    for purchase in user_data.purchases:
        user_vector.loc[purchase.game_id] = purchase.rating

    matrix_values = user_matrix.values
    target_vector = user_vector.values
    similarities = _cosine_similarity(matrix_values, target_vector)

    if not np.any(similarities > 0):
        return _top_overall_games(user_data, top_n)

    top_indices = np.argsort(similarities)[::-1][:k]
    neighbor_weights = similarities[top_indices]
    neighbor_matrix = matrix_values[top_indices]

    purchased_ids = {purchase.game_id for purchase in user_data.purchases}
    scores: Dict[int, float] = {}
    for col_idx, game_id in enumerate(user_matrix.columns):
        if game_id in purchased_ids:
            continue
        neighbor_ratings = neighbor_matrix[:, col_idx]
        positive_mask = neighbor_ratings > 0
        if not positive_mask.any():
            continue
        weights = neighbor_weights[positive_mask]
        if weights.sum() == 0:
            continue
        weighted_score = np.dot(neighbor_ratings[positive_mask], weights) / weights.sum()
        scores[game_id] = float(weighted_score)

    if not scores:
        return _top_overall_games(user_data, top_n)

    lookup = game_lookup()
    sorted_scores = sorted(scores.items(), key=lambda item: item[1], reverse=True)[:top_n]
    recommendations = [
        {
            "game_id": int(game_id),
            "game_name": lookup.get(int(game_id), f"Game {game_id}"),
            "score": round(score, 3),
        }
        for game_id, score in sorted_scores
    ]
    return recommendations


def _cosine_similarity(matrix: np.ndarray, vector: np.ndarray) -> np.ndarray:
    vector_norm = np.linalg.norm(vector)
    if np.isclose(vector_norm, 0.0):
        return np.zeros(matrix.shape[0])

    matrix_norms = np.linalg.norm(matrix, axis=1)
    matrix_norms[matrix_norms == 0] = np.inf
    similarities = matrix @ vector
    similarities = similarities / (matrix_norms * vector_norm)
    return np.nan_to_num(similarities)


def _top_overall_games(user_data: UserData, top_n: int) -> List[Dict]:
    df = load_ratings_dataframe()
    purchased_ids = {purchase.game_id for purchase in user_data.purchases}
    averages = (
        df.groupby(["game_id", "game_name"])["rating"].mean().reset_index().sort_values(by="rating", ascending=False)
    )
    fallback = averages[~averages["game_id"].isin(purchased_ids)].head(top_n)
    return [
        {
            "game_id": int(row.game_id),
            "game_name": row.game_name,
            "score": round(float(row.rating), 3),
        }
        for _, row in fallback.iterrows()
    ]
