package com.gamesUP.gamesUP.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.model.Author;
import com.gamesUP.gamesUP.repository.AuthorRepository;

@Service
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public List<Author> findAll() {
        return authorRepository.findAll(Sort.by("fullName"));
    }

    public Author create(Author author) {
        validate(author);
        return authorRepository.save(author);
    }

    public Author update(Long id, Author author) {
        Author existing = authorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auteur introuvable"));
        validate(author);
        existing.setFullName(author.getFullName());
        existing.setCountry(author.getCountry());
        existing.setBiography(author.getBiography());
        return authorRepository.save(existing);
    }

    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auteur introuvable");
        }
        authorRepository.deleteById(id);
    }

    private void validate(Author author) {
        if (author == null || !StringUtils.hasText(author.getFullName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom de l'auteur est requis");
        }
        author.setFullName(author.getFullName().trim());
    }
}
