package org.example;

import java.time.LocalDate;

public class EstudoService {

    private final EstudoDAO estudoDAO = new EstudoDAO();
    private final RegistroEstudoDiarioDAO registroDAO = new RegistroEstudoDiarioDAO();

    public void marcarEstudado(int idEstudo) {
        LocalDate hoje = LocalDate.now();
        LocalDate ontem = hoje.minusDays(1);

        if (registroDAO.existeRegistro(idEstudo, hoje)) {
            return;
        }

        Estudo estudo = estudoDAO.buscarPorId(idEstudo);
        if (estudo == null) {
            return;
        }

        boolean estudouOntem = registroDAO.existeRegistro(idEstudo, ontem);

        if (estudouOntem) {
            estudo.setStreakAtual(estudo.getStreakAtual() + 1);
        } else {
            estudo.setStreakAtual(1);
        }

        if (estudo.getStreakAtual() > estudo.getMelhorStreak()) {
            estudo.setMelhorStreak(estudo.getStreakAtual());
        }

        registroDAO.inserir(idEstudo, hoje);
        estudoDAO.atualizarStreak(estudo);
    }
}
