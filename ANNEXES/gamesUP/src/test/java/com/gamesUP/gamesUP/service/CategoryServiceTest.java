package com.gamesUP.gamesUP.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.model.Category;
import com.gamesUP.gamesUP.repository.CategoryRepository;
import com.gamesUP.gamesUP.support.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createShouldTrimFieldsAndSave() {
        Category category = TestDataFactory.createCategory(null);
        category.setName("  Strategie  ");
        category.setSlug("  STRAT  ");
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category saved = categoryService.create(category);

        assertThat(saved.getName()).isEqualTo("Strategie");
        assertThat(saved.getSlug()).isEqualTo("strat");
    }

    @Test
    void createShouldFailForMissingFields() {
        Category category = new Category();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoryService.create(category));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldFailWhenSlugMissing() {
        Category category = new Category();
        category.setName("Name only");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoryService.create(category));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldFailWhenCategoryNull() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoryService.create(null));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void findAllShouldRequestSortedList() {
        when(categoryRepository.findAll(any(Sort.class))).thenReturn(java.util.List.of(TestDataFactory.createCategory(1L)));

        categoryService.findAll();

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(categoryRepository).findAll(sortCaptor.capture());
        assertThat(sortCaptor.getValue().getOrderFor("name")).isNotNull();
    }

    @Test
    void updateShouldThrowWhenCategoryMissing() {
        when(categoryRepository.findById(11L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoryService.update(11L, TestDataFactory.createCategory(null)));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateShouldTrimFieldsAndSave() {
        Category existing = TestDataFactory.createCategory(2L);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        Category payload = new Category();
        payload.setName("  Familiaux ");
        payload.setSlug("  FAM ");
        payload.setDescription(" Jeux familiaux ");

        Category updated = categoryService.update(2L, payload);

        assertThat(updated.getName()).isEqualTo("Familiaux");
        assertThat(updated.getSlug()).isEqualTo("fam");
        assertThat(updated.getDescription()).isEqualTo(" Jeux familiaux ");
        verify(categoryRepository).save(existing);
    }

    @Test
    void deleteShouldRemoveCategoryWhenExists() {
        when(categoryRepository.existsById(8L)).thenReturn(true);

        categoryService.delete(8L);

        verify(categoryRepository).deleteById(8L);
    }

    @Test
    void deleteShouldValidateExistence() {
        when(categoryRepository.existsById(5L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoryService.delete(5L));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
