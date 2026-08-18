package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class UsuarioController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @PostMapping("/cadastrar")
    public String cadastrar(@Valid @ModelAttribute Usuario usuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/cadastro.html?erro=true";
        }

        usuarioDAO.inserir(usuario);
        return "redirect:/cadastro.html?sucesso=true";
    }
}                                                        