package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.Category;

public record CategoryResponseDTO(
        Long id,
        String name,
        String type
) {

    public CategoryResponseDTO(Category category) {
        this(category.getId(), category.getName(), category.getType().name());
    }
}
