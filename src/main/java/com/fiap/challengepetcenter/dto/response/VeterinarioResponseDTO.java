package com.fiap.challengepetcenter.dto.response;

import com.fiap.challengepetcenter.model.Veterinario;

public record VeterinarioResponseDTO(

        Long id,
        String nomeVeterinario,
        String crmv,
        String especialidade,
        String descricao

) {

    public static VeterinarioResponseDTO fromEntity(Veterinario veterinario) {
        return new VeterinarioResponseDTO(
                veterinario.getId(),
                veterinario.getUser().getNome(),
                veterinario.getCrmv(),
                veterinario.getEspecialidade(),
                veterinario.getDescricao()
        );
    }
}