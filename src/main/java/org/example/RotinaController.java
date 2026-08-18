package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class RotinaController {

    private final RotinaDAO rotinaDAO = new RotinaDAO();

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
}