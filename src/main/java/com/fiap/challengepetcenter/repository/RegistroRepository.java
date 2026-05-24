package com.fiap.challengepetcenter.repository;

import com.fiap.challengepetcenter.model.Registro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroRepository extends JpaRepository<Registro, Long> {
    boolean existsById(Long id);
}
