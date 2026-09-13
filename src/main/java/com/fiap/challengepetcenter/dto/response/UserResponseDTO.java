package com.fiap.challengepetcenter.dto.response;

import com.fiap.challengepetcenter.model.TipoUsuario;
import com.fiap.challengepetcenter.model.User;

import java.time.LocalDateTime;

public record UserResponseDTO(

        Long id,
        String nome,
        String email,
        String telefone,
        TipoUsuario tipoUsuario,
        boolean ativo,
        LocalDateTime dataCriacao

) {

    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getTelefone(),
                user.getTipoUsuario(),
                user.getAtivo(),
                user.getDataCriacao()
        );
    }
}