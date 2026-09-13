package com.fiap.challengepetcenter.dto.response;

import com.fiap.challengepetcenter.model.DiarioEntrada;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DiarioEntradaResponseDTO(
        Long id,
        Long idPet,
        String nomePet,
        LocalDate data,
        String resumo,
        String humorGeral,
        String status,

        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {

    public static DiarioEntradaResponseDTO fromEntity(DiarioEntrada diarioEntrada) {
        return new DiarioEntradaResponseDTO(
                diarioEntrada.getId(),
                diarioEntrada.getPet().getId(),
                diarioEntrada.getPet().getNome(),
                diarioEntrada.getData(),
                diarioEntrada.getResumo(),
                diarioEntrada.getHumorGeral(),
                diarioEntrada.getStatus(),

                diarioEntrada.getCriadoEm(),
                diarioEntrada.getAtualizadoEm()
        );
    }
}