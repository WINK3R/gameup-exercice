package com.gamesUP.gamesUP.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamesUP.gamesUP.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
