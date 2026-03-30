package com.gabrieldsrod.gestao_ct.service;

import com.gabrieldsrod.gestao_ct.DTO.request.NewCategoryDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.CategoryResponseDTO;
import com.gabrieldsrod.gestao_ct.Enums.TransactionType;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.BusinessRuleException;
import com.gabrieldsrod.gestao_ct.Model.Category;
import com.gabrieldsrod.gestao_ct.Repository.CategoryRepository;
import com.gabrieldsrod.gestao_ct.Service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("Deve retornar todas as categorias")
    void shouldGetAllCategories() {
        // Arrange
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Aluguel");
        category1.setType(TransactionType.EXPENSE);

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Mensalidade");
        category2.setType(TransactionType.INCOME);

        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));

        // Act
        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Aluguel", result.get(0).name());
        assertEquals("Mensalidade", result.get(1).name());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve criar uma nova categoria com sucesso")
    void shouldCreateCategorySuccessfully() {
        // Arrange
        NewCategoryDTO newCategoryDTO = new NewCategoryDTO("Material", TransactionType.EXPENSE);
        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Material");
        savedCategory.setType(TransactionType.EXPENSE);

        when(categoryRepository.findByName("Material")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // Act
        CategoryResponseDTO result = categoryService.createCategory(newCategoryDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Material", result.name());
        assertEquals(TransactionType.EXPENSE.name(), result.type());
        verify(categoryRepository, times(1)).findByName("Material");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar categoria com nome duplicado")
    void shouldThrowExceptionWhenCreatingDuplicateCategoryName() {
        // Arrange
        NewCategoryDTO newCategoryDTO = new NewCategoryDTO("Material", TransactionType.EXPENSE);
        Category existingCategory = new Category();
        existingCategory.setName("Material");

        when(categoryRepository.findByName("Material")).thenReturn(Optional.of(existingCategory));

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> categoryService.createCategory(newCategoryDTO));

        assertEquals("Já existe uma categoria com esse nome.", exception.getMessage());
        verify(categoryRepository, times(1)).findByName("Material");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar uma categoria pelo ID")
    void shouldGetCategoryById() {
        // Arrange
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Aluguel");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.getCategoryById(categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar categoria com ID inexistente")
    void shouldThrowExceptionWhenGettingNonExistentCategoryById() {
        // Arrange
        Long categoryId = 99L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> categoryService.getCategoryById(categoryId));

        assertEquals("Categoria não encontrada.", exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("Deve retornar uma categoria pelo nome")
    void shouldGetCategoryByName() {
        // Arrange
        String categoryName = "Mensalidade";
        Category category = new Category();
        category.setId(1L);
        category.setName(categoryName);

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.getCategoryByName(categoryName);

        // Assert
        assertNotNull(result);
        assertEquals(categoryName, result.getName());
        verify(categoryRepository, times(1)).findByName(categoryName);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar categoria com nome inexistente")
    void shouldThrowExceptionWhenGettingNonExistentCategoryByName() {
        // Arrange
        String categoryName = "Categoria Inexistente";
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> categoryService.getCategoryByName(categoryName));

        assertEquals("Categoria " + categoryName + " não encontrada.", exception.getMessage());
        verify(categoryRepository, times(1)).findByName(categoryName);
    }
}
