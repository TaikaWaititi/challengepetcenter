package com.fiap.challengepetcenter.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistroRequestDTO(

        @Schema(
                description = "ID do diário de entrada associado ao registro",
                example = "1"
        )
        @NotNull(message = "A entrada é obrigatória")
        Long entradaId,

        @Schema(
                description = "Tipo do registro",
                example = "Alimentação"
        )
        @NotBlank(message = "O tipo é obrigatório")
        String tipo,

        @Schema(
                description = "Subtipo do registro",
                example = "Ração seca"
        )
        String subtipo,

        @Schema(
                description = "Valor numérico relacionado ao registro",
                example = "250.0"
        )
        Double valor,

        @Schema(
                description = "Unidade de medida do valor",
                example = "gramas"
        )
        String unidade,

        @Schema(
                description = "Observações adicionais do registro",
                example = "Pet comeu normalmente"
        )
        String nota

) {
}