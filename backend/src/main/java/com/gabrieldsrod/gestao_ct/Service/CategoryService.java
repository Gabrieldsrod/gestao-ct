package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.DTO.request.NewCategoryDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.CategoryResponseDTO;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.BusinessRuleException;
import com.gabrieldsrod.gestao_ct.Model.Category;
import com.gabrieldsrod.gestao_ct.Repository.CategoryRepository;
import com.gabrieldsrod.gestao_ct.Repository.MemberPaymentRepository;
import com.gabrieldsrod.gestao_ct.Repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final MemberPaymentRepository pagamentoRepo;
    private final TransactionRepository transacaoRepo;
    private final CategoryRepository categoryRepo;

    public CategoryService(MemberPaymentRepository pagamentoRepo, TransactionRepository transacaoRepo, CategoryRepository categoriaRepo) {
        this.pagamentoRepo = pagamentoRepo;
        this.transacaoRepo = transacaoRepo;
        this.categoryRepo = categoriaRepo;
    }

    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = categoryRepo.findAll();
        return categories.stream()
                .map(CategoryResponseDTO::new)
                .toList();
    }

    @Transactional
    public CategoryResponseDTO createCategory(NewCategoryDTO data) {
        if (categoryRepo.findByName(data.name()).isPresent()) {
            throw new BusinessRuleException("Já existe uma categoria com esse nome.");
        }

        Category category = new Category();
        category.setName(data.name());
        category.setType(data.type());
        category = categoryRepo.save(category);
        return new CategoryResponseDTO(category);
    }
}
