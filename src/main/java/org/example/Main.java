package org.example;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        HabitoDAO habitoDAO = new HabitoDAO();
        char operacao;

        while (true) {

            System.out.println("\n========= APP DE ROTINA =========");
            System.out.println("1 - Cadastrar hábito");
            System.out.println("2 - Listar hábitos ativos");
            System.out.println("3 - Pausar hábito");
            System.out.println("4 - Sair");
            System.out.print("Digite uma opção: ");

            operacao = scanner.nextLine().trim().charAt(0);

            switch (operacao) {

                case '1':

                    Habito habito = new Habito();

                    System.out.print("Nome do hábito: ");
                    habito.setNome(scanner.nextLine());

                    System.out.print("Frequência (ex: diario): ");
                    habito.setFrequencia(scanner.nextLine());

                    habitoDAO.inserir(habito);

                    break;

                case '2':

                    ArrayList<Habito> habitos = habitoDAO.listarAtivos();

                    if (habitos.isEmpty()) {
                        System.out.println("Nenhum hábito cadastrado ainda.");
                    } else {
                        System.out.println("\n===== HÁBITOS ATIVOS =====");
                        for (Habito h : habitos) {
                            System.out.println(h.getIdHabito() + " - " + h.getNome()
                                    + " (" + h.getFrequencia() + ") desde " + h.getDataCriacao());
                        }
                    }

                    break;

                case '3':

                    System.out.print("Digite o ID do hábito a pausar: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    habitoDAO.pausar(id);

                    break;

                case '4':

                    System.out.println("Até mais!");
                    scanner.close();
                    return;

                default:

                    System.out.println("Opção inválida!");
            }
        }
    }
}