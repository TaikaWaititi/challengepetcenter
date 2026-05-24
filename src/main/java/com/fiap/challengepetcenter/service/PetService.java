package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.DTO.PetRequestDTO;
import com.fiap.challengepetcenter.DTO.PetResponseDTO;
import com.fiap.challengepetcenter.exception.DiarioEntradaComDependenciasException;
import com.fiap.challengepetcenter.exception.PetNaoEncontradoException;
import com.fiap.challengepetcenter.exception.UserNaoEncontradoException;
import com.fiap.challengepetcenter.model.Pet;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.repository.DiarioEntradaRepository;
import com.fiap.challengepetcenter.repository.PetRepository;
import com.fiap.challengepetcenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final DiarioEntradaRepository diarioEntradaRepository;

    @Autowired
    public PetService(PetRepository petRepository, UserRepository userRepository, DiarioEntradaRepository diarioEntradaRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.diarioEntradaRepository = diarioEntradaRepository;
    }

    @Transactional
    public PetResponseDTO salvar(PetRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.userId())
                .orElseThrow(() -> new UserNaoEncontradoException("Usuário não encontrado com ID: " + requestDTO.userId()));

        Pet pet = new Pet();
        pet.setUser(user);
        pet.setNome(requestDTO.nome());
        pet.setEspecie(requestDTO.especie());
        pet.setRaca(requestDTO.raca());
        pet.setDataNascimento(requestDTO.dataNascimento());
        pet.setObservacoes(requestDTO.observacoes());

        Pet petSalvo = petRepository.save(pet);

        return PetResponseDTO.fromEntity(petSalvo);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> listarTodos(Pageable pageable) {
        return petRepository.findAll(pageable)
                .map(PetResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public PetResponseDTO buscarPorId(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNaoEncontradoException("Pet não encontrado com ID: " + id));
        return PetResponseDTO.fromEntity(pet);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorUserId(Long userId, Pageable pageable) {

        return petRepository.findByUserId(userId, pageable)
                .map(PetResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorNome(String nome, Pageable pageable) {

        return petRepository.findByNomeContaining(nome, pageable)
                .map(PetResponseDTO::fromEntity);
    }

    @Transactional
    public PetResponseDTO atualizar(Long id, PetRequestDTO requestDTO) {
        Pet petExistente = petRepository.findById(id)
                .orElseThrow(() -> new PetNaoEncontradoException("Pet não encontrado com ID: " + id));

        User user = userRepository.findById(requestDTO.userId())
                .orElseThrow(() -> new UserNaoEncontradoException("Usuário não encontrado com ID: " + requestDTO.userId()));

        petExistente.setUser(user);
        petExistente.setNome(requestDTO.nome());
        petExistente.setEspecie(requestDTO.especie());
        petExistente.setRaca(requestDTO.raca());
        petExistente.setDataNascimento(requestDTO.dataNascimento());
        petExistente.setObservacoes(requestDTO.observacoes());

        Pet petAtualizado = petRepository.save(petExistente);

        return PetResponseDTO.fromEntity(petAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if (!petRepository.existsById(id)) {
            throw new PetNaoEncontradoException("Pet não encontrado com ID: " + id);
        }

        if (diarioEntradaRepository.existsByPetId(id)) {
            throw new DiarioEntradaComDependenciasException("Não é possível excluir o pet pois existem entradas de diário vinculadas a ele");
        }
        petRepository.deleteById(id);

    }
}