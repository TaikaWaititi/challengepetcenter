package com.fiap.challengepetcenter.model;

import java.time.LocalDateTime;

public class Solicitacao {
    private Long id;
    private Pet pet;
    private User user;
    private Veterinario veterinario;
    private String status;
    private String mensagem;
    private LocalDateTime criadoEm;
    private LocalDateTime respondidoEm;

    public Solicitacao() {
    }

    public Solicitacao(Long id, Pet pet, User user, Veterinario veterinario, String status, String mensagem, LocalDateTime criadoEm, LocalDateTime respondidoEm) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
