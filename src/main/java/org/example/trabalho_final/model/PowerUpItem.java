package org.example.trabalho_final.model;

/**
 * Minha classe PowerUpItem!
 * Representa os itens de poder espalhados pelas fases (como o "SUPER" e o "FIRE").
 * Ela herda de Entity e usa um pouco de matemática (função seno) para dar
 * aquele efeito visual de flutuação suave para cima e para baixo.
 */
public class PowerUpItem extends Entity {
    private final String type; // Define qual é o poder do item ("SUPER" ou "FIRE")
    private double bobAngle = 0; // Ângulo usado para calcular a flutuação trigonométrica

    // Construtor: posiciona o item na tela (x,y) e define seu tamanho padrão (24x24)
    public PowerUpItem(double x, double y, String type) {
        super(x, y, 24, 24);
        this.type = type;
    }

    // Sobrescrita do update (polimorfismo): faz o item ficar flutuando suavemente na tela
    @Override
    public void update() {
        bobAngle += 0.08;
        y += Math.sin(bobAngle) * 0.4; // Movimento harmônico leve para dar vida ao item colecionável
    }

    // Getter para saber qual é o tipo do poder
    public String getType() { return type; }
}