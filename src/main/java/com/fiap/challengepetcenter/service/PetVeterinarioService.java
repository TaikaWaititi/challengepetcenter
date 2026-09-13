package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.response.PetVeterinarioResponseDTO;
import com.fiap.challengepetcenter.exception.RecursoNaoEncontradoException;
import com.fiap.challengepetcenter.model.Pet;
import com.fiap.challengepetcenter.model.PetVeterinario;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.model.Veterinario;
import com.fiap.challengepetcenter.repository.PetRepository;
import com.fiap.challengepetcenter.repository.PetVeterinarioRepository;
import com.fiap.challengepetcenter.repository.UserRepository;
import com.fiap.challengepetcenter.repository.VeterinarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetVeterinarioService {

    private final PetVeterinarioRepository petVeterinarioRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;


    @Autowired
    public PetVeterinarioService(
            PetVeterinarioRepository petVeterinarioRepository,
            UserRepository userRepository,
            PetRepository petRepository,
            VeterinarioRepository veterinarioRepository) {

        this.petVeterinarioRepository = petVeterinarioRepository;
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    @Transactional(readOnly = true)
    public Page<PetVeterinarioResponseDTO> listarTodos(Pageable pageable) {
        return petVeterinarioRepository.findAll(pageable)
                .map(PetVeterinarioResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public PetVeterinarioResponseDTO buscarPorId(Long id) {

        User usuarioLogado = getUsuarioAutenticado();

        PetVeterinario petVeterinario = petVeterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Vínculo entre pet e veterinário não encontrado com ID: " + id
                ));

        boolean ehDonoDoPet =
                petVeterinario.getPet().getUser().getId()
                        .equals(usuarioLogado.getId());

        boolean ehVeterinario =
                petVeterinario.getVeterinario().getUser().getId()
                        .equals(usuarioLogado.getId());

        if (!ehDonoDoPet && !ehVeterinario) {
            throw new RecursoNaoEncontradoException(
                    "Você não pode acessar este vínculo"
            );
        }

        return PetVeterinarioResponseDTO.fromEntity(petVeterinario);
    }

    @Transactional(readOnly = true)
    public Page<PetVeterinarioResponseDTO> buscarPorVeterinarioId(Long veterinarioId, Pageable pageable) {


        User usuarioLogado = getUsuarioAutenticado();

        Veterinario veterinario = veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Veterinário não encontrado com ID: " + veterinarioId
                ));

        if (!veterinario.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException(
                    "Você não pode acessar os vínculos de outro veterinário"
            );
        }

        return petVeterinarioRepository.findByVeterinario_Id(veterinarioId, pageable)
                .map(PetVeterinarioResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<PetVeterinarioResponseDTO> buscarPorPetId(Long petId, Pageable pageable) {

        User usuarioLogado = getUsuarioAutenticado();

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Pet não encontrado com ID: " + petId
                ));

        if (!pet.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException(
                    "Você não pode acessar os vínculos de outro pet"
            );
        }
        return petVeterinarioRepository.findByPet_Id(petId, pageable)
                .map(PetVeterinarioResponseDTO::fromEntity);
    }

    @Transactional
    public void deletar(Long id) {

        User usuarioLogado = getUsuarioAutenticado();

        PetVeterinario petVeterinario = petVeterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Vínculo entre pet e veterinário não encontrado com ID: " + id
                ));

        if (!petVeterinario.getVeterinario().getUser().getId()
                .equals(usuarioLogado.getId())) {

            throw new RecursoNaoEncontradoException(
                    "Você não pode excluir o vínculo de outro veterinário"
            );
        }
        
        petVeterinarioRepository.deleteById(id);
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }


}
