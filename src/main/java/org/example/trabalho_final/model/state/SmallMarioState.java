package org.example.trabalho_final.model.state;

import org.example.trabalho_final.model.Mario;

/**
 * Estado inicial do Mario (Small / Formato base), mas que aqui equipa a espada
 * clássica inspirada em Blasphemous ("Espada Mea Culpa")!
 * Implementa a interface MarioState como manda o figurino do padrão State.
 */
public class SmallMarioState implements MarioState {

    @Override
    public void handleJump(Mario mario) {
        // Pulo padrão menorzinho porque ele tá na forma base
        mario.setVelY(-12.6);
    }

    @Override
    public void handleDamage(Mario mario) {
        // Como ele já tá na forma base (pequena), se tomar dano aqui... perde vida direto! Game over ou menos 1 coração.
        mario.loseLife();
    }

    @Override
    public String getStateName() {
        // Nome da arma/estado base no HUD
        return "ESPADA MEA CULPA";
    }

    @Override
    public int getWeaponDamage() {
        // Dano base da espada normal
        return 2;
    }

    @Override
    public double getAttackRange() {
        // Alcance da espada no modo normal
        return 75.0;
    }
}