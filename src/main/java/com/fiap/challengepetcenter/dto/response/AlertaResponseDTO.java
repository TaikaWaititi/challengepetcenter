package com.fiap.challengepetcenter.dto.response;

import com.fiap.challengepetcenter.model.Alerta;

import java.time.LocalDateTime;

public record AlertaResponseDTO(

        Long id,
        Long petId,
        String petNome,
        Long veterinarioId,
        String veterinarioNome,
        String tipo,
        String titulo,
        String descricao,
        LocalDateTime dataInicio,
        Integer frequenciaHoras,
        LocalDateTime dataFim,
        Boolean ativo
) {

    public static AlertaResponseDTO fromEntity(Alerta alerta) {
        return new AlertaResponseDTO(
                alerta.getId(),
                alerta.getPet().getId(),
                alerta.getPet().getNome(),
                alerta.getVeterinario().getId(),
                alerta.getVeterinario().getUser().getNome(),
                alerta.getTipo(),
                alerta.getTitulo(),
                alerta.getDescricao(),
                alerta.getDataInicio(),
                alerta.getFrequenciaHoras(),
                alerta.getDataFim(),
                alerta.getAtivo()
        );
    }
}

