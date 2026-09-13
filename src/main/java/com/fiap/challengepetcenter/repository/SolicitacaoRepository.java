package com.fiap.challengepetcenter.repository;

import com.fiap.challengepetcenter.model.Solicitacao;
import com.fiap.challengepetcenter.model.StatusSolicitacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    Page<Solicitacao> findByVeterinario_IdAndStatus(
            Long veterinarioId,
            StatusSolicitacao status,
            Pageable pageable
    );

    Page<Solicitacao> findByPetId(
            Long petId,
            Pageable pageable
    );

    Page<Solicitacao> findByVeterinarioId(
            Long veterinarioId,
            Pageable pageable
    );

    Page<Solicitacao> findByUserId(
            Long userId,
            Pageable pageable
    );
}