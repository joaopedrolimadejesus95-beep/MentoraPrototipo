package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class EstudoController {

    private final EstudoDAO estudoDAO = new EstudoDAO();
    private final EstudoService estudoService = new EstudoService();

    @PostMapping("/cadastrar-estudo")
    public String cadastrar(
            @RequestParam String materia,
            @RequestParam String status) {

        Estudo estudo = new Estudo();
        estudo.setMateria(materia);
        estudo.setStatus(status);
        estudo.setDataCriacao(LocalDate.now());
        estudo.setStreakAtual(0);
        estudo.setMelhorStreak(0);

        estudoDAO.inserirEstudo(estudo);

        return "redirect:/dashboard";
    }

    @PostMapping("/marcar-estudado/{idEstudo}")
    public String marcarEstudado(@PathVariable int idEstudo) {
        estudoService.marcarEstudado(idEstudo);
        return "redirect:/dashboard";
    }

    @GetMapping("/editar-estudo/{idEstudo}")
    public String telaEditar(@PathVariable int idEstudo, Model model) {
        Estudo estudo = estudoDAO.buscarPorId(idEstudo);
        model.addAttribute("estudo", estudo);
        return "editar-estudo";
    }

    @PostMapping("/editar-estudo/{idEstudo}")
    public String salvarEdicao(
            @PathVariable int idEstudo,
            @RequestParam String materia,
            @RequestParam String status) {

        Estudo estudo = new Estudo();
        estudo.setIdEstudo(idEstudo);
        estudo.setMateria(materia);
        estudo.setStatus(status);

        estudoDAO.atualizarDados(estudo);

        return "redirect:/dashboard";
    }

    @PostMapping("/deletar-estudo/{idEstudo}")
    public String deletar(@PathVariable int idEstudo) {
        estudoDAO.deletar(idEstudo);
        return "redirect:/dashboard";
    }
}
