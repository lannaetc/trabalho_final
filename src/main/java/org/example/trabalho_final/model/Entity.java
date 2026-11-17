package org.example.trabalho_final.model;

import javafx.geometry.BoundingBox;

/**
 * Minha classe abstrata Entity (Entidade)!
 * Ela é o molde-mãe de absolutamente tudo o que se mexe ou existe no jogo:
 * o Mario, os inimigos, as plataformas, os itens. Como cada um tem coordenadas (x,y)
 * e tamanhos parecidos, coloco tudo aqui pra evitar código repetido (Herança pura!).
 */
public abstract class Entity {
    protected double x, y;          // Posição atual na tela
    protected double velX, velY;    // Velocidade horizontal e vertical (pra física e gravidade)
    protected double width, height; // Largura e altura da entidade
    protected boolean active = true; // Se tá ativo no jogo ou se já "morreu"/sumiu

    // Construtor: toda entidade nasce com uma posição e um tamanho definidos
    public Entity(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Método abstrato! Como cada coisa se atualiza de um jeito (o Mario anda, o inimigo patrulha),
    // obrigo todas as classes filhas a implementarem o seu próprio update().
    public abstract void update();

    // O segredo da colisão AABB (Axis-Aligned Bounding Box)!
    // Crio uma caixinha delimitadora usando a geometria nativa do JavaFX para checar se encostou em outra coisa.
    public BoundingBox getBounds() {
        return new BoundingBox(x, y, width, height);
    }

    // Getters e Setters: encapsulamento total dos atributos protegidos
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVelX() { return velX; }
    public double getVelY() { return velY; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setVelX(double velX) { this.velX = velX; }
    public void setVelY(double velY) { this.velY = velY; }
}