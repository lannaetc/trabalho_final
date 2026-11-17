package org.example.trabalho_final.model;

/**
 * Minha classe Platform! Ela herda de Entity e serve para criar o chão e as plataformas
 * onde o Mario e os inimigos pisam. O mais legal é que ela suporta movimento harmônico
 * usando funções trigonométricas (Math.sin) para fazer as plataformas flutuarem e se moverem sozinhas!
 */
public class Platform extends Entity {
    private final double startY;   // Posição inicial Y (referência pra oscilação)
    private final double moveRange; // O quanto ela se movimenta para cima e para baixo (amplitude)
    private final double moveSpeed; // Velocidade do movimento
    private double phase;          // Fase da onda trigonométrica

    // Construtor simples: cria uma plataforma estática (parada)
    public Platform(double x, double y, double width, double height) {
        this(x, y, width, height, 0, 0, 0);
    }

    // Construtor completo: cria uma plataforma móvel usando matemática e trigonometria!
    public Platform(double x, double y, double width, double height, double moveRange, double moveSpeed, double phase) {
        super(x, y, width, height);
        this.startY = y;
        this.moveRange = moveRange;
        this.moveSpeed = moveSpeed;
        this.phase = phase;
    }

    // Sobrescrita do update (polimorfismo): se tiver range de movimento, calcula a nova posição Y usando seno
    @Override
    public void update() {
        if (moveRange > 0) {
            phase += moveSpeed;
            // Movimento harmônico suave usando Math.sin (Matemática Aplicada pura no jogo!)
            y = startY + Math.sin(phase) * moveRange;
        }
    }
}