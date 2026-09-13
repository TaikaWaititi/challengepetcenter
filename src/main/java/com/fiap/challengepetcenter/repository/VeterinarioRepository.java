package com.fiap.challengepetcenter.repository;

import com.fiap.challengepetcenter.model.Veterinario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    Page<Veterinario> findByUserId(Long userId, Pageable pageable);

    Optional<Veterinario> findByUserId(Long userId);

}
