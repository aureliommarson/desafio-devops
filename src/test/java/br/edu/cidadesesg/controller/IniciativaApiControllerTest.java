package br.edu.cidadesesg.controller;

import br.edu.cidadesesg.model.Categoria;
import br.edu.cidadesesg.model.Iniciativa;
import br.edu.cidadesesg.model.StatusIniciativa;
import br.edu.cidadesesg.repository.IniciativaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:cidadesesg-api;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
class IniciativaApiControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private IniciativaRepository repository;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void limparDados() {
        repository.deleteAll();
    }

    @Test
    void deveListarIniciativas() throws Exception {
        repository.save(novaIniciativa());

        mockMvc.perform(get("/api/iniciativas"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].titulo").value("Coleta seletiva"))
            .andExpect(jsonPath("$[0].categoria").value("AMBIENTAL"));
    }

    @Test
    void deveConsultarIniciativaExistente() throws Exception {
        Iniciativa salva = repository.save(novaIniciativa());

        mockMvc.perform(get("/api/iniciativas/{id}", salva.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(salva.getId()))
            .andExpect(jsonPath("$.titulo").value("Coleta seletiva"))
            .andExpect(jsonPath("$.pontuacaoImpacto").value(8));
    }

    @Test
    void deveRetornar404AoConsultarIniciativaInexistente() throws Exception {
        mockMvc.perform(get("/api/iniciativas/9999"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deveCadastrarIniciativaValida() throws Exception {
        mockMvc.perform(post("/api/iniciativas").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaIniciativa())))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.titulo").value("Coleta seletiva"))
            .andExpect(jsonPath("$.dataCriacao").exists());
    }

    @Test
    void deveRejeitarCadastroComDadosInvalidos() throws Exception {
        Iniciativa invalida = novaIniciativa();
        invalida.setTitulo(" ");
        invalida.setPontuacaoImpacto(11);

        mockMvc.perform(post("/api/iniciativas").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalida)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("titulo")))
            .andExpect(jsonPath("$.path").value("/api/iniciativas"));
    }

    @Test
    void deveAtualizarIniciativaValida() throws Exception {
        Iniciativa salva = repository.save(novaIniciativa());
        Iniciativa atualizada = novaIniciativa();
        atualizada.setTitulo("Coleta seletiva ampliada");
        atualizada.setStatus(StatusIniciativa.EM_ANDAMENTO);

        mockMvc.perform(put("/api/iniciativas/{id}", salva.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizada)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(salva.getId()))
            .andExpect(jsonPath("$.titulo").value("Coleta seletiva ampliada"))
            .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
    }

    @Test
    void deveRetornar400AoAtualizarComDadosInvalidos() throws Exception {
        Iniciativa invalida = novaIniciativa();
        invalida.setCidade("");

        mockMvc.perform(put("/api/iniciativas/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalida)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("cidade")));
    }

    @Test
    void deveRetornar404AoAtualizarIniciativaInexistente() throws Exception {
        mockMvc.perform(put("/api/iniciativas/9999").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaIniciativa())))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveExcluirIniciativaExistente() throws Exception {
        Iniciativa salva = repository.save(novaIniciativa());

        mockMvc.perform(delete("/api/iniciativas/{id}", salva.getId()))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));
    }

    @Test
    void deveRetornar404AoExcluirIniciativaInexistente() throws Exception {
        mockMvc.perform(delete("/api/iniciativas/9999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"));
    }

    private Iniciativa novaIniciativa() {
        return new Iniciativa("Coleta seletiva", "Ampliação da coleta seletiva municipal",
            "São Paulo", Categoria.AMBIENTAL, StatusIniciativa.PLANEJADA, 8);
    }
}
