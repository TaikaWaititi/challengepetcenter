package com.fiap.challengepetcenter.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

        @Schema(
                description = "Email do usuário",
                example = "joao@email.com"
        )
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @Schema(
                description = "Senha do usuário",
                example = "123456"
        )
        @NotBlank(message = "Senha é obrigatória")
        String senha

) {
}