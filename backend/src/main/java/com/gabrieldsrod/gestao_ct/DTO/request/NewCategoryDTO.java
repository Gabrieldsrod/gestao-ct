package com.gabrieldsrod.gestao_ct.DTO.request;

import com.gabrieldsrod.gestao_ct.Enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewCategoryDTO(
        @NotBlank(message = "O nome da categoria é obrigatório.")
        String name,

        @NotNull(message = "O tipo da categoria é obrigatório.")
        TransactionType type
) {
}
