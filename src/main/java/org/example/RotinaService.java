package org.example;

import java.time.LocalDate;

public class RotinaService {

    private final RotinaDAO rotinaDAO = new RotinaDAO();
    private final RegistroCumprimentoDAO registroDAO = new RegistroCumprimentoDAO();

    public void marcarCumprido(int idRotina) {
        LocalDate hoje = LocalDate.now();
        LocalDate ontem = hoje.minusDays(1);

        // 1. Já foi marcado hoje? Se sim, não faz nada.
        if (registroDAO.existeRegistro(idRotina, hoje)) {
            return;
        }

        Rotina rotina = rotinaDAO.buscarPorId(idRotina);
        if (rotina == null) {
            return;
        }

        // 2. Ontem foi cumprido?
        boolean cumpriuOntem = registroDAO.existeRegistro(idRotina, ontem);

        if (cumpriuOntem || !rotina.isObrigatorio()) {
            // streak continua (seja porque ontem foi cumprido,
            // seja porque esse hábito não é obrigatório e pular não quebra)
            rotina.setStreakAtual(rotina.getStreakAtual() + 1);
        } else {
            // quebrou o streak, recomeça do 1
            rotina.setStreakAtual(1);
        }

        // Atualiza recorde, se for o caso
        if (rotina.getStreakAtual() > rotina.getMelhorStreak()) {
            rotina.setMelhorStreak(rotina.getStreakAtual());
        }

        // 3. Registra o cumprimento de hoje
        RegistroCumprimento registro = new RegistroCumprimento();
        registro.setIdRotina(idRotina);
        registro.setData(hoje);
        registro.setCumprido(true);
        registroDAO.inserir(registro);

        // 4. Salva o novo estado da rotina
        rotinaDAO.atualizar(rotina);
    }
}