package br.edu.cidadesesg.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveExibirDashboard() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("dashboard"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Cidades ESG Inteligentes")));
    }

    @Test
    void actuatorDeveResponderComSaude() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("UP")));
    }

    @Test
    void deveRenderizarListaDeIniciativas() throws Exception {
        mockMvc.perform(get("/iniciativas"))
            .andExpect(status().isOk())
            .andExpect(view().name("iniciativas/lista"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Programa Municipal de Coleta Seletiva")));
    }

    @Test
    void deveRenderizarFormularioDeCadastro() throws Exception {
        mockMvc.perform(get("/iniciativas/nova"))
            .andExpect(status().isOk())
            .andExpect(view().name("iniciativas/formulario"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Cadastrar iniciativa")));
    }
}
