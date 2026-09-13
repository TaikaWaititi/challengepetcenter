package com.fiap.challengepetcenter.repository;

import com.fiap.challengepetcenter.model.PetVeterinario;
import com.fiap.challengepetcenter.model.StatusSolicitacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetVeterinarioRepository extends JpaRepository<PetVeterinario, Long> {

    Optional<PetVeterinario> findByPet_IdAndVeterinario_Id(
            Long petId,
            Long veterinarioId
    );

    Page<PetVeterinario> findByVeterinario_Id(
            Long veterinarioId,
            Pageable pageable
    );

    Page<PetVeterinario> findByPet_Id(
            Long petId,
            Pageable pageable
    );

    boolean existsByVeterinario_IdAndAtivo(
            Long veterinarioId,
            Boolean ativo
    );
}