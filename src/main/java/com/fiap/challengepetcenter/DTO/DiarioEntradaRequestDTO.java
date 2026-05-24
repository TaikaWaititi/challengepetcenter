package com.fiap.challengepetcenter.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DiarioEntradaRequestDTO(

        @Schema(
                description = "ID do pet associado à entrada do diário",
                example = "1"
        )
        @NotNull(message = "O pet é obrigatório")
        Long petId,

        @Schema(
                description = "Data referente à entrada do diário",
                example = "2026-05-17"
        )
        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        @Schema(
                description = "Resumo geral do dia do pet",
                example = "Pet se alimentou bem e apresentou comportamento tranquilo"
        )
        @Size(max = 1000, message = "O resumo deve ter no máximo 1000 caracteres")
        String resumo,

        @Schema(
                description = "Humor geral observado no pet",
                example = "Calmo"
        )
        @Size(max = 50, message = "O humor geral deve ter no máximo 50 caracteres")
        String humorGeral,

        @Schema(
                description = "Status atual da entrada do diário",
                example = "Concluído"
        )
        @NotBlank(message = "O status é obrigatório")
        @Size(max = 20, message = "O status deve ter no máximo 20 caracteres")
        String status

) {
}
