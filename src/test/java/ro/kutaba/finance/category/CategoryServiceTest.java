package ro.kutaba.finance.category;

import ro.kutaba.finance.exception.CategoryNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldUpdateCategory() {

        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Food");

        CreateCategoryRequest request =
                new CreateCategoryRequest("Groceries");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CategoryResponse response =
                categoryService.update(1L, request);

        // Assert
        assertEquals(1L, response.id());
        assertEquals("Groceries", response.name());

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(category);
    }

    @Test
    void shouldThrowWhenCategoryNotFoundOnUpdate() {

        // Arrange
        CreateCategoryRequest request =
                new CreateCategoryRequest("Groceries");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.update(1L, request)
        );

        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldDeleteCategory() {

        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Food");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        // Act
        categoryService.delete(1L);

        // Assert
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).delete(category);
    }

    @Test
    void shouldThrowWhenCategoryNotFoundOnDelete() {

        // Arrange
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.delete(1L)
        );

        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never()).delete(any());
    }

}