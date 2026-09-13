package com.fiap.challengepetcenter.dto.request;

import com.fiap.challengepetcenter.model.TipoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(

        @Schema(
                description = "Nome do usuário",
                example = "João Silva"
        )
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @Schema(
                description = "Email do usuário",
                example = "joao@email.com"
        )
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 150, message = "O email deve ter no máximo 150 caracteres")
        String email,

        @Schema(
                description = "Senha do usuário",
                example = "123456"
        )
        @NotBlank(message = "Senha obrigatória")
        @Size(min = 6, max = 8, message = "Senha deve ter entre 6 e 8 caracteres")
        String senha,

        @Schema(
                description = "Telefone do usuário",
                example = "11 94002-8922"
        )
        String telefone,

        @Schema(
                description = "Tipo do usuário",
                example = "TUTOR"
        )
        @NotNull(message = "Tipo de usuário é obrigatório")
        TipoUsuario tipoUsuario

) {
}



