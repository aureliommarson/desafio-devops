package br.edu.cidadesesg.service;

import br.edu.cidadesesg.model.Categoria;
import br.edu.cidadesesg.model.Iniciativa;
import br.edu.cidadesesg.model.StatusIniciativa;
import br.edu.cidadesesg.repository.IniciativaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IniciativaService {

    private final IniciativaRepository repository;

    public IniciativaService(IniciativaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Iniciativa> listarTodas() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "dataCriacao"));
    }

    @Transactional(readOnly = true)
    public Iniciativa buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new IniciativaNaoEncontradaException(id));
    }

    @Transactional
    public Iniciativa salvar(Iniciativa iniciativa) {
        validarPontuacao(iniciativa.getPontuacaoImpacto());
        return repository.save(iniciativa);
    }

    @Transactional
    public Iniciativa atualizar(Long id, Iniciativa dados) {
        validarPontuacao(dados.getPontuacaoImpacto());
        Iniciativa existente = buscarPorId(id);
        existente.setTitulo(dados.getTitulo());
        existente.setDescricao(dados.getDescricao());
        existente.setCidade(dados.getCidade());
        existente.setCategoria(dados.getCategoria());
        existente.setStatus(dados.getStatus());
        existente.setPontuacaoImpacto(dados.getPontuacaoImpacto());
        return repository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Iniciativa existente = buscarPorId(id);
        repository.delete(existente);
    }

    public long total() { return repository.count(); }
    public long totalPorCategoria(Categoria categoria) { return repository.countByCategoria(categoria); }
    public long totalConcluidas() { return repository.countByStatus(StatusIniciativa.CONCLUIDA); }

    private void validarPontuacao(Integer pontuacao) {
        if (pontuacao == null || pontuacao < 1 || pontuacao > 10) {
            throw new IllegalArgumentException("A pontuação de impacto deve estar entre 1 e 10");
        }
    }
}
