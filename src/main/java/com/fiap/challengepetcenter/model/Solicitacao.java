package com.fiap.challengepetcenter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacoes")
@Schema(
        name = "Solicitacao",
        description = "Representa uma solicitação de vínculo entre um tutor, seu pet e um veterinário"
)
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único da solicitação",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    @NotNull(message = "Pet é obrigatório")
    @Schema(
            description = "Pet para o qual o vínculo está sendo solicitado",
            required = true
    )
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "tutor_id", nullable = false)
    @NotNull(message = "Usuário é obrigatório")
    @Schema(
            description = "Usuário tutor responsável pela solicitação",
            required = true
    )
    private User user;

    @ManyToOne
    @JoinColumn(name = "veterinario_id", nullable = false)
    @NotNull(message = "Veterinário é obrigatório")
    @Schema(
            description = "Veterinário para o qual a solicitação foi enviada",
            required = true
    )
    private Veterinario veterinario;

    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Schema(
            description = "Status atual da solicitação",
            example = "PENDENTE",
            required = true
    )
    private StatusSolicitacao status;

    @Size(
            max = 500,
            message = "A mensagem deve ter no máximo 500 caracteres"
    )
    @Column(length = 500)
    @Schema(
            description = "Mensagem enviada pelo tutor ao veterinário",
            example = "Meu pet precisa de acompanhamento para alergia.",
            maxLength = 500
    )
    private String mensagem;

    @NotNull(message = "Data de criação é obrigatória")
    @Column(nullable = false)
    @Schema(
            description = "Data e hora em que a solicitação foi criada",
            example = "2026-09-05T15:30:00",
            required = true,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime criadoEm;

    @Schema(
            description = "Data e hora em que o veterinário respondeu à solicitação",
            example = "2026-09-05T16:00:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime respondidoEm;


    public Solicitacao() {
    }

    public Solicitacao(Long id, Pet pet, User user, Veterinario veterinario, StatusSolicitacao status, String mensagem, LocalDateTime criadoEm, LocalDateTime respondidoEm) {
        this.id = id;
        this.pet = pet;
        this.user = user;
        this.veterinario = veterinario;
        this.status = status;
        this.mensagem = mensagem;
        this.criadoEm = criadoEm;
        this.respondidoEm = respondidoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getRespondidoEm() {
        return respondidoEm;
    }

    public void setRespondidoEm(LocalDateTime respondidoEm) {
        this.respondidoEm = respondidoEm;
    }
}
