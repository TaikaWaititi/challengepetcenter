package com.fiap.challengepetcenter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "veterinarios")
@Schema(
        name = "Veterinario",
        description = "Representa um veterinário no sistema API PetCenter"
)
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único do veterinário",
            example = "1", accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(
            description = "Usuário associado ao veterinário",
            required = true)
    private User user;

    @NotBlank(message = "CRMV é obrigatório")
    @Size(min = 4, max = 20, message = "O CRMV deve ter entre 4 e 20 caracteres")
    @Column(unique = true, nullable = false, length = 20)
    @Schema(
            description = "Número de registro do veterinário no CRMV",
            example = "12345-SP",
            required = true,
            minLength = 4,
            maxLength = 20
    )
    private String crmv;

    @NotBlank(message = "Especialidade é obrigatória")
    @Size(min = 3, max = 100, message = "A especialidade deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)
    @Schema(
            description = "Especialidade do veterinário",
            example = "Clínica Geral",
            required = true,
            minLength = 3,
            maxLength = 100
    )
    private String especialidade;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, max = 500, message = "A descrição deve ter entre 10 e 500 caracteres")
    @Column(nullable = false, length = 500)
    @Schema(
            description = "Descrição profissional do veterinário",
            example = "Veterinário especializado em atendimento clínico de cães e gatos.",
            required = true, minLength = 10, maxLength = 500
    )
    private String descricao;

    public Veterinario() {
    }

    public Veterinario(Long id, User user, String crmv, String especialidade, String descricao) {
        this.id = id;
        this.user = user;
        this.crmv = crmv;
        this.especialidade = especialidade;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCrmv() {
        return crmv;
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}