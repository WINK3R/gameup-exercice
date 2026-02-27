package com.gamesUP.gamesUP.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.model.Category;
import com.gamesUP.gamesUP.repository.CategoryRepository;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll(Sort.by("name"));
    }

    public Category create(Category category) {
        validateCategory(category);
        return categoryRepository.save(category);
    }

    public Category update(Long id, Category category) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégorie introuvable"));
        validateCategory(category);
        existing.setName(category.getName());
        existing.setSlug(category.getSlug());
        existing.setDescription(category.getDescription());
        return categoryRepository.save(existing);
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Catégorie introuvable");
        }
        categoryRepository.deleteById(id);
    }

    private void validateCategory(Category category) {
        if (category == null || !StringUtils.hasText(category.getName()) || !StringUtils.hasText(category.getSlug())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nom et slug sont requis pour la catégorie");
        }
        category.setName(category.getName().trim());
        category.setSlug(category.getSlug().trim().toLowerCase());
    }
}
