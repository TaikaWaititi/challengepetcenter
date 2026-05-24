package com.fiap.challengepetcenter.repository;

import com.fiap.challengepetcenter.model.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Page<Pet> findByNomeContaining(String nome, Pageable pageable);

    Page<Pet> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserId(Long id);
}
