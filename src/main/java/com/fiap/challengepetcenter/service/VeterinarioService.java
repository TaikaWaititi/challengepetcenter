package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.VeterinarioRequestDTO;
import com.fiap.challengepetcenter.dto.response.VeterinarioResponseDTO;
import com.fiap.challengepetcenter.exception.DiarioEntradaComDependenciasException;
import com.fiap.challengepetcenter.exception.RecursoNaoEncontradoException;
import com.fiap.challengepetcenter.model.TipoUsuario;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.model.Veterinario;
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
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final UserRepository userRepository;
    private final PetVeterinarioRepository petVeterinarioRepository;


    @Autowired
    public VeterinarioService(VeterinarioRepository veterinarioRepository, UserRepository userRepository, PetVeterinarioRepository petVeterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
        this.userRepository = userRepository;
        this.petVeterinarioRepository = petVeterinarioRepository;
    }

    @Transactional
    public VeterinarioResponseDTO salvar(VeterinarioRequestDTO requestDTO) {

        User usuarioLogado = getUsuarioAutenticado();

        if (usuarioLogado.getTipoUsuario() != TipoUsuario.VETERINARIO) {
            throw new RecursoNaoEncontradoException("O usuário informado não possui perfil de veterinário");
        }

        Veterinario veterinario = new Veterinario();
        veterinario.setUser(usuarioLogado);
        veterinario.setCrmv(requestDTO.crmv());
        veterinario.setEspecialidade(requestDTO.especialidade());
        veterinario.setDescricao(requestDTO.descricao());

        Veterinario veterinarioSalvo = veterinarioRepository.save(veterinario);

        return VeterinarioResponseDTO.fromEntity(veterinarioSalvo);
    }

    @Transactional(readOnly = true)
    public Page<VeterinarioResponseDTO> listarTodos(Pageable pageable) {
        return veterinarioRepository.findAll(pageable)
                .map(VeterinarioResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public VeterinarioResponseDTO buscarPorId(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinario não encontrado com ID: " + id));
        return VeterinarioResponseDTO.fromEntity(veterinario);
    }

    @Transactional(readOnly = true)
    public Page<VeterinarioResponseDTO> buscarPorUserId(Long userId, Pageable pageable) {

        User usuarioLogado = getUsuarioAutenticado();

        if (!userId.equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException("Você não pode acessar os dados de outro usuário");
        }

        return veterinarioRepository.findByUserId(userId, pageable)
                .map(VeterinarioResponseDTO::fromEntity);
    }

    @Transactional
    public VeterinarioResponseDTO atualizar(Long id, VeterinarioRequestDTO requestDTO) {

        User usuarioLogado = getUsuarioAutenticado();

        Veterinario veterinarioExistente = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Veterinário não encontrado com ID: " + id
                ));

        if (!veterinarioExistente.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException(
                    "Você não pode atualizar o perfil de outro veterinário"
            );
        }

        veterinarioExistente.setCrmv(requestDTO.crmv());
        veterinarioExistente.setEspecialidade(requestDTO.especialidade());
        veterinarioExistente.setDescricao(requestDTO.descricao());

        Veterinario veterinarioAtualizado = veterinarioRepository.save(veterinarioExistente);

        return VeterinarioResponseDTO.fromEntity(veterinarioAtualizado);
    }

    @Transactional
    public void deletar(Long id) {

        User usuarioLogado = getUsuarioAutenticado();

        Veterinario veterinarioExistente = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Veterinário não encontrado com ID: " + id
                ));

        if (!veterinarioExistente.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException(
                    "Você não pode excluir o perfil de outro veterinário"
            );
        }

        if (petVeterinarioRepository.existsByVeterinario_IdAndAtivo(id, true)) {
            throw new DiarioEntradaComDependenciasException("Não é possível excluir o veterinário pois existem pets vinculados a ele");
        }

        veterinarioRepository.deleteById(id);
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

}
