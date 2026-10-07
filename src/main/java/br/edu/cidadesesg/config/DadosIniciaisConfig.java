package br.edu.cidadesesg.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.edu.cidadesesg.model.Categoria;
import br.edu.cidadesesg.model.Iniciativa;
import br.edu.cidadesesg.model.StatusIniciativa;
import br.edu.cidadesesg.repository.IniciativaRepository;

@Configuration
public class DadosIniciaisConfig {

    @Bean
    CommandLineRunner carregarDadosIniciais(IniciativaRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                    new Iniciativa("Programa Municipal de Coleta Seletiva", "Ampliação da coleta e reciclagem nos bairros.", "Curitiba", Categoria.AMBIENTAL, StatusIniciativa.EM_ANDAMENTO, 9),
                    new Iniciativa("Instalação de Painéis Solares", "Energia solar em escolas e unidades de saúde.", "Recife", Categoria.AMBIENTAL, StatusIniciativa.CONCLUIDA, 8),
                    new Iniciativa("Projeto Cidade Mais Verde", "Plantio de árvores em corredores urbanos.", "Campinas", Categoria.AMBIENTAL, StatusIniciativa.PLANEJADA, 7),
                    new Iniciativa("Programa de Inclusão Digital", "Acesso gratuito a cursos e laboratórios de informática.", "Salvador", Categoria.SOCIAL, StatusIniciativa.EM_ANDAMENTO, 8),
                    new Iniciativa("Portal de Transparência Municipal", "Publicação acessível de receitas, despesas e contratos.", "Belo Horizonte", Categoria.GOVERNANCA, StatusIniciativa.CONCLUIDA, 9)
                ));
            }
        };
    }
}
