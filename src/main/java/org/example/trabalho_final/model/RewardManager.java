package org.example.trabalho_final.model;

import java.io.File;
import java.io.IOException;

/**
 * Minha classe RewardManager!
 * Responsável por salvar a conquista do jogador quando ele zera o jogo.
 * Aplica os conceitos de I/O em Java e tratamento de exceções (try-catch)
 * criando um arquivo local de salvamento ("divine_sakura_conquest.dat").
 */
public class RewardManager {
    private static final String REWARD_FILE = "divine_sakura_conquest.dat"; // Nome do arquivo de conquista

    // Salva o progresso criando o arquivo no computador quando o jogo é finalizado
    public static void saveGameCompleted() {
        try {
            File file = new File(REWARD_FILE);
            file.createNewFile(); // Cria o arquivo de salvamento da vitória
        } catch (IOException e) {
            e.printStackTrace(); // Tratamento obrigatório de exceção I/O exigido pelo Java
        }
    }

    // Checa se o jogador já zerou o jogo antes (verificando se o arquivo existe)
    public static boolean hasBeatenGame() {
        File file = new File(REWARD_FILE);
        return file.exists(); // Retorna true se o arquivo existir no disco
    }
}