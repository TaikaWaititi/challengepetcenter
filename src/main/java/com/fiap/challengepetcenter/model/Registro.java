package com.fiap.challengepetcenter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "registros")
@Schema(
        name = "Registro",
        description = "Representa um registro associado a uma entrada no diário no sistema API PetCenter"
)
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "ID único do registro",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrada_id", nullable = false)
    @NotNull(message = "O ID da entrada é obrigatório")
    @Schema(
            description = "ID do diário de entrada associado ao registro",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private DiarioEntrada entrada;

    @NotBlank(message = "O tipo é obrigatório")
    @Schema(
            description = "Tipo do registro",
            example = "Alimentação"
    )
    private String tipo;

    @Schema(
            description = "Subtipo do registro",
            example = "Ração seca"
    )
    private String subtipo;

    @Schema(
            description = "Valor numérico relacionado ao registro",
            example = "250.0"
    )
    private Double valor;

    @Schema(
            description = "Unidade de medida do valor",
            example = "gramas"
    )
    private String unidade;

    @Schema(
            description = "Observações adicionais do registro",
            example = "Pet comeu normalmente"
    )
    private String nota;
    @Schema(
            description = "Data e hora do registro",
            example = "2026-05-17T14:30:00"
    )
    private LocalDateTime horario;

    @Schema(
            description = "Data e hora da última atualização do registro",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime atualizadoEm;

    public Registro() {
    }

    public Registro(DiarioEntrada entrada, Long id, String tipo, String subtipo, Double valor, String unidade, String nota) {
        this.entrada = entrada;
        this.id = id;
        this.tipo = tipo;
        this.subtipo = subtipo;
        this.valor = valor;
        this.unidade = unidade;
        this.nota = nota;
        this.horario = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiarioEntrada getEntrada() {
        return entrada;
    }

    public void setEntrada(DiarioEntrada entrada) {
        this.entrada = entrada;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSubtipo() {
        return subtipo;
    }

    public void setSubtipo(String subtipo) {
        this.subtipo = subtipo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    @PrePersist
    protected void onCreate() {
        this.horario = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}