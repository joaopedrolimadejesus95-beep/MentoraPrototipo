package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
public class HojeController {

    private final RotinaDAO rotinaDAO = new RotinaDAO();
    private final EstudoDAO estudoDAO = new EstudoDAO();
    private final RegistroEstudoDAO registroEstudoDAO = new RegistroEstudoDAO();
    private final SessaoTreinoDAO sessaoTreinoDAO = new SessaoTreinoDAO();

    @GetMapping({"/", "/hoje"})
    public String hoje(@RequestParam(required = false) Integer minutos, Model model) {
        model.addAttribute("dataFormatada", LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale.forLanguageTag("pt-BR"))));
        model.addAttribute("rotinas", rotinaDAO.listar());
        model.addAttribute("estudos", estudoDAO.ListarEstudos());
        try {
            model.addAttribute("minutosEstudoHoje", registroEstudoDAO.minutosHoje());
            model.addAttribute("metaEstudoMin", registroEstudoDAO.metaDiariaMin());
            model.addAttribute("minimoEstudoMin", registroEstudoDAO.minimoDiarioMin());
            model.addAttribute("sequenciaEstudo", registroEstudoDAO.sequenciaAtual());
        } catch (SQLException e) {
            model.addAttribute("erroBanco", "Não consegui carregar o resumo de estudos. Confira a conexão com o banco.");
            model.addAttribute("minutosEstudoHoje", 0);
            model.addAttribute("metaEstudoMin", 90);
            model.addAttribute("minimoEstudoMin", 15);
            model.addAttribute("sequenciaEstudo", 0);
        }
        model.addAttribute("minutosDisponiveis", minutos != null && minutos > 0 && minutos <= 720 ? minutos : null);
        model.addAttribute("sugestaoTempo", sugestaoPara(minutos));
        try {
            model.addAttribute("treinosHoje", sessaoTreinoDAO.listarHoje(LocalDate.now()));
            model.addAttribute("calisteniaSemana", sessaoTreinoDAO.contarSemana("CALISTENIA"));
            model.addAttribute("corridaSemana", sessaoTreinoDAO.contarSemana("CORRIDA"));
        } catch (SQLException e) {
            model.addAttribute("treinosHoje", Collections.emptyList());
            model.addAttribute("calisteniaSemana", 0);
            model.addAttribute("corridaSemana", 0);
        }
        return "hoje";
    }

    private String sugestaoPara(Integer minutos) {
        if (minutos == null || minutos <= 0 || minutos > 720) {
            return "Por padrão, siga a ordem do seu plano. Informe seu tempo livre para ajustar a sugestão de hoje.";
        }
        if (minutos <= 15) {
            return "Faça um bloco curto de estudo. A partir de 15 minutos registrados, o dia conta para sua sequência.";
        }
        if (minutos <= 30) {
            return "Um bloco de estudo cabe nesse intervalo. Escolha carreira dev ou faculdade.";
        }
        if (minutos < 60) {
            return "Escolha um bloco de estudo ou comece uma parte do treino que você definiu.";
        }
        return "Esse intervalo pode acomodar um treino definido por você ou mais tempo de estudo.";
    }

    @PostMapping("/hoje/estudo")
    public String registrarEstudo(@RequestParam String area,
                                  @RequestParam int duracaoMin,
                                  @RequestParam(required = false) String assunto,
                                  RedirectAttributes redirectAttributes) {
        if (!(area.equals("CARREIRA_DEV") || area.equals("FACULDADE")) || duracaoMin < 1 || duracaoMin > 1440) {
            redirectAttributes.addFlashAttribute("erroAcao", "Escolha uma área e informe uma duração válida.");
            return "redirect:/hoje";
        }
        try {
            registroEstudoDAO.registrar(area, duracaoMin, assunto);
            redirectAttributes.addFlashAttribute("sucessoAcao", "Sessão de estudo registrada.");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("erroAcao", "Não foi possível registrar. Execute o SQL da primeira etapa e confira o banco.");
        }
        return "redirect:/hoje";
    }

    @PostMapping("/hoje/treino")
    public String registrarTreino(@RequestParam String tipo,
                                  @RequestParam(required = false) String exercicios,
                                  @RequestParam(required = false) String observacao,
                                  @RequestParam(defaultValue = "false") boolean concluida,
                                  RedirectAttributes redirectAttributes) {
        if (!(tipo.equals("CALISTENIA") || tipo.equals("CORRIDA"))) {
            redirectAttributes.addFlashAttribute("erroAcao", "Escolha calistenia ou corrida.");
            return "redirect:/hoje";
        }
        List<String> nomes = exercicios == null ? List.of() : Arrays.stream(exercicios.split("\\R"))
                .map(String::trim).filter(nome -> !nome.isBlank()).toList();
        if (nomes.size() > 30 || nomes.stream().anyMatch(nome -> nome.length() > 120)
                || (observacao != null && observacao.length() > 1000)) {
            redirectAttributes.addFlashAttribute("erroAcao", "Use até 30 exercícios, com no máximo 120 caracteres cada.");
            return "redirect:/hoje";
        }
        try {
            sessaoTreinoDAO.registrar(tipo, LocalDate.now(), concluida, observacao, nomes);
            redirectAttributes.addFlashAttribute("sucessoAcao", "Sessão de treino registrada.");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("erroAcao", "Não foi possível registrar. Execute o SQL da primeira etapa e confira o banco.");
        }
        return "redirect:/hoje";
    }
}
