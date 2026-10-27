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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
public class HojeController {

    private final RotinaDAO rotinaDAO = new RotinaDAO();
    private final EstudoDAO estudoDAO = new EstudoDAO();
    private final RegistroEstudoDAO registroEstudoDAO = new RegistroEstudoDAO();
    private final SessaoTreinoDAO sessaoTreinoDAO = new SessaoTreinoDAO();
    private final DisponibilidadeDAO disponibilidadeDAO = new DisponibilidadeDAO();
    private final PlanoTreinoDAO planoTreinoDAO = new PlanoTreinoDAO();

    @GetMapping({"/", "/hoje"})
    public String hoje(@RequestParam(required = false) Long planoId, Model model) {
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
        Integer minutos = null;
        try {
            int ajuste = disponibilidadeDAO.buscarAjuste(LocalDate.now());
            if (ajuste > 0) minutos = ajuste;
            model.addAttribute("horariosHoje", disponibilidadeDAO.listarSemana().stream()
                    .filter(intervalo -> intervalo.getDia() == LocalDate.now().getDayOfWeek()).toList());
        } catch (SQLException e) {
            model.addAttribute("erroDisponibilidade", "Execute sql-disponibilidade.sql para ativar o ajuste de tempo do dia.");
            model.addAttribute("horariosHoje", Collections.emptyList());
        }
        model.addAttribute("minutosDisponiveis", minutos);
        model.addAttribute("sugestaoTempo", sugestaoPara(minutos));
        List<PlanoTreino> planos;
        try {
            planos = planoTreinoDAO.listarAtivos();
        } catch (SQLException e) {
            planos = List.of();
            model.addAttribute("erroPlanos", "Execute sql-planos-treino.sql para cadastrar planos de treino.");
        }
        final Integer tempoHoje = minutos;
        List<PlanoTreino> planosHoje = planos.stream()
                .filter(plano -> tempoHoje == null || plano.getDuracaoEstimadaMin() <= tempoHoje).toList();
        model.addAttribute("planosHoje", planosHoje);
        PlanoTreino planoSelecionado = null;
        if (planoId != null) {
            try {
                planoSelecionado = planoTreinoDAO.buscarAtivo(planoId);
                if (planoSelecionado == null) model.addAttribute("erroPlanos", "Esse plano não está ativo.");
                else if (minutos != null && planoSelecionado.getDuracaoEstimadaMin() > minutos)
                    model.addAttribute("erroPlanos", "Esse plano leva mais tempo do que você tem hoje.");
            } catch (SQLException e) {
                model.addAttribute("erroPlanos", "Não consegui carregar os exercícios do plano.");
            }
        }
        model.addAttribute("planoSelecionado", planoSelecionado);
        model.addAttribute("exerciciosPlano", planoSelecionado == null ? List.of() : planoSelecionado.getExercicios());
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

    @PostMapping("/hoje/ajuste")
    public String salvarAjuste(@RequestParam int minutos, RedirectAttributes redirectAttributes) {
        if (minutos < 1 || minutos > 720) {
            redirectAttributes.addFlashAttribute("erroAcao", "Informe um tempo entre 1 e 720 minutos.");
            return "redirect:/hoje";
        }
        try {
            disponibilidadeDAO.salvarAjuste(LocalDate.now(), minutos);
            redirectAttributes.addFlashAttribute("sucessoAcao", "Tempo de hoje ajustado.");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("erroAcao", "Execute sql-disponibilidade.sql e tente novamente.");
        }
        return "redirect:/hoje";
    }

    @PostMapping("/hoje/ajuste/remover")
    public String limparAjuste(RedirectAttributes redirectAttributes) {
        try {
            disponibilidadeDAO.limparAjuste(LocalDate.now());
            redirectAttributes.addFlashAttribute("sucessoAcao", "Ajuste de hoje removido.");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("erroAcao", "Não foi possível remover o ajuste de hoje.");
        }
        return "redirect:/hoje";
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
                                  @RequestParam(required = false) Long planoId,
                                  @RequestParam(required = false) List<String> exercicios,
                                  @RequestParam(required = false) List<String> series,
                                  @RequestParam(required = false) List<String> repeticoes,
                                  @RequestParam(required = false) List<String> cargas,
                                  @RequestParam(required = false) String observacao,
                                  @RequestParam(defaultValue = "false") boolean concluida,
                                  RedirectAttributes redirectAttributes) {
        if (!(tipo.equals("CALISTENIA") || tipo.equals("CORRIDA"))) {
            redirectAttributes.addFlashAttribute("erroAcao", "Escolha calistenia ou corrida.");
            return "redirect:/hoje";
        }
        if (planoId != null) {
            try {
                PlanoTreino plano = planoTreinoDAO.buscarAtivo(planoId);
                if (plano == null || !plano.getTipo().equals(tipo)) {
                    redirectAttributes.addFlashAttribute("erroAcao", "O plano selecionado não está disponível para essa modalidade.");
                    return "redirect:/hoje";
                }
                int tempoDisponivelHoje = disponibilidadeDAO.buscarAjuste(LocalDate.now());
                if (tempoDisponivelHoje > 0 && plano.getDuracaoEstimadaMin() > tempoDisponivelHoje) {
                    redirectAttributes.addFlashAttribute("erroAcao", "Esse plano leva mais tempo do que você informou para hoje.");
                    return "redirect:/hoje";
                }
            } catch (SQLException e) {
                redirectAttributes.addFlashAttribute("erroAcao", "Não consegui verificar o plano selecionado.");
                return "redirect:/hoje";
            }
        }
        int linhas = Math.max(tamanho(exercicios), Math.max(tamanho(series), Math.max(tamanho(repeticoes), tamanho(cargas))));
        if (linhas > 30 || (observacao != null && observacao.length() > 1000)) {
            redirectAttributes.addFlashAttribute("erroAcao", "Use até 30 exercícios e uma observação de até 1000 caracteres.");
            return "redirect:/hoje";
        }
        List<ExercicioTreinoEntrada> itens = new ArrayList<>();
        try {
            for (int i = 0; i < linhas; i++) {
                String nome = valor(exercicios, i).trim();
                String seriesTexto = valor(series, i).trim();
                String repeticoesTexto = valor(repeticoes, i).trim();
                String cargaTexto = valor(cargas, i).trim();
                if (nome.isBlank() && seriesTexto.isBlank() && repeticoesTexto.isBlank() && cargaTexto.isBlank()) continue;
                if (nome.isBlank()) throw new IllegalArgumentException("Informe o exercício antes dos detalhes.");
                if (nome.length() > 120) throw new IllegalArgumentException("Cada exercício pode ter até 120 caracteres.");
                Integer quantidadeSeries = inteiroOpcional(seriesTexto, 1, 50, "séries");
                Integer quantidadeRepeticoes = inteiroOpcional(repeticoesTexto, 1, 1000, "repetições");
                BigDecimal carga = decimalOpcional(cargaTexto);
                itens.add(new ExercicioTreinoEntrada(nome, quantidadeSeries, quantidadeRepeticoes, carga));
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erroAcao", e.getMessage());
            return "redirect:/hoje";
        }
        try {
            sessaoTreinoDAO.registrar(tipo, LocalDate.now(), concluida, observacao, itens, planoId);
            redirectAttributes.addFlashAttribute("sucessoAcao", "Sessão de treino registrada.");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("erroAcao", "Não foi possível registrar. Execute o SQL da primeira etapa e confira o banco.");
        }
        return "redirect:/hoje";
    }

    private int tamanho(List<String> valores) { return valores == null ? 0 : valores.size(); }

    private String valor(List<String> valores, int indice) {
        return valores == null || indice >= valores.size() || valores.get(indice) == null ? "" : valores.get(indice);
    }

    private Integer inteiroOpcional(String texto, int minimo, int maximo, String campo) {
        if (texto.isBlank()) return null;
        try {
            int valor = Integer.parseInt(texto);
            if (valor < minimo || valor > maximo) throw new IllegalArgumentException("Informe " + campo + " entre " + minimo + " e " + maximo + ".");
            return valor;
        } catch (NumberFormatException e) { throw new IllegalArgumentException("Informe um número válido para " + campo + "."); }
    }

    private BigDecimal decimalOpcional(String texto) {
        if (texto.isBlank()) return null;
        try {
            BigDecimal valor = new BigDecimal(texto.replace(',', '.'));
            if (valor.signum() < 0 || valor.compareTo(new BigDecimal("10000")) > 0)
                throw new IllegalArgumentException("A carga deve ficar entre 0 e 10000 kg.");
            return valor;
        } catch (NumberFormatException e) { throw new IllegalArgumentException("Informe uma carga válida."); }
    }
}
