package com.fiap.challengepetcenter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "pets")
@Schema(
        name = "Pet",
        description = "Representa um pet associado a um ususário no sistema API PetCenter"
)
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "ID único do pet",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "O tutorId é obrigatório")
    @Schema(
            description = "Tutor responsável pelo pet",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private User user;

    @Column(nullable = false, length = 100)
    @Schema(
            description = "Nome do pet",
            example = "Luna"
    )
    private String nome;

    @Column(nullable = false, length = 50)
    @Schema(
            description = "Espécie do pet",
            example = "Cachorro"
    )
    private String especie;

    @Column(length = 100)
    @Schema(
            description = "Raça do pet",
            example = "Golden Retriever"
    )
    private String raca;
    
    @Schema(
            description = "Data de nascimento do pet",
            example = "2022-03-15"
    )
    private LocalDate dataNascimento;

    @Column(length = 500)
    @Schema(
            description = "Observações adicionais sobre o pet",
            example = "Pet alérgico a determinados alimentos"
    )
    private String observacoes;

    public Pet() {
    }

    public Pet(User user, String nome, String especie, String raca, LocalDate dataNascimento, String observacoes) {
        this.user = user;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.dataNascimento = dataNascimento;
        this.observacoes = observacoes;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
