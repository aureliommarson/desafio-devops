package br.edu.cidadesesg.service;

public class IniciativaNaoEncontradaException extends RuntimeException {
    public IniciativaNaoEncontradaException(Long id) {
        super("Iniciativa com id " + id + " não encontrada");
    }
}
