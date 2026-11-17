package org.example.trabalho_final.model;

import java.io.*;

/**
 * Minha classe ScoreManager!
 * Gerencia a pontuação máxima (High Score) do jogo salvando tudo num arquivo de texto ("recorde_crimson_sakura.txt").
 * Aqui eu aplico conceitos de I/O em Java, leitura/escrita com Buffer e tratamento seguro de exceções.
 */
public class ScoreManager {
    private static final String FILE_NAME = "recorde_crimson_sakura.txt"; // Nome do arquivo do recorde

    // Carrega o recorde salvo no computador do jogador
    public static int loadHighScore() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return 0; // Se o arquivo ainda não existe, o recorde começa em 0
        }

        // Uso o BufferedReader com try-with-resources para garantir que o arquivo vai fechar sozinho depois de ler
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null && !line.isBlank()) {
                return Integer.parseInt(line.trim()); // Converte o texto salvo no arquivo para número inteiro
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Aviso ao ler recorde (usando 0): " + e.getMessage()); // Tratamento de erro caso o arquivo esteja corrompido
        }
        return 0;
    }

    // Salva a nova pontuação máxima apenas se ela for maior que o recorde anterior
    public static void saveHighScore(int newScore) {
        int currentHigh = loadHighScore();
        if (newScore <= currentHigh) return; // Se a pontuação nova for menor ou igual, não faz nada

        // BufferedWriter com try-with-resources para gravar o recorde com segurança no disco
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(String.valueOf(newScore)); // Escreve o novo recorde em formato de texto
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo de recorde: " + e.getMessage()); // Tratamento de erro de escrita I/O
        }
    }
}