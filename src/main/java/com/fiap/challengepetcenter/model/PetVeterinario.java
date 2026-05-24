package com.fiap.challengepetcenter.model;

import java.time.LocalDate;

public class PetVeterinario {

    private Long id;
    private Pet pet;
    private Veterinario veterinario;
    private LocalDate dataInicio;
    private Boolean ativo;
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