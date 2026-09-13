package com.fiap.challengepetcenter.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(
        name = "SolicitacaoRequest",
        description = "Dados necessários para enviar uma solicitação de atendimento veterinário"
)
public record SolicitacaoRequestDTO(

        @NotNull(message = "ID do pet é obrigatório")
        @Schema(
                description = "ID do pet para o qual o atendimento está sendo solicitado",
                example = "1",
                required = true
        )
        Long petId,

        @NotNull(message = "ID do veterinário é obrigatório")
        @Schema(
                description = "ID do veterinário para o qual a solicitação será enviada",
                example = "1",
                required = true
        )
        Long veterinarioId,

        @Size(
                max = 500,
                message = "A mensagem deve ter no máximo 500 caracteres"
        )
        @Schema(
                description = "Mensagem enviada pelo tutor ao veterinário",
                example = "Meu pet precisa de acompanhamento para alergia.",
                maxLength = 500
        )
        String mensagem
) {
}