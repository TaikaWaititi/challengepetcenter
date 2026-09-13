package com.fiap.challengepetcenter.dto.response;

import com.fiap.challengepetcenter.model.PetVeterinario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(
        name = "PetVeterinarioResponse",
        description = "Dados do vínculo entre um pet e um veterinário"
)
public record PetVeterinarioResponseDTO(

        @Schema(
                description = "Identificador único do vínculo",
                example = "1"
        )
        Long id,

        @Schema(
                description = "ID do pet",
                example = "1"
        )
        Long petId,

        @Schema(
                description = "Nome do pet",
                example = "Bolt"
        )
        String petNome,

        @Schema(
                description = "ID do veterinário",
                example = "2"
        )
        Long veterinarioId,

        @Schema(
                description = "Nome do veterinário",
                example = "Dr. João Silva"
        )
        String veterinarioNome,

        @Schema(
                description = "Data de início do vínculo",
                example = "2026-09-05"
        )
        LocalDate dataInicio,

        @Schema(
                description = "Indica se o vínculo está ativo",
                example = "true"
        )
        Boolean ativo,

        @Schema(
                description = "Observações sobre o vínculo",
                example = "Pet apresenta alergia alimentar."
        )
        String observacoes

) {

    public static PetVeterinarioResponseDTO fromEntity(PetVeterinario petVeterinario) {
        return new PetVeterinarioResponseDTO(
                petVeterinario.getId(),
                petVeterinario.getPet().getId(),
                petVeterinario.getPet().getNome(),
                petVeterinario.getVeterinario().getId(),
                petVeterinario.getVeterinario().getUser().getNome(),
                petVeterinario.getDataInicio(),
                petVeterinario.getAtivo(),
                petVeterinario.getObservacoes()
        );
    }
}