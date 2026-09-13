package com.fiap.challengepetcenter.repository;

import com.fiap.challengepetcenter.model.Alerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    Page<Alerta> findByPetId(Long petId, Pageable pageable);

}
