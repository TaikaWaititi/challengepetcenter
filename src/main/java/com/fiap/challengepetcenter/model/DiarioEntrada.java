package com.fiap.challengepetcenter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "diario_entrada")
@Schema(
        name = "DiarioEntrada",
        description = "Representa uma entrada de diário associada a um pet no sistema API PetCenter"
)
public class DiarioEntrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "ID único da entrada do diário",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    @NotNull(message = "O pet é obrigatório")
    @Schema(
            description = "Pet associado à entrada do diário",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Pet pet;

    @NotNull(message = "A data é obrigatória")
    @Schema(
            description = "Data referente à entrada do diário",
            example = "2026-05-17"
    )
    private LocalDate data;

    @Column(updatable = false)
    @Schema(
            description = "Data e hora de criação da entrada",
            example = "2026-05-17T14:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime criadoEm;

    @Schema(
            description = "Data e hora da última atualização da entrada",
            example = "2026-05-17T18:00:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime atualizadoEm;

    @Schema(
            description = "Resumo geral do dia do pet",
            example = "Pet se alimentou bem e apresentou comportamento tranquilo"
    )
    private String resumo;

    @Schema(
            description = "Humor geral observado no pet",
            example = "Calmo"
    )
    private String humorGeral;

    @Column(nullable = false)
    @NotBlank(message = "O status é obrigatório")
    @Schema(
            description = "Status atual da entrada do diário",
            example = "Concluído"
    )
    private String status;

    public DiarioEntrada() {
    }

    public DiarioEntrada(Long id, Pet pet, LocalDate data, String resumo, String humorGeral, String status) {
        this.id = id;
        this.pet = pet;
        this.data = data;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        this.resumo = resumo;
        this.humorGeral = humorGeral;
        this.status = status;

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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public String getHumorGeral() {
        return humorGeral;
    }

    public void setHumorGeral(String humorGeral) {
        this.humorGeral = humorGeral;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
