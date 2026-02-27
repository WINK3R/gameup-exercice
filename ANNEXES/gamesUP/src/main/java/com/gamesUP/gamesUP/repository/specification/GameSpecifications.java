package com.gamesUP.gamesUP.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.gamesUP.gamesUP.dto.GameSearchRequest;
import com.gamesUP.gamesUP.model.Game;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public final class GameSpecifications {

    private GameSpecifications() {
    }

    public static Specification<Game> withSearch(GameSearchRequest criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria == null) {
                return cb.conjunction();
            }

            if (StringUtils.hasText(criteria.getKeyword())) {
                String keyword = "%" + criteria.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), keyword),
                        cb.like(cb.lower(root.get("description")), keyword)));
            }

            if (StringUtils.hasText(criteria.getGenre())) {
                predicates.add(cb.equal(cb.lower(root.get("genre")), criteria.getGenre().toLowerCase()));
            }

            if (criteria.getCategoryId() != null) {
                predicates.add(cb.equal(root.join("category", JoinType.LEFT).get("id"), criteria.getCategoryId()));
            }

            if (criteria.getPublisherId() != null) {
                predicates.add(cb.equal(root.join("publisher", JoinType.LEFT).get("id"), criteria.getPublisherId()));
            }

            if (criteria.getAuthorId() != null) {
                predicates.add(cb.equal(root.join("author", JoinType.LEFT).get("id"), criteria.getAuthorId()));
            }

            if (criteria.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), criteria.getMinPrice()));
            }

            if (criteria.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), criteria.getMaxPrice()));
            }

            if (criteria.getMinPlayers() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("minPlayers"), criteria.getMinPlayers()));
            }

            if (criteria.getMaxPlayers() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("maxPlayers"), criteria.getMaxPlayers()));
            }

            if (criteria.getMinRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), criteria.getMinRating()));
            }

            if (Boolean.TRUE.equals(criteria.getInStock())) {
                predicates.add(cb.greaterThan(root.get("stock"), 0));
            }

            if (criteria.getReleasedAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("releaseDate"), criteria.getReleasedAfter()));
            }

            if (criteria.getReleasedBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("releaseDate"), criteria.getReleasedBefore()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
