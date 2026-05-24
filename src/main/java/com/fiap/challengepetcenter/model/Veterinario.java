package com.fiap.challengepetcenter.model;

import org.apache.catalina.User;

public class Veterinario {

    private Long id;
    private User user;
    private String crmv;
    private String especialidade;
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