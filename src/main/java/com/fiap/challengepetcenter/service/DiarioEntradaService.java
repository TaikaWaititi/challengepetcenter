package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.DTO.DiarioEntradaRequestDTO;
import com.fiap.challengepetcenter.DTO.DiarioEntradaResponseDTO;
import com.fiap.challengepetcenter.exception.DiarioEntradaNaoEncontradoException;
import com.fiap.challengepetcenter.exception.PetNaoEncontradoException;
import com.fiap.challengepetcenter.exception.RegistroComDependenciasException;
import com.fiap.challengepetcenter.model.DiarioEntrada;
import com.fiap.challengepetcenter.model.Pet;
import com.fiap.challengepetcenter.repository.DiarioEntradaRepository;
import com.fiap.challengepetcenter.repository.PetRepository;
import com.fiap.challengepetcenter.repository.RegistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class DiarioEntradaService {

    private final DiarioEntradaRepository diarioEntradaRepository;
    private final PetRepository petRepository;
    private final RegistroRepository registroRepository;

    @Autowired
    public DiarioEntradaService(DiarioEntradaRepository diarioEntradaRepository, PetRepository petRepository, RegistroRepository registroRepository) {
        this.diarioEntradaRepository = diarioEntradaRepository;
        this.petRepository = petRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional
    public DiarioEntradaResponseDTO salvar(DiarioEntradaRequestDTO requestDTO) {
        Pet pet = petRepository.findById(requestDTO.petId())
                .orElseThrow(() -> new PetNaoEncontradoException("Pet não encontrado com ID: " + requestDTO.petId()));

        DiarioEntrada diarioEntrada = new DiarioEntrada();
        diarioEntrada.setPet(pet);
        diarioEntrada.setData(requestDTO.data());
        diarioEntrada.setResumo(requestDTO.resumo());
        diarioEntrada.setHumorGeral(requestDTO.humorGeral());
        diarioEntrada.setStatus(requestDTO.status());

        DiarioEntrada diarioEntradaSalvo = diarioEntradaRepository.save(diarioEntrada);

        return DiarioEntradaResponseDTO.fromEntity(diarioEntradaSalvo);
    }


    @Transactional(readOnly = true)
    public Page<DiarioEntradaResponseDTO> listarTodos(Pageable pageable) {
        Page<DiarioEntrada> entradas = diarioEntradaRepository.findAll(pageable);

        return entradas.map(DiarioEntradaResponseDTO::fromEntity);

    }

    @Transactional(readOnly = true)
    public DiarioEntradaResponseDTO buscarPorId(Long id) {
        DiarioEntrada diarioEntrada = diarioEntradaRepository.findById(id)
                .orElseThrow(() -> new DiarioEntradaNaoEncontradoException("DiarioEntrada não encontrado com ID: " + id));
        return DiarioEntradaResponseDTO.fromEntity(diarioEntrada);
    }

    @Transactional(readOnly = true)
    public Page<DiarioEntradaResponseDTO> buscarPorData(LocalDate data, Pageable pageable) {
        Page<DiarioEntrada> entradas = diarioEntradaRepository.findByData(data, pageable);

        return entradas.map(DiarioEntradaResponseDTO::fromEntity);
    }

    @Transactional
    public DiarioEntradaResponseDTO atualizar(Long id, DiarioEntradaRequestDTO requestDTO) {
        DiarioEntrada diarioEntradaExistente = diarioEntradaRepository.findById(id)
                .orElseThrow(() -> new DiarioEntradaNaoEncontradoException("DiarioEntrada não encontrado com ID: " + id));

        Pet pet = petRepository.findById(requestDTO.petId())
                .orElseThrow(() -> new PetNaoEncontradoException("Pet não encontrado com ID: " + requestDTO.petId()));

        diarioEntradaExistente.setPet(pet);
        diarioEntradaExistente.setData(requestDTO.data());
        //diarioEntradaExistente.setAtualizadoEm(LocalDateTime.now());
        diarioEntradaExistente.setResumo(requestDTO.resumo());
        diarioEntradaExistente.setHumorGeral(requestDTO.humorGeral());
        diarioEntradaExistente.setStatus(requestDTO.status());

        DiarioEntrada diarioEntradaAtualizado = diarioEntradaRepository.save(diarioEntradaExistente);

        return DiarioEntradaResponseDTO.fromEntity(diarioEntradaAtualizado);

    }

    @Transactional
    public void deletar(Long id) {
        if (!diarioEntradaRepository.existsById(id)) {
            throw new DiarioEntradaNaoEncontradoException("DiarioEntrada não encontrado com ID: " + id);
        }

        if (registroRepository.existsById(id)) {
            throw new RegistroComDependenciasException("Não é possível excluir o pet pois existem DiarioEntradas vinculados a ele");
        }

        diarioEntradaRepository.deleteById(id);
    }
}

