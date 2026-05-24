package com.fiap.challengepetcenter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Schema(
        name = "User",
        description = "Representa um usuário no sistema API PetCenter"
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "ID único do usuário",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)
    @Schema(
            description = "Nome do usuário",
            example = "João Silva",
            required = true,
            minLength = 3,
            maxLength = 100
    )
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(min = 3, max = 120, message = "O email deve ter entre 3 e 120 caracteres")
    @Column(unique = true, nullable = false, length = 120)
    @Schema(
            description = "Email único do usuário",
            example = "joaosilva@email.com",
            required = true,
            minLength = 3,
            maxLength = 120
    )
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 8, message = "Senha deve ter entre 6 e 8 caracteres")
    @Column(nullable = false)
    @Schema(
            description = "Senha do usuário",
            example = "123642",
            required = true,
            minLength = 6,
            maxLength = 8
    )
    private String senha;

    @NotBlank(message = "Telefone obrigatório")
    @Column(nullable = false, length = 20)
    @Schema(
            description = "Telefone do usuário",
            example = "11 94002-8922",
            required = true,
            maxLength = 20
    )
    private String telefone;

    @NotBlank(message = "Tipo de usuário é obrigatório")
    @Column(name = "tipo_usuario", nullable = false, length = 20)
    @Schema(
            description = "Tipo do usuário",
            example = "Tutor",
            required = true,
            maxLength = 20
    )
    private String tipoUsuario;

    @Schema(
            description = "Indica se o usuário está ativo",
            example = "true",
            defaultValue = "true"
    )
    private Boolean ativo = true;

    @Column(name = "ultimo_login")
    @Schema(
            description = "Data e hora do último login",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime ultimoLogin;

    @Column(name = "data_criacao", updatable = false)
    @Schema(
            description = "Data e hora da criação do usuário",
            example = "2026-01-10T10:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime dataCriacao;

    public User() {
    }

    public User(String nome, String email, String senha, String telefone, String tipoUsuario, Boolean ativo, LocalDateTime ultimoLogin) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.tipoUsuario = tipoUsuario;
        this.ativo = ativo;
        this.ultimoLogin = ultimoLogin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();

        if (this.ativo == null) {
            this.ativo = true;
        }
    }
}
