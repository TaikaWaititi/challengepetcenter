package com.fiap.challengepetcenter.exception;

public class UserNaoEncontradoException extends RuntimeException {

    public UserNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
