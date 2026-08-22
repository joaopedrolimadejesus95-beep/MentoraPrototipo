package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class RotinaController {

    private final RotinaDAO rotinaDAO = new RotinaDAO();
    private final RotinaService rotinaService = new RotinaService();

    @PostMapping("/cadastrar-rotina")
    public String cadastrar(
            @RequestParam String nome,
            @RequestParam(required = false) String descricao,
            @RequestParam String frequencia,
            @RequestParam(required = false, defaultValue = "false") boolean obrigatorio) {

        Rotina rotina = new Rotina();

        rotina.setNome(nome);
        rotina.setDescricao(descricao);
        rotina.setFrequencia(frequencia);
        rotina.setDataCriacao(LocalDate.now());
        rotina.setObrigatorio(obrigatorio);
        rotina.setStreakAtual(0);
        rotina.setMelhorStreak(0);
        rotina.setNivel(1);
        rotina.setXpAtual(0);
        rotina.setXpParaProximoNivel(100);

        rotinaDAO.inserir(rotina);

        return "redirect:/dashboard";
    }

    @PostMapping("/marcar-cumprido/{idRotina}")
    public String marcarCumprido(@PathVariable int idRotina) {
        rotinaService.marcarCumprido(idRotina);
        return "redirect:/dashboard";
    }

    @GetMapping("/editar-rotina/{idRotina}")
    public String telaEditar(@PathVariable int idRotina, Model model) {
        Rotina rotina = rotinaDAO.buscarPorId(idRotina);
        model.addAttribute("rotina", rotina);
        return "editar-rotina";
    }

    @PostMapping("/editar-rotina/{idRotina}")
    public String salvarEdicao(
            @PathVariable int idRotina,
            @RequestParam String nome,
            @RequestParam(required = false) String descricao,
            @RequestParam String frequencia,
            @RequestParam(required = false, defaultValue = "false") boolean obrigatorio) {

        Rotina rotina = new Rotina();
        rotina.setIdRotina(idRotina);
        rotina.setNome(nome);
        rotina.setDescricao(descricao);
        rotina.setFrequencia(frequencia);
        rotina.setObrigatorio(obrigatorio);

        rotinaDAO.atualizarDados(rotina);

        return "redirect:/dashboard";
    }

    @PostMapping("/deletar-rotina/{idRotina}")
    public String deletar(@PathVariable int idRotina) {
        rotinaDAO.deletar(idRotina);
        return "redirect:/dashboard";
    }
}
