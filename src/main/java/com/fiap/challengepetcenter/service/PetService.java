package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.PetRequestDTO;
import com.fiap.challengepetcenter.dto.response.PetResponseDTO;
import com.fiap.challengepetcenter.exception.DiarioEntradaComDependenciasException;
import com.fiap.challengepetcenter.exception.RecursoNaoEncontradoException;
import com.fiap.challengepetcenter.model.Pet;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.repository.DiarioEntradaRepository;
import com.fiap.challengepetcenter.repository.PetRepository;
import com.fiap.challengepetcenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        User usuarioLogado = getUsuarioAutenticado();

        Pet pet = new Pet();
        pet.setUser(usuarioLogado);
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

        User usuarioLogado = getUsuarioAutenticado();

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado com ID: " + id));

        if (!pet.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException("Você não pode acessar um pet de outro usuário");
        }

        return PetResponseDTO.fromEntity(pet);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorUserId(Long userId, Pageable pageable) {

        User usuarioLogado = getUsuarioAutenticado();

        if (!userId.equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException("Você não pode acessar os pets de outro usuário");
        }

        return petRepository.findByUserId(userId, pageable)
                .map(PetResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> buscarPorNome(String nome, Pageable pageable) {

        User usuarioLogado = getUsuarioAutenticado();

        return petRepository.findByNomeContainingAndUserId(nome, usuarioLogado.getId(), pageable)
                .map(PetResponseDTO::fromEntity);
    }

    @Transactional
    public PetResponseDTO atualizar(Long id, PetRequestDTO requestDTO) {

        User usuarioLogado = getUsuarioAutenticado();

        Pet petExistente = petRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado com ID: " + id));

        if (!petExistente.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException("Você não pode atualizar um pet de outro usuário");
        }

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

        User usuarioLogado = getUsuarioAutenticado();

        Pet petExistente = petRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado com ID: " + id));

        if (!petExistente.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException("Você não pode excluir um pet de outro usuário");
        }

        if (diarioEntradaRepository.existsByPetId(id)) {
            throw new DiarioEntradaComDependenciasException("Não é possível excluir o pet pois existem entradas de diário vinculadas a ele");
        }

        petRepository.deleteById(id);

    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

}