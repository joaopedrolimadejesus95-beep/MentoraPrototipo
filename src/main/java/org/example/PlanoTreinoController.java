package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Controller
public class PlanoTreinoController {
    private final PlanoTreinoDAO dao = new PlanoTreinoDAO();

    @GetMapping("/treinos/planos")
    public String listar(Model model) {
        try {
            model.addAttribute("planos", dao.listarAtivos());
        } catch (SQLException e) {
            model.addAttribute("planos", List.of());
            model.addAttribute("erroBanco", "Execute sql-planos-treino.sql e confira a conexão com o banco.");
        }
        return "planos-treino";
    }

    @PostMapping("/treinos/planos")
    public String criar(@RequestParam String nome,
                        @RequestParam String tipo,
                        @RequestParam int duracaoMin,
                        @RequestParam String exercicios,
                        RedirectAttributes redirect) {
        if (nome.isBlank() || nome.trim().length() > 80) {
            redirect.addFlashAttribute("erroAcao", "Dê um nome ao plano (até 80 caracteres).");
            return "redirect:/treinos/planos";
        }
        if (!(tipo.equals("CALISTENIA") || tipo.equals("CORRIDA")) || duracaoMin < 1 || duracaoMin > 360) {
            redirect.addFlashAttribute("erroAcao", "Escolha calistenia ou corrida e uma duração entre 1 e 360 minutos.");
            return "redirect:/treinos/planos";
        }
        List<String> nomes = Arrays.stream(exercicios.split("\\R"))
                .map(String::trim).filter(s -> !s.isBlank()).toList();
        if (nomes.isEmpty() || nomes.size() > 30 || nomes.stream().anyMatch(s -> s.length() > 120)) {
            redirect.addFlashAttribute("erroAcao", "Informe de 1 a 30 exercícios, com até 120 caracteres cada.");
            return "redirect:/treinos/planos";
        }
        try {
            dao.criar(nome, tipo, duracaoMin, nomes);
            redirect.addFlashAttribute("sucessoAcao", "Plano criado. Você já pode escolhê-lo na tela Hoje.");
        } catch (SQLException e) {
            redirect.addFlashAttribute("erroAcao", "Não foi possível salvar. Execute sql-planos-treino.sql e tente novamente.");
        }
        return "redirect:/treinos/planos";
    }

    @PostMapping("/treinos/planos/{id}/arquivar")
    public String arquivar(@PathVariable long id, RedirectAttributes redirect) {
        try {
            dao.arquivar(id);
            redirect.addFlashAttribute("sucessoAcao", "Plano arquivado. O histórico das sessões permanece salvo.");
        } catch (SQLException e) {
            redirect.addFlashAttribute("erroAcao", "Não foi possível arquivar esse plano.");
        }
        return "redirect:/treinos/planos";
    }
}
