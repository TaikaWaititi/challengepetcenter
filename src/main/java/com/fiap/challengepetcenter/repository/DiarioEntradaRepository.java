package com.fiap.challengepetcenter.repository;

import com.fiap.challengepetcenter.model.DiarioEntrada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DiarioEntradaRepository extends JpaRepository<DiarioEntrada, Long> {

    Page<DiarioEntrada> findByData(LocalDate data, Pageable pageable);

    boolean existsByPetId(Long id);
}
