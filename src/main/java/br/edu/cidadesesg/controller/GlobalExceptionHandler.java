package br.edu.cidadesesg.controller;

import br.edu.cidadesesg.service.IniciativaNaoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IniciativaNaoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String registroNaoEncontrado(IniciativaNaoEncontradaException exception, Model model) {
        model.addAttribute("mensagem", exception.getMessage());
        return "erro/404";
    }
}
