package org.example.trabalho_final.model;

/**
 * Minha classe Projectile!
 * Herda de Entity e cuida de todos os projéteis disparados na tela:
 * seja as magias do Mario (fogo, ondas, cortes de espada) ou os ataques
 * disparados pelos chefões de cada reino.
 */
public class Projectile extends Entity {
    private int life = 100;          // Tempo de vida do projétil (para ele sumir depois de um tempo se não acertar nada)
    private final boolean fromPlayer; // Define se o tiro veio do Mario (true) ou de um chefe (false)
    private final String style;      // O estilo visual/tipo do projétil ("FIRE", "WAVE", "BOSS", etc.)

    // Construtor: posiciona o projétil, define velocidades (velX, velY), origem e estilo
    public Projectile(double x, double y, double velX, double velY, boolean fromPlayer, String style) {
        super(x, y, fromPlayer ? 16 : 20, fromPlayer ? 16 : 20); // Tamanho dinâmico dependendo de quem atirou
        this.velX = velX;
        this.velY = velY;
        this.fromPlayer = fromPlayer;
        this.style = style;
    }

    // Sobrescrita do update (polimorfismo): move o projétil e controla o tempo de vida/limites da tela
    @Override
    public void update() {
        x += velX;
        y += velY;
        life--; // Vai esgotando o tempo de vida do tiro

        // Se o tiro passar muito da borda da tela ou acabar o tempo de vida, ele desativa (some)
        if (life <= 0 || x < -20 || x > 1020 || y < -20 || y > 640) {
            active = false;
        }
    }

    // Getters para verificar quem disparou e qual é o estilo do projétil
    public boolean isFromPlayer() { return fromPlayer; }
    public String getStyle() { return style; }
}