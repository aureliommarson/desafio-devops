package br.edu.cidadesesg.service;

import br.edu.cidadesesg.model.Iniciativa;
import br.edu.cidadesesg.repository.IniciativaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class IniciativaServiceTest {

    private IniciativaRepository repository;
    private IniciativaService service;

    @BeforeEach
    void preparar() {
        repository = mock(IniciativaRepository.class);
        service = new IniciativaService(repository);
    }

    @Test
    void deveRejeitarPontuacaoAcimaDeDez() {
        Iniciativa iniciativa = new Iniciativa();
        iniciativa.setPontuacaoImpacto(11);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.salvar(iniciativa));

        assertEquals("A pontuação de impacto deve estar entre 1 e 10", exception.getMessage());
        verify(repository, never()).save(iniciativa);
    }

    @Test
    void deveRejeitarPontuacaoAbaixoDeUm() {
        Iniciativa iniciativa = new Iniciativa();
        iniciativa.setPontuacaoImpacto(0);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(iniciativa));
        verify(repository, never()).save(iniciativa);
    }
}
