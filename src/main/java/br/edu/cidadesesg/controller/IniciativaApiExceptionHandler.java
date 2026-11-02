package br.edu.cidadesesg.controller;

import br.edu.cidadesesg.service.IniciativaNaoEncontradaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = IniciativaApiController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class IniciativaApiExceptionHandler {

    @ExceptionHandler(IniciativaNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> naoEncontrada(IniciativaNaoEncontradaException exception,
                                                              HttpServletRequest request) {
        return resposta(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
        IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> requisicaoInvalida(Exception exception, HttpServletRequest request) {
        String mensagem;
        if (exception instanceof MethodArgumentNotValidException validation) {
            mensagem = validation.getBindingResult().getFieldErrors().stream()
                .map(this::mensagemCampo).distinct().collect(Collectors.joining("; "));
        } else if (exception instanceof HttpMessageNotReadableException) {
            mensagem = "Corpo JSON inválido ou com valores incompatíveis";
        } else {
            mensagem = exception.getMessage();
        }
        return resposta(HttpStatus.BAD_REQUEST, mensagem, request.getRequestURI());
    }

    private String mensagemCampo(FieldError erro) {
        return erro.getField() + ": " + erro.getDefaultMessage();
    }

    private ResponseEntity<Map<String, Object>> resposta(HttpStatus status, String mensagem, String caminho) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", Instant.now());
        corpo.put("status", status.value());
        corpo.put("error", status.getReasonPhrase());
        corpo.put("message", mensagem);
        corpo.put("path", caminho);
        return ResponseEntity.status(status).body(corpo);
    }
}
