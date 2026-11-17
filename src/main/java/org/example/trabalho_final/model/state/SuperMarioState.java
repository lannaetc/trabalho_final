package org.example.trabalho_final.model.state;

import org.example.trabalho_final.model.Mario;

/**
 * Estado intermediário do Mario (Super Mario), usando o poderoso
 * "Incensário Veredicto"! Implementa a interface MarioState e
 * traz um pulo mais forte e armas com maior dano e alcance.
 */
public class SuperMarioState implements MarioState {

    @Override
    public void handleJump(Mario mario) {
        // Pulo um pouco mais forte do que na forma base, mas menor que o Fire
        mario.setVelY(-13.8);
    }

    @Override
    public void handleDamage(Mario mario) {
        // Tomou dano no modo Super? Ele não morre na hora:
        // recua pro estado Small (forma base) e ganha um tempinho de invulnerabilidade (90 frames)
        mario.changeState(new SmallMarioState());
        mario.setInvulnTimer(90);
    }

    @Override
    public String getStateName() {
        // Nome da arma pesada no HUD
        return "INCENSÁRIO VEREDICTO";
    }

    @Override
    public int getWeaponDamage() {
        // Dano mais alto porque o incensário bate forte!
        return 4;
    }

    @Override
    public double getAttackRange() {
        // Alcance da arma maior por causa da corrente do incensário
        return 105.0;
    }
}