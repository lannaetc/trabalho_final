package org.example.trabalho_final.model;

import org.example.trabalho_final.model.state.FireMarioState;
import org.example.trabalho_final.model.state.MarioState;
import org.example.trabalho_final.model.state.SmallMarioState;
import org.example.trabalho_final.model.state.SuperMarioState;

/**
 * Minha classe Mario! O protagonista do jogo.
 * Ela herda de Entity e aplica o Design Pattern State (através do atributo currentState)
 * para gerenciar as transformações de poder (Small, Super, Fire).
 */
public class Mario extends Entity {
    private MarioState currentState; // O estado atual do Mario (Padrão State)
    private int lives = 3;           // Vidas iniciais
    private int score = 0;           // Pontuação do jogador
    private int invulnTimer = 0;     // Tempo de invulnerabilidade após tomar dano (pisca na tela)
    private int attackTimer = 0;     // Controle da animação/cooldown do ataque
    private boolean onGround = false;
    private boolean underwater = false; // Se está na fase subaquática (fase 2)
    private int facing = 1;          // Direção que o Mario está olhando (1 = direita, -1 = esquerda)
    private boolean creativeMode = false; // Nosso modo criativo secreto (ativado pelo cheat "ALANA")

    // Construtor: nasce na posição (x,y), tamanho 36x52 e começa no estado base (Small)
    public Mario(double x, double y) {
        super(x, y, 36, 52);
        this.currentState = new SmallMarioState();
    }

    // Sobrescrita do update (polimorfismo): atualiza timers, física, posição e limites da tela
    @Override
    public void update() {
        if (invulnTimer > 0) invulnTimer--;
        if (attackTimer > 0) attackTimer--;

        x += velX;
        y += velY;

        // Limites horizontais para o Mario não sair da tela
        if (x < 25) {
            x = 25;
            velX = 0;
        } else if (x > 965 - width) {
            x = 965 - width;
            velX = 0;
        }

        // Limite do chão da fase
        if (y > 473) {
            y = 473;
            velY = 0;
            onGround = true;
        }
    }

    // Lógica de pulo: se estiver no modo criativo, voa/pula livre; na água flutua diferente; no chão dá o pulo normal
    public void jump() {
        if (creativeMode) {
            velY = underwater ? -7.0 : -13.5;
            onGround = false;
            return;
        }

        if (underwater) {
            velY = -7.5;
            onGround = false;
        } else if (onGround) {
            velY = -13.5;
            onGround = false;
        }
    }

    // Aciona o ataque da espada/arma se o cooldown permitir
    public boolean triggerAttack() {
        if (attackTimer <= 0) {
            attackTimer = 18; // Duração dos frames do ataque
            return true;
        }
        return false;
    }

    // O que acontece quando o Mario toma dano (respeita o modo criativo e invulnerabilidade)
    public void takeDamage() {
        if (creativeMode || invulnTimer > 0) return;

        // Delega o dano de acordo com o estado atual (Fire vira Super, Super vira Small, Small perde vida)
        if (currentState instanceof FireMarioState) {
            currentState = new SuperMarioState();
            invulnTimer = 90;
        } else if (currentState instanceof SuperMarioState) {
            currentState = new SmallMarioState();
            invulnTimer = 90;
        } else {
            loseLife();
        }
    }

    // Perde uma vida e ganha tempo de invencibilidade temporária
    public void loseLife() {
        if (creativeMode) return;
        lives--;
        invulnTimer = 120;
        if (lives > 0) {
            currentState = new SmallMarioState(); // Volta à forma base se ainda tiver vidas
        } else {
            lives = 0; // Game Over
        }
    }

    // Método essencial do padrão State: troca o estado atual do herói em tempo de execução
    public void changeState(MarioState newState) {
        this.currentState = newState;
    }

    public boolean canShootFire() {
        return currentState instanceof FireMarioState;
    }

    public void setInvulnTimer(int timer) {
        this.invulnTimer = timer;
    }

    // Getters e Setters de todos os atributos do player
    public MarioState getState() { return currentState; }
    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }
    public int getScore() { return score; }
    public void addScore(int pts) { this.score += pts; }
    public int getInvulnTimer() { return invulnTimer; }
    public int getAttackTimer() { return attackTimer; }
    public boolean isOnGround() { return onGround; }
    public void setOnGround(boolean onGround) { this.onGround = onGround; }
    public boolean isUnderwater() { return underwater; }
    public void setUnderwater(boolean underwater) { this.underwater = underwater; }
    public int getFacing() { return facing; }
    public void setFacing(int facing) { this.facing = facing; }
    public boolean isCreativeMode() { return creativeMode; }
    public void setCreativeMode(boolean creativeMode) { this.creativeMode = creativeMode; }
}