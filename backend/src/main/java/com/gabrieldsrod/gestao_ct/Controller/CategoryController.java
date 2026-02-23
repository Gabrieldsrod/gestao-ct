package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.NewCategoryDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.CategoryResponseDTO;
import com.gabrieldsrod.gestao_ct.Model.Category;
import com.gabrieldsrod.gestao_ct.Repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryRepository categoryRepo;

    public CategoryController(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<Category> categories = categoryRepo.findAll();
        List<CategoryResponseDTO> response = categories.stream()
                .map(CategoryResponseDTO::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody NewCategoryDTO data) {
        if (data.name() == null || data.name().isBlank()) {
            return ResponseEntity.badRequest().body("O nome da categoria é obrigatório.");
        }
        if (data.type() == null) {
            return ResponseEntity.badRequest().body("O tipo da categoria é obrigatório.");
        }

        if (categoryRepo.findByName(data.name()).isPresent()) {
            return ResponseEntity.badRequest().body("Categoria já existente!");
        }

        Category category = new Category();
        category.setName(data.name());
        category.setType(data.type());
        categoryRepo.save(category);

        return ResponseEntity.ok(new CategoryResponseDTO(category));
    }

}
