package org.example.trabalho_final.model;

/**
 * Minha classe Goomba!
 * Representa aquele inimigo clássico que anda de um lado pro outro.
 * Ela herda direto de Enemy (que por sua vez herda de Entity), então já nasce com vida e física.
 */
public class Goomba extends Enemy {

    // Construtor: posiciona o Goomba na tela (x, y), define o tamanho (32x32) e 1 de vida (hp = 1)
    public Goomba(double x, double y) {
        super(x, y, 32, 32, 1);
    }

    // Sobrescrita do update (polimorfismo): controla o movimento e a patrulha dele na fase
    @Override
    public void update() {
        x += velX; // Move horizontalmente
        y += velY; // Aplica gravidade/velocidade vertical se houver

        // Limites de patrulha: se bater nas paredes invisíveis da fase, inverte a direção (anda pro outro lado)
        if (x <= 30 || x >= 940) {
            velX *= -1;
        }
    }

    // Sobrescrita do que acontece quando o Goomba encosta no Mario
    @Override
    public void onPlayerCollision(Mario player) {
        // Se o Mario estiver caindo em cima dele (pulando na cabeça), o Goomba morre e o Mario ganha impulso e pontos!
        if (player.getVelY() > 0 && (player.getY() + player.getHeight() - player.getVelY()) <= y + 14) {
            this.active = false; // Mata o Goomba
            player.setVelY(-9.5); // Dá um pulinho de impulso pra cima
            player.addScore(200);  // Ganha 200 pontos no placar
        } else {
            // Se encostou de lado ou por baixo, o Mario toma dano!
            player.takeDamage();
        }
    }
}