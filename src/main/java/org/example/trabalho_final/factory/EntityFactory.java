package org.example.trabalho_final.factory;

import org.example.trabalho_final.model.Enemy;
import org.example.trabalho_final.model.Goomba;
import org.example.trabalho_final.model.Koopa;

/**
 * Minha fábrica de entidades (Factory Method)!
 * Decidi usar esse padrão de projeto pra não ficar espalhando 'new Goomba()' ou 'new Koopa()'
 * por todo o código do jogo. Centralizo tudo aqui: passo o tipo em texto e ela me devolve o objeto pronto.
 */
public class EntityFactory {

    // Método estático que cria e retorna um inimigo com base na string que a gente mandar
    public static Enemy createEnemy(String type, double x, double y) {
        // Garante que não vai dar erro se digitarem minúsculo ou maiúsculo
        String upper = type.toUpperCase();

        if ("GOOMBA".equals(upper)) {
            return new Goomba(x, y); // Cria um Goomba comum na coordenada (x, y)
        } else if ("KOOPA".equals(upper)) {
            return new Koopa(x, y, false); // Cria um Koopa normal
        } else if ("BOSS".equals(upper)) {
            return new Koopa(x, y, true); // Cria o Chefão da fase! (o último parâmetro 'true' avisa que é o boss)
        }

        // Se passarem alguma string errada, o Java estoura uma exceção pra avisar
        throw new IllegalArgumentException("Tipo de inimigo desconhecido: " + type);
    }
}