package br.edu.cidadesesg.controller;

import br.edu.cidadesesg.model.Categoria;
import br.edu.cidadesesg.service.IniciativaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final IniciativaService service;

    public DashboardController(IniciativaService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("total", service.total());
        model.addAttribute("ambientais", service.totalPorCategoria(Categoria.AMBIENTAL));
        model.addAttribute("sociais", service.totalPorCategoria(Categoria.SOCIAL));
        model.addAttribute("governanca", service.totalPorCategoria(Categoria.GOVERNANCA));
        model.addAttribute("concluidas", service.totalConcluidas());
        model.addAttribute("iniciativas", service.listarTodas().stream().limit(5).toList());
        return "dashboard";
    }
}
