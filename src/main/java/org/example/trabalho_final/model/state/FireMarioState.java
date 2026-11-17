package org.example.trabalho_final.model.state;

import org.example.trabalho_final.model.Mario;

/**
 * Estado do Fire Mario (ou melhor, Adagas Centella / Raio!)
 * Aqui o herói tá no poder máximo, com ataque forte e alcance maior.
 * Implemento a interface MarioState pra garantir que ele siga o padrão de estados.
 */
public class FireMarioState implements MarioState {

    @Override
    public void handleJump(Mario mario) {
        // Pulo super leve e ágil quando tá nesse estado
        mario.setVelY(-14.2);
    }

    @Override
    public void handleDamage(Mario mario) {
        // Tomou dano no modo supremo? Ele não morre direto:
        // recua pro estado Super e ganha tempinho de invulnerabilidade (90 frames)
        mario.changeState(new SuperMarioState());
        mario.setInvulnTimer(90);
    }

    @Override
    public String getStateName() {
        // Nome épico do poder no jogo
        return "ADAGAS CENTELLA (RAIO)";
    }

    @Override
    public int getWeaponDamage() {
        // Dano alto porque tá na forma suprema!
        return 3;
    }

    @Override
    public double getAttackRange() {
        // Alcance da arma bem maior
        return 88.0;
    }
}