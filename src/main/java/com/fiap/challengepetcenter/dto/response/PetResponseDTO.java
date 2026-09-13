package com.fiap.challengepetcenter.dto.response;

import com.fiap.challengepetcenter.model.Pet;

import java.time.LocalDate;

public record PetResponseDTO(

        Long id,
        String nomeTutor,
        String nome,
        String especie,
        String raca,
        LocalDate dataNascimento,
        String observacoes
) {

    public static PetResponseDTO fromEntity(Pet pet) {
        return new PetResponseDTO(
                pet.getId(),
                pet.getUser().getNome(),
                pet.getNome(),
                pet.getEspecie(),
                pet.getRaca(),
                pet.getDataNascimento(),
                pet.getObservacoes()
        );
    }
}