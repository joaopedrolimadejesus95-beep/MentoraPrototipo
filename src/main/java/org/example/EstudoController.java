package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class EstudoController {

    private final EstudoDAO estudoDAO = new EstudoDAO();

    @PostMapping("/cadastrar-estudo")
    public String cadastrar(
            @RequestParam String materia,
            @RequestParam String status) {

        Estudo estudo = new Estudo();
        estudo.setMateria(materia);
        estudo.setStatus(status);
        estudo.setDataCriacao(LocalDate.now());

        estudoDAO.inserirEstudo(estudo);

        return "redirect:/dashboard";
    }
}