package org.example.trabalho_final.model.state;

import org.example.trabalho_final.model.Mario;

/**
 * Minha Interface MarioState!
 * Criei ela para aplicar o Design Pattern State. Basicamente, ela é o "contrato"
 * que obriga todos os estados do herói (Small, Super, Fire) a terem esses mesmos métodos,
 * mas cada um implementa do seu jeito (polimorfismo puro!).
 */
public interface MarioState {

    // Cada estado define como o Mario pula (altura do pulo diferente em cada forma)
    void handleJump(Mario mario);

    // O que acontece quando o Mario toma dano em cada estado específico
    void handleDamage(Mario mario);

    // Retorna o nome bonitinho do estado/arma atual pra aparecer no HUD
    String getStateName();

    // Quanto de dano o ataque do Mario causa nesse estado
    int getWeaponDamage();

    // Qual é o alcance do ataque corpo a corpo ou arma nessa forma
    double getAttackRange();
}