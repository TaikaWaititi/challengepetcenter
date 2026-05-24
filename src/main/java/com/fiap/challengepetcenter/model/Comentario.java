package com.fiap.challengepetcenter.model;

import java.time.LocalDateTime;

public class Comentario {

    private Long id;
    private DiarioEntrada entrada;
    private User user;
    private String comentario;
    private LocalDateTime criadoEm;
    private LocalDateTime editadoEm;

    public Comentario() {
    }

    public Comentario(Long id, DiarioEntrada entrada, User user, String comentario, LocalDateTime criadoEm, LocalDateTime editadoEm) {
        this.id = id;
        this.entrada = entrada;
        this.user = user;
        this.comentario = comentario;
        this.criadoEm = criadoEm;
        this.editadoEm = editadoEm;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getEditadoEm() {
        return editadoEm;
    }

    public void setEditadoEm(LocalDateTime editadoEm) {
        this.editadoEm = editadoEm;
    }
}