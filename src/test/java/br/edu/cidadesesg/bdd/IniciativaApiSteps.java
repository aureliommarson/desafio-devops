package br.edu.cidadesesg.bdd;

import br.edu.cidadesesg.model.Categoria;
import br.edu.cidadesesg.model.Iniciativa;
import br.edu.cidadesesg.model.StatusIniciativa;
import br.edu.cidadesesg.repository.IniciativaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.E;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class IniciativaApiSteps {

    @Autowired private MockMvc mockMvc;
    @Autowired private IniciativaRepository repository;
    @Autowired private ObjectMapper objectMapper;
    private MvcResult resultado;
    private Iniciativa iniciativa;
    private Long id;

    @Before
    public void prepararCenario() {
        repository.deleteAll();
        resultado = null;
        iniciativa = iniciativa("Horta urbana comunitária", Categoria.AMBIENTAL, 8);
        id = null;
    }

    @Dado("que possuo informações válidas de uma iniciativa ambiental")
    public void iniciativaAmbientalValida() {
        iniciativa = iniciativa("Horta urbana comunitária", Categoria.AMBIENTAL, 8);
    }

    @Dado("que informo pontuação de impacto igual a 11")
    public void pontuacaoInvalida() {
        iniciativa.setPontuacaoImpacto(11);
    }

    @Dado("que existe uma iniciativa ESG cadastrada")
    @Dado("que existe uma iniciativa ambiental cadastrada")
    @Dado("que existe uma iniciativa cadastrada")
    public void iniciativaExistente() {
        iniciativa = repository.save(iniciativa);
        id = iniciativa.getId();
    }

    @Dado("que uma iniciativa não existe")
    public void iniciativaInexistente() {
        id = 987654321L;
    }

    @Dado("que existem iniciativas cadastradas")
    public void iniciativasExistentes() {
        repository.save(iniciativa);
        repository.save(iniciativa("Programa de capacitação profissional", Categoria.SOCIAL, 9));
    }

    @Quando("realizo o cadastro pela API")
    public void cadastrar() throws Exception {
        resultado = mockMvc.perform(post("/api/iniciativas").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(iniciativa))).andReturn();
    }

    @Quando("tento cadastrar a iniciativa")
    public void tentarCadastrar() throws Exception { cadastrar(); }

    @Quando("consulto a iniciativa pelo identificador")
    public void consultarExistente() throws Exception {
        resultado = mockMvc.perform(get("/api/iniciativas/{id}", id)).andReturn();
    }

    @Quando("realizo uma consulta pelo identificador")
    public void consultarInexistente() throws Exception { consultarExistente(); }

    @Quando("atualizo sua categoria para SOCIAL e sua pontuação para 9")
    public void atualizar() throws Exception {
        iniciativa.setCategoria(Categoria.SOCIAL);
        iniciativa.setPontuacaoImpacto(9);
        iniciativa.setTitulo("Horta urbana com inclusão social");
        resultado = mockMvc.perform(put("/api/iniciativas/{id}", id).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(iniciativa))).andReturn();
    }

    @Quando("realizo sua exclusão")
    public void excluir() throws Exception {
        resultado = mockMvc.perform(delete("/api/iniciativas/{id}", id)).andReturn();
    }

    @Quando("consulto a listagem")
    public void listar() throws Exception { resultado = mockMvc.perform(get("/api/iniciativas")).andReturn(); }

    @Quando("tento atualizar a iniciativa inexistente")
    public void atualizarInexistente() throws Exception {
        resultado = mockMvc.perform(put("/api/iniciativas/{id}", id).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(iniciativa))).andReturn();
    }

    @Quando("tento excluir a iniciativa inexistente")
    public void excluirInexistente() throws Exception {
        resultado = mockMvc.perform(delete("/api/iniciativas/{id}", id)).andReturn();
    }

    @Então("o sistema retorna HTTP {int}")
    @Então("a API retorna HTTP {int}")
    public void validarStatus(int status) {
        assertNotNull(resultado);
        assertEquals(status, resultado.getResponse().getStatus());
    }

    @E("a resposta contém os dados cadastrados")
    public void dadosCadastrados() throws Exception {
        JsonNode json = corpoJson();
        assertEquals(iniciativa.getTitulo(), json.get("titulo").asText());
        assertEquals("AMBIENTAL", json.get("categoria").asText());
        assertTrue(repository.existsById(json.get("id").asLong()));
    }

    @E("o identificador é gerado automaticamente")
    public void identificadorGerado() throws Exception {
        assertTrue(corpoJson().get("id").asLong() > 0);
    }

    @E("apresenta uma mensagem de validação em JSON")
    public void mensagemValidacao() throws Exception {
        JsonNode json = corpoJson();
        assertEquals(400, json.get("status").asInt());
        assertTrue(json.get("message").asText().contains("pontuacaoImpacto"));
        assertEquals("/api/iniciativas", json.get("path").asText());
    }

    @E("apresenta os dados da iniciativa")
    public void dadosIniciativa() throws Exception {
        assertEquals(id, corpoJson().get("id").asLong());
        assertEquals(iniciativa.getTitulo(), corpoJson().get("titulo").asText());
    }

    @E("apresenta um erro estruturado em JSON")
    public void erroJson() throws Exception {
        JsonNode json = corpoJson();
        assertEquals(resultado.getResponse().getStatus(), json.get("status").asInt());
        assertTrue(json.hasNonNull("timestamp"));
        assertTrue(json.hasNonNull("error"));
        assertTrue(json.hasNonNull("message"));
        assertTrue(json.hasNonNull("path"));
    }

    @E("a iniciativa apresenta os dados atualizados")
    public void dadosAtualizados() throws Exception {
        JsonNode json = corpoJson();
        assertEquals("SOCIAL", json.get("categoria").asText());
        assertEquals(9, json.get("pontuacaoImpacto").asInt());
        assertEquals("SOCIAL", repository.findById(id).orElseThrow().getCategoria().name());
    }

    @E("uma consulta posterior retorna HTTP 404")
    public void consultaPosterior404() throws Exception {
        resultado = mockMvc.perform(get("/api/iniciativas/{id}", id)).andReturn();
        assertEquals(404, resultado.getResponse().getStatus());
    }

    @E("apresenta uma lista JSON com as iniciativas existentes")
    public void listaJson() throws Exception {
        JsonNode json = corpoJson();
        assertTrue(json.isArray());
        assertEquals(2, json.size());
        assertTrue(json.toString().contains("AMBIENTAL"));
        assertTrue(json.toString().contains("SOCIAL"));
    }

    private JsonNode corpoJson() throws Exception {
        assertTrue(resultado.getResponse().getContentAsString().length() > 0);
        return objectMapper.readTree(resultado.getResponse().getContentAsString());
    }

    private Iniciativa iniciativa(String titulo, Categoria categoria, int pontuacao) {
        return new Iniciativa(titulo, "Iniciativa sustentável para a cidade", "Curitiba",
            categoria, StatusIniciativa.PLANEJADA, pontuacao);
    }
}
