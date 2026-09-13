package com.fiap.challengepetcenter.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PetRequestDTO(

        @Schema(
                description = "Nome do pet",
                example = "Luna"
        )
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100)
        String nome,

        @Schema(
                description = "Espécie do pet",
                example = "Cachorro"
        )
        @NotBlank(message = "Espécie é obrigatória")
        String especie,

        @Schema(
                description = "Raça do pet",
                example = "Golden Retriever"
        )
        String raca,

        @Schema(
                description = "Data de nascimento do pet",
                example = "2022-03-15"
        )
        LocalDate dataNascimento,

        @Schema(
                description = "Observações adicionais sobre o pet",
                example = "Pet alérgico a determinados alimentos"
        )
        String observacoes

) {
}
