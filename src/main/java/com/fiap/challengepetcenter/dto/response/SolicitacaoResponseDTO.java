package com.fiap.challengepetcenter.dto.response;

import com.fiap.challengepetcenter.model.Solicitacao;
import com.fiap.challengepetcenter.model.StatusSolicitacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "SolicitacaoResponse",
        description = "Dados da solicitação de atendimento veterinário"
)
public record SolicitacaoResponseDTO(

        @Schema(
                description = "Identificador único da solicitação",
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
                description = "ID do tutor responsável pela solicitação",
                example = "5"
        )
        Long userId,

        @Schema(
                description = "Nome do tutor",
                example = "Cleber"
        )
        String userNome,

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
                description = "Status atual da solicitação",
                example = "PENDENTE"
        )
        StatusSolicitacao status,

        @Schema(
                description = "Mensagem enviada pelo tutor",
                example = "Meu pet precisa de acompanhamento para alergia."
        )
        String mensagem,

        @Schema(
                description = "Data e hora de criação da solicitação",
                example = "2026-09-05T15:30:00"
        )
        LocalDateTime criadoEm,

        @Schema(
                description = "Data e hora da resposta do veterinário",
                example = "2026-09-05T16:00:00"
        )
        LocalDateTime respondidoEm
) {

    public static SolicitacaoResponseDTO fromEntity(Solicitacao entity) {
        return new SolicitacaoResponseDTO(
                entity.getId(),
                entity.getPet().getId(),
                entity.getPet().getNome(),
                entity.getUser().getId(),
                entity.getUser().getNome(),
                entity.getVeterinario().getId(),
                entity.getVeterinario().getUser().getNome(),
                entity.getStatus(),
                entity.getMensagem(),
                entity.getCriadoEm(),
                entity.getRespondidoEm()
        );
    }
}