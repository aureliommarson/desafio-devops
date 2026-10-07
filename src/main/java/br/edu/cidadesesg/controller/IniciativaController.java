package br.edu.cidadesesg.controller;

import br.edu.cidadesesg.model.Categoria;
import br.edu.cidadesesg.model.Iniciativa;
import br.edu.cidadesesg.model.StatusIniciativa;
import br.edu.cidadesesg.service.IniciativaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/iniciativas")
public class IniciativaController {

    private final IniciativaService service;

    public IniciativaController(IniciativaService service) {
        this.service = service;
    }

    @ModelAttribute
    public void opcoesFormulario(Model model) {
        model.addAttribute("categorias", Categoria.values());
        model.addAttribute("statusDisponiveis", StatusIniciativa.values());
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("iniciativas", service.listarTodas());
        return "iniciativas/lista";
    }

    @GetMapping("/nova")
    public String formularioCadastro(Model model) {
        model.addAttribute("iniciativa", new Iniciativa());
        model.addAttribute("modoEdicao", false);
        return "iniciativas/formulario";
    }

    @PostMapping
    public String cadastrar(@Valid @ModelAttribute Iniciativa iniciativa, BindingResult resultado,
                            Model model, RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "iniciativas/formulario";
        }
        service.salvar(iniciativa);
        redirectAttributes.addFlashAttribute("sucesso", "Iniciativa cadastrada com sucesso!");
        return "redirect:/iniciativas";
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        model.addAttribute("iniciativa", service.buscarPorId(id));
        return "iniciativas/detalhes";
    }

    @GetMapping("/{id}/editar")
    public String formularioEdicao(@PathVariable Long id, Model model) {
        model.addAttribute("iniciativa", service.buscarPorId(id));
        model.addAttribute("modoEdicao", true);
        return "iniciativas/formulario";
    }

    @PostMapping("/{id}")
    public String editar(@PathVariable Long id, @Valid @ModelAttribute Iniciativa iniciativa,
                         BindingResult resultado, Model model, RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            iniciativa.setId(id);
            model.addAttribute("modoEdicao", true);
            return "iniciativas/formulario";
        }
        service.atualizar(id, iniciativa);
        redirectAttributes.addFlashAttribute("sucesso", "Iniciativa atualizada com sucesso!");
        return "redirect:/iniciativas";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("sucesso", "Iniciativa excluída com sucesso!");
        return "redirect:/iniciativas";
    }
}
