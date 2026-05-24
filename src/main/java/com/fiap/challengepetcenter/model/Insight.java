package com.fiap.challengepetcenter.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Insight {
    private Long id;
    private Pet pet;
    private String tipo;
    private String descricao;
    private String origemRegra;
    private String nivelAlerta;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalDateTime criadoEm;
    private String geradoPor;
    private String status;
    private String contexto;

    public Insight() {
    }

    public Insight(Long id, Pet pet, String tipo, String descricao, String origemRegra, String nivelAlerta, LocalDate dataInicio, LocalDate dataFim, LocalDateTime criadoEm, String geradoPor, String status, String contexto) {
        this.id = id;
        this.pet = pet;
        this.tipo = tipo;
        this.descricao = descricao;
        this.origemRegra = origemRegra;
        this.nivelAlerta = nivelAlerta;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.criadoEm = criadoEm;
        this.geradoPor = geradoPor;
        this.status = status;
        this.contexto = contexto;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getOrigemRegra() {
        return origemRegra;
    }

    public void setOrigemRegra(String origemRegra) {
        this.origemRegra = origemRegra;
    }

    public String getNivelAlerta() {
        return nivelAlerta;
    }

    public void setNivelAlerta(String nivelAlerta) {
        this.nivelAlerta = nivelAlerta;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getGeradoPor() {
        return geradoPor;
    }

    public void setGeradoPor(String geradoPor) {
        this.geradoPor = geradoPor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContexto() {
        return contexto;
    }

    public void setContexto(String contexto) {
        this.contexto = contexto;
    }
}
