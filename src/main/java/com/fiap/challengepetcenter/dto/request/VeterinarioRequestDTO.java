package com.fiap.challengepetcenter.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VeterinarioRequestDTO(

        @NotBlank(message = "CRMV é obrigatório")
        @Size(min = 4, max = 20, message = "O CRMV deve ter entre 4 e 20 caracteres")
        @Schema(
                description = "Número de registro do veterinário no CRMV",
                example = "12345-SP",
                required = true,
                minLength = 4,
                maxLength = 20
        )
        String crmv,

        @NotBlank(message = "Especialidade é obrigatória")
        @Size(min = 3, max = 100, message = "A especialidade deve ter entre 3 e 100 caracteres")
        @Schema(
                description = "Especialidade do veterinário",
                example = "Clínica Geral",
                required = true,
                minLength = 3,
                maxLength = 100
        )
        String especialidade,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(min = 10, max = 500, message = "A descrição deve ter entre 10 e 500 caracteres")
        @Schema(
                description = "Descrição profissional do veterinário",
                example = "Veterinário especializado em atendimento clínico de cães e gatos.",
                required = true,
                minLength = 10,
                maxLength = 500
        )
        String descricao


) {
}

