package com.fiap.challengepetcenter.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(
        name = "AlertaRequest",
        description = "Dados necessários para cadastrar um alerta"
)
public record AlertaRequestDTO(

        @NotNull(message = "ID do pet é obrigatório")
        @Schema(
                description = "ID do pet relacionado ao alerta",
                example = "1",
                required = true
        )
        Long petId,

        @NotBlank(message = "O tipo é obrigatório")
        @Size(max = 20, message = "O tipo deve ter no máximo 20 caracteres")
        @Schema(
                description = "Tipo do alerta",
                example = "REMEDIO",
                required = true
        )
        String tipo,

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        @Schema(
                description = "Título do alerta",
                example = "Dar remédio",
                required = true
        )
        String titulo,

        @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
        @Schema(
                description = "Descrição do alerta",
                example = "Administrar o medicamento conforme orientação veterinária."
        )
        String descricao,

        @NotNull(message = "A data de início é obrigatória")
        @Schema(
                description = "Data e hora de início do alerta",
                example = "2026-09-10T08:00:00",
                required = true
        )
        LocalDateTime dataInicio,

        @Positive(message = "A frequência deve ser maior que zero")
        @Schema(
                description = "Frequência do alerta em horas",
                example = "12"
        )
        Integer frequenciaHoras,

        @Schema(
                description = "Data e hora de término do alerta",
                example = "2026-09-20T08:00:00"
        )
        LocalDateTime dataFim
) {
}