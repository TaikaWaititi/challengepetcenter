package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.AlertaRequestDTO;
import com.fiap.challengepetcenter.dto.response.AlertaResponseDTO;
import com.fiap.challengepetcenter.model.Alerta;
import com.fiap.challengepetcenter.model.Pet;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.model.Veterinario;
import com.fiap.challengepetcenter.repository.AlertaRepository;
import com.fiap.challengepetcenter.repository.PetRepository;
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
public class AlertaService {

    private final PetRepository petRepository;
    private final AlertaRepository alertaRepository;
    private final UserRepository userRepository;
    private final VeterinarioRepository veterinarioRepository;

    @Autowired
    public AlertaService(
            PetRepository petRepository,
            AlertaRepository alertaRepository,
            UserRepository userRepository,
            VeterinarioRepository veterinarioRepository
    ) {
        this.petRepository = petRepository;
        this.alertaRepository = alertaRepository;
        this.userRepository = userRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    @Transactional
    public AlertaResponseDTO salvar(AlertaRequestDTO requestDTO) {

        User usuarioLogado = getUsuarioAutenticado();

        Veterinario veterinario = veterinarioRepository
                .findByUserId(usuarioLogado.getId())
                .orElseThrow(() -> new RuntimeException("Veterinário não encontrado"));

        Pet pet = petRepository.findById(requestDTO.petId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Pet não encontrado com ID: " + requestDTO.petId()
                        )
                );

        Alerta alerta = new Alerta();
        alerta.setPet(pet);
        alerta.setVeterinario(veterinario);
        alerta.setTipo(requestDTO.tipo());
        alerta.setTitulo(requestDTO.titulo());
        alerta.setDescricao(requestDTO.descricao());
        alerta.setDataInicio(requestDTO.dataInicio());
        alerta.setFrequenciaHoras(requestDTO.frequenciaHoras());
        alerta.setDataFim(requestDTO.dataFim());
        alerta.setAtivo(true);

        Alerta alertaSalvo = alertaRepository.save(alerta);

        return AlertaResponseDTO.fromEntity(alertaSalvo);
    }

    @Transactional(readOnly = true)
    public Page<AlertaResponseDTO> listarTodos(Pageable pageable) {
        return alertaRepository.findAll(pageable)
                .map(AlertaResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public AlertaResponseDTO buscarPorId(Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado com ID: " + id));
        return AlertaResponseDTO.fromEntity(alerta);
    }

    @Transactional(readOnly = true)
    public Page<AlertaResponseDTO> listarPorPet(Long petId, Pageable pageable) {
        if (!petRepository.existsById(petId)) {
            throw new RuntimeException("Pet não encontrado com ID: " + petId);
        }

        return alertaRepository.findByPetId(petId, pageable)
                .map(AlertaResponseDTO::fromEntity);
    }

    @Transactional
    public AlertaResponseDTO desativar(Long id) {

        User usuarioLogado = getUsuarioAutenticado();

        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Alerta não encontrado com ID: " + id)
                );

        boolean ehTutorDoPet =
                alerta.getPet().getUser().getId()
                        .equals(usuarioLogado.getId());

        boolean ehVeterinarioDoAlerta =
                alerta.getVeterinario().getUser().getId()
                        .equals(usuarioLogado.getId());

        if (!ehTutorDoPet && !ehVeterinarioDoAlerta) {
            throw new RuntimeException(
                    "Você não tem permissão para desativar este alerta"
            );
        }

        alerta.setAtivo(false);

        return AlertaResponseDTO.fromEntity(
                alertaRepository.save(alerta)
        );
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado")
                );
    }
}
