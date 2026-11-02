package br.edu.cidadesesg.controller;

import br.edu.cidadesesg.model.Iniciativa;
import br.edu.cidadesesg.service.IniciativaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/iniciativas")
public class IniciativaApiController {

    private final IniciativaService service;

    public IniciativaApiController(IniciativaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Iniciativa> listar() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public Iniciativa buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Iniciativa> cadastrar(@Valid @RequestBody Iniciativa iniciativa) {
        Iniciativa criada = service.salvar(iniciativa);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @PutMapping("/{id}")
    public Iniciativa atualizar(@PathVariable Long id, @Valid @RequestBody Iniciativa iniciativa) {
        return service.atualizar(id, iniciativa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
