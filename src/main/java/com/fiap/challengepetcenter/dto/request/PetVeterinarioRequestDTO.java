package com.fiap.challengepetcenter.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(
        name = "PetVeterinarioRequest",
        description = "Dados necessários para solicitar o vínculo entre um pet e um veterinário"
)
public record PetVeterinarioRequestDTO(

        @NotNull(message = "ID do pet é obrigatório")
        @Schema(
                description = "ID do pet que deseja ser atendido pelo veterinário",
                example = "1",
                required = true
        )
        Long petId,

        @NotNull(message = "ID do veterinário é obrigatório")
        @Schema(
                description = "ID do veterinário ao qual o tutor deseja solicitar o vínculo",
                example = "2",
                required = true
        )
        Long veterinarioId,

        @Size(
                max = 500,
                message = "As observações devem ter no máximo 500 caracteres"
        )
        @Schema(
                description = "Observações ou informações adicionais sobre a solicitação",
                example = "Pet apresenta alergia alimentar e precisa de acompanhamento.",
                maxLength = 500
        )
        String observacoes
) {
}