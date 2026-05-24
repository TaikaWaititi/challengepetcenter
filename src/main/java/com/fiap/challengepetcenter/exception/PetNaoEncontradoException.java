package com.fiap.challengepetcenter.exception;

public class PetNaoEncontradoException extends RuntimeException {

    public PetNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
