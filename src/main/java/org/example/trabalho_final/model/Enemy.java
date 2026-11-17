package org.example.trabalho_final.model;

/**
 * Minha classe abstrata Enemy (Inimigo)!
 * Como todo inimigo tem vida, tamanho e anda pela fase, criei ela estendendo a Entity.
 * Usei abstract porque ela serve de molde base, então ninguém instancia um "Enemy puro",
 * mas sim os filhos dela (Goomba, Koopa, Chefão).
 */
public abstract class Enemy extends Entity {
    protected int hp;             // Vida atual do inimigo
    protected int maxHp;          // Vida máxima (bom pra desenhar a barrinha de HP do chefe)
    protected int hitFlashTimer = 0; // Timer pro inimigo piscar quando leva um golpe

    // Construtor: inicializa a posição (x,y), tamanho, vida e já joga o inimigo andando pra esquerda (velX = -1.8)
    public Enemy(double x, double y, double width, double height, int hp) {
        super(x, y, width, height);
        this.hp = hp;
        this.maxHp = hp;
        this.velX = -1.8;
    }

    // Método abstrato! Obrigo as classes filhas (Goomba e Koopa) a implementarem
    // o que acontece quando o inimigo encosta no Mario (polimorfismo!).
    public abstract void onPlayerCollision(Mario player);

    // Quando o inimigo toma porrada da espada do Mario
    public void takeHit(int damage) {
        this.hp -= damage;
        this.hitFlashTimer = 8; // Deixa o inimigo piscando por 8 frames pra dar feedback visual
        if (this.hp <= 0) {
            this.hp = 0;
            this.active = false; // Se o HP zerou, o inimigo morre e sai de cena (some do jogo)
        }
    }

    // Getters pra consultar os dados do inimigo (muito útil na View pra desenhar a barra de vida)
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getHitFlashTimer() { return hitFlashTimer; }
}