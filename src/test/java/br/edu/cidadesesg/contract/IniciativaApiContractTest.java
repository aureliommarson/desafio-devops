package br.edu.cidadesesg.contract;

import br.edu.cidadesesg.model.Categoria;
import br.edu.cidadesesg.model.Iniciativa;
import br.edu.cidadesesg.model.StatusIniciativa;
import br.edu.cidadesesg.repository.IniciativaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:cidadesesg-contract;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
class IniciativaApiContractTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private IniciativaRepository repository;
    @Autowired private ObjectMapper objectMapper;
    private final JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);

    @BeforeEach
    void limparBancoDeTeste() { repository.deleteAll(); }

    @Test
    void contratoDaListagem() throws Exception {
        repository.save(novaIniciativa());
        validarJson(mockMvc.perform(get("/api/iniciativas")).andReturn(), 200, "iniciativa-lista.schema.json");
    }

    @Test
    void contratoDaConsultaExistente() throws Exception {
        Iniciativa salva = repository.save(novaIniciativa());
        validarJson(mockMvc.perform(get("/api/iniciativas/{id}", salva.getId())).andReturn(), 200,
            "iniciativa.schema.json");
    }

    @Test
    void contratoDaConsultaInexistente() throws Exception {
        validarJson(mockMvc.perform(get("/api/iniciativas/999999")).andReturn(), 404, "erro-api.schema.json");
    }

    @Test
    void contratoDoCadastroValido() throws Exception {
        validarJson(mockMvc.perform(post("/api/iniciativas").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(novaIniciativa()))).andReturn(), 201, "iniciativa.schema.json");
    }

    @Test
    void contratoDoCadastroInvalido() throws Exception {
        Iniciativa invalida = novaIniciativa();
        invalida.setPontuacaoImpacto(11);
        validarJson(mockMvc.perform(post("/api/iniciativas").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalida))).andReturn(), 400, "erro-api.schema.json");
    }

    @Test
    void contratoDaAtualizacaoValida() throws Exception {
        Iniciativa salva = repository.save(novaIniciativa());
        Iniciativa atualizada = novaIniciativa();
        atualizada.setCategoria(Categoria.SOCIAL);
        atualizada.setPontuacaoImpacto(9);
        validarJson(mockMvc.perform(put("/api/iniciativas/{id}", salva.getId())
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(atualizada)))
            .andReturn(), 200, "iniciativa.schema.json");
    }

    @Test
    void contratoDaAtualizacaoInvalida() throws Exception {
        Iniciativa invalida = novaIniciativa();
        invalida.setPontuacaoImpacto(11);
        validarJson(mockMvc.perform(put("/api/iniciativas/1").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalida))).andReturn(), 400, "erro-api.schema.json");
    }

    @Test
    void contratoDaAtualizacaoInexistente() throws Exception {
        validarJson(mockMvc.perform(put("/api/iniciativas/999999").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(novaIniciativa()))).andReturn(), 404, "erro-api.schema.json");
    }

    @Test
    void contratoDaExclusaoValidaSemCorpo() throws Exception {
        Iniciativa salva = repository.save(novaIniciativa());
        MvcResult result = mockMvc.perform(delete("/api/iniciativas/{id}", salva.getId())).andReturn();
        assertEquals(204, result.getResponse().getStatus());
        assertEquals("", result.getResponse().getContentAsString());
    }

    @Test
    void contratoDaExclusaoInexistente() throws Exception {
        validarJson(mockMvc.perform(delete("/api/iniciativas/999999")).andReturn(), 404, "erro-api.schema.json");
    }

    @Test
    void contratoDoHealthCheck() throws Exception {
        validarJson(mockMvc.perform(get("/actuator/health")).andReturn(), 200, "health.schema.json");
    }

    private void validarJson(MvcResult result, int statusEsperado, String arquivoSchema) throws Exception {
        assertEquals(statusEsperado, result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().contains("json"),
            "Resposta deve ser JSON, recebido: " + result.getResponse().getContentType());
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        try (InputStream schemaStream = getClass().getResourceAsStream("/schemas/" + arquivoSchema)) {
            assertNotNull(schemaStream, "Schema não encontrado: " + arquivoSchema);
            JsonSchema schema = schemaFactory.getSchema(schemaStream);
            Set<String> erros = schema.validate(objectMapper.writeValueAsString(json), InputFormat.JSON)
                .stream().map(Object::toString).collect(Collectors.toSet());
            assertTrue(erros.isEmpty(), "JSON incompatível com " + arquivoSchema + ": " + erros);
        }
    }

    private Iniciativa novaIniciativa() {
        return new Iniciativa("Energia solar comunitária", "Instalação de painéis solares públicos",
            "Recife", Categoria.AMBIENTAL, StatusIniciativa.PLANEJADA, 8);
    }
}
