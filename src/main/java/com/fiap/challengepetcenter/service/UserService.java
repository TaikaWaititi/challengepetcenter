package com.fiap.challengepetcenter.service;

import com.fiap.challengepetcenter.dto.request.UserRequestDTO;
import com.fiap.challengepetcenter.dto.response.UserResponseDTO;
import com.fiap.challengepetcenter.exception.RecursoNaoEncontradoException;
import com.fiap.challengepetcenter.exception.UserComDependenciasException;
import com.fiap.challengepetcenter.exception.ValidacaoException;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.repository.PetRepository;
import com.fiap.challengepetcenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PetRepository petRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO salvar(UserRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.email())) {
            throw new ValidacaoException("Email já cadastrado");
        }

        User user = new User();
        user.setNome(requestDTO.nome());
        user.setEmail(requestDTO.email());
        user.setSenha(passwordEncoder.encode(requestDTO.senha()));
        user.setTelefone(requestDTO.telefone());
        user.setTipoUsuario(requestDTO.tipoUsuario());
        user.setAtivo(true);

        User userSalvo = userRepository.save(user);

        return UserResponseDTO.fromEntity(userSalvo);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> listarTodos(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        return users.map(UserResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO buscarPorId(Long id) {

        User usuarioLogado = getUsuarioAutenticado();

        validarProprioUsuario(id, usuarioLogado);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO buscarPorEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com email: " + email));
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional
    public UserResponseDTO atualizar(Long id, UserRequestDTO requestDTO) {

        User usuarioLogado = getUsuarioAutenticado();

        validarProprioUsuario(id, usuarioLogado);

        User userExistente = userRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
        userExistente.setNome(requestDTO.nome());
        userExistente.setEmail(requestDTO.email());
        userExistente.setSenha(passwordEncoder.encode(requestDTO.senha()));
        userExistente.setTelefone(requestDTO.telefone());
        userExistente.setTipoUsuario(requestDTO.tipoUsuario());

        User userAtualizado = userRepository.save(userExistente);

        return UserResponseDTO.fromEntity(userAtualizado);
    }

    @Transactional
    public void deletar(Long id) {

        User usuarioLogado = getUsuarioAutenticado();

        validarProprioUsuario(id, usuarioLogado);

        if (!userRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id);
        }

        if (petRepository.existsByUserId(id)) {
            throw new UserComDependenciasException("Não é possível excluir o usuário pois existem pets vinculados a ele");
        }
        userRepository.deleteById(id);
    }

    // Usuário autenticado pelo JWT
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private void validarProprioUsuario(Long id, User usuarioLogado) {
        if (!id.equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException(
                    "Você não pode acessar os dados de outro usuário"
            );
        }
    }


}
