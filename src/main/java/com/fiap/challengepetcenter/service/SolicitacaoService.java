package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.SolicitacaoRequestDTO;
import com.fiap.challengepetcenter.dto.response.SolicitacaoResponseDTO;
import com.fiap.challengepetcenter.exception.RecursoNaoEncontradoException;
import com.fiap.challengepetcenter.model.*;
import com.fiap.challengepetcenter.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final UserRepository userRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final PetRepository petRepository;
    private final PetVeterinarioRepository petVeterinarioRepository;

    @Autowired
    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository, UserRepository userRepository, VeterinarioRepository veterinarioRepository, PetRepository petRepository, PetVeterinarioRepository petVeterinarioRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.userRepository = userRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.petRepository = petRepository;
        this.petVeterinarioRepository = petVeterinarioRepository;
    }

    @Transactional
    public SolicitacaoResponseDTO salvar(SolicitacaoRequestDTO requestDTO) {
        Veterinario veterinario = veterinarioRepository.findById(requestDTO.veterinarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado com ID: " + requestDTO.veterinarioId()));

        Pet pet = petRepository.findById(requestDTO.petId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado com ID: " + requestDTO.petId()));

        User usuarioLogado = getUsuarioAutenticado();

        if (!pet.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException(
                    "Você não pode criar uma solicitação para o pet de outro usuário"
            );
        }

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setPet(pet);
        solicitacao.setUser(usuarioLogado);
        solicitacao.setVeterinario(veterinario);
        solicitacao.setMensagem(requestDTO.mensagem());
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setCriadoEm(LocalDateTime.now());

        Solicitacao solicitacaoSalva = solicitacaoRepository.save(solicitacao);

        return SolicitacaoResponseDTO.fromEntity(solicitacaoSalva);
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoResponseDTO> listarTodos(Pageable pageable) {
        Page<Solicitacao> solicitacoes = solicitacaoRepository.findAll(pageable);

        return solicitacoes.map(SolicitacaoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public SolicitacaoResponseDTO buscarPorId(Long id) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada com ID: " + id));
        return SolicitacaoResponseDTO.fromEntity(solicitacao);
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoResponseDTO> buscarPorVeterinarioId(Long veterinarioId, Pageable pageable) {

        User usuarioLogado = getUsuarioAutenticado();

        Veterinario veterinario = veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Veterinário não encontrado com ID: " + veterinarioId
                ));

        if (!veterinario.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException(
                    "Você não pode acessar solicitações de outro veterinário"
            );
        }

        return solicitacaoRepository.findByVeterinarioId(veterinarioId, pageable)
                .map(SolicitacaoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoResponseDTO> buscarPorPetId(Long petId, Pageable pageable) {

        User usuarioLogado = getUsuarioAutenticado();

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Pet não encontrado com ID: " + petId
                ));

        if (!pet.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException(
                    "Você não pode acessar solicitações de outro pet"
            );
        }

        return solicitacaoRepository.findByPetId(petId, pageable)
                .map(SolicitacaoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoResponseDTO> buscarPorUserId(Long userId, Pageable pageable) {

        User usuarioLogado = getUsuarioAutenticado();

        if (!userId.equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException(
                    "Você não pode acessar solicitações de outro usuário"
            );
        }

        return solicitacaoRepository.findByUserId(userId, pageable)
                .map(SolicitacaoResponseDTO::fromEntity);
    }

    @Transactional
    public SolicitacaoResponseDTO aceitar(Long id) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada com ID: " + id));

        User usuarioLogado = getUsuarioAutenticado();

        if (!solicitacao.getVeterinario().getUser().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException(
                    "Você não pode responder a uma solicitação destinada a outro veterinário"
            );
        }

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new RecursoNaoEncontradoException("A solicitação já foi respondida");
        }

        solicitacao.setStatus(StatusSolicitacao.ACEITA);
        solicitacao.setRespondidoEm(LocalDateTime.now());

        PetVeterinario petVeterinario = new PetVeterinario();
        petVeterinario.setPet(solicitacao.getPet());
        petVeterinario.setVeterinario(solicitacao.getVeterinario());
        petVeterinario.setDataInicio(LocalDate.now());
        petVeterinario.setAtivo(true);
        petVeterinario.setObservacoes(solicitacao.getMensagem());

        petVeterinarioRepository.save(petVeterinario);

        Solicitacao solicitacaoSalva = solicitacaoRepository.save(solicitacao);

        return SolicitacaoResponseDTO.fromEntity(solicitacaoSalva);
    }

    @Transactional
    public SolicitacaoResponseDTO recusar(Long id) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada com ID: " + id));

        User usuarioLogado = getUsuarioAutenticado();

        if (!solicitacao.getVeterinario().getUser().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException(
                    "Você não pode responder a uma solicitação destinada a outro veterinário"
            );
        }

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new RecursoNaoEncontradoException("A solicitação já foi respondida");
        }

        solicitacao.setStatus(StatusSolicitacao.RECUSADA);
        solicitacao.setRespondidoEm(LocalDateTime.now());

        Solicitacao solicitacaoSalva = solicitacaoRepository.save(solicitacao);

        return SolicitacaoResponseDTO.fromEntity(solicitacaoSalva);
    }

    @Transactional
    public void deletar(Long id) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada com ID: " + id));

        User usuarioLogado = getUsuarioAutenticado();

        if (!solicitacao.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException(
                    "Você não pode excluir a solicitação de outro usuário"
            );
        }

        if (solicitacao.getStatus() == StatusSolicitacao.ACEITA) {
            throw new RecursoNaoEncontradoException("Não é possível excluir uma solicitação que já foi aceita");
        }

        solicitacaoRepository.delete(solicitacao);
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

}
