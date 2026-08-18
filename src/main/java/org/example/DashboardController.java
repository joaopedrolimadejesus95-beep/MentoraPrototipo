package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final RotinaDAO rotinaDAO = new RotinaDAO();
    private final EstudoDAO estudoDAO = new EstudoDAO();

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("rotinas", rotinaDAO.listar());
        model.addAttribute("estudos", estudoDAO.ListarEstudos());
        return "dashboard";
    }
}