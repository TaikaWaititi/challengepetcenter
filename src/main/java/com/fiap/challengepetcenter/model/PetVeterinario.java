package com.fiap.challengepetcenter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "pet_veterinario")
@Schema(
        name = "PetVeterinario",
        description = "Representa o vínculo entre um pet e um veterinário")
public class PetVeterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único do vínculo entre pet e veterinário",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    @NotNull(message = "Pet é obrigatório")
    @Schema(
            description = "Pet associado ao veterinário",
            required = true
    )
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "veterinario_id", nullable = false)
    @NotNull(message = "Veterinário é obrigatório")
    @Schema(
            description = "Veterinário associado ao pet",
            required = true
    )
    private Veterinario veterinario;

    @NotNull(message = "Data de início é obrigatória")
    @Column(name = "data_inicio", nullable = false)
    @Schema(
            description = "Data em que o vínculo entre o pet e o veterinário foi iniciado",
            example = "2026-09-05",
            required = true
    )
    private LocalDate dataInicio;

    @NotNull(message = "Status do vínculo é obrigatório")
    @Column(nullable = false)
    @Schema(
            description = "Indica se o vínculo entre o pet e o veterinário está ativo",
            example = "true",
            defaultValue = "true",
            required = true
    )
    private Boolean ativo = true;

    @Size(max = 500, message = "As observações devem ter no máximo 500 caracteres")
    @Column(length = 500)
    @Schema(
            description = "Observações sobre o vínculo entre o pet e o veterinário",
            example = "Veterinário responsável pelo acompanhamento clínico do pet.",
            maxLength = 500
    )
    private String observacoes;

    public PetVeterinario() {
    }

    public PetVeterinario(Long id, Pet pet, Veterinario veterinario, LocalDate dataInicio, Boolean ativo, String observacoes) {
        this.id = id;
        this.pet = pet;
        this.veterinario = veterinario;
        this.dataInicio = dataInicio;
        this.ativo = ativo;
        this.observacoes = observacoes;
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

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}