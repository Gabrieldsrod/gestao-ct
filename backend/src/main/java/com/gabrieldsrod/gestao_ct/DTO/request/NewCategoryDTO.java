package com.gabrieldsrod.gestao_ct.DTO.request;

import com.gabrieldsrod.gestao_ct.Enums.TransactionType;

public record NewCategoryDTO(
        String name,
        TransactionType type
) {
}
