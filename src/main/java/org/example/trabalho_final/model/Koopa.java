package org.example.trabalho_final.model;

import javafx.geometry.BoundingBox;

/**
 * Minha classe Koopa! Ela é super completa porque serve tanto pro inimigo
 * tartaruga normal (que entra no casco e pode ser chutado) quanto para os
 * Chefões (*Bosses*) de cada uma das 4 fases do jogo!
 */
public class Koopa extends Enemy {
    private boolean inShell = false;
    private boolean shellKicked = false;
    private int shellBounces = 0;
    private boolean isBoss;
    private int bossStage;
    private int aiTimer = 0;
    private boolean wantsToShoot = false;
    private boolean firingTractorBeam = false;
    private String bossTitle = "";
    private String attackPattern = "NORMAL";
    private double anchorY = 180;

    // Construtor auxiliar para koopa comum ou chefe padrão
    public Koopa(double x, double y, boolean isBoss) {
        this(x, y, isBoss, 1);
    }

    // Construtor principal: define tamanho, vida e o comportamento de acordo com o estágio do chefe
    public Koopa(double x, double y, boolean isBoss, int stage) {
        super(x, y,
                isBoss ? (stage == 1 ? 56 : (stage == 2 ? 70 : (stage == 3 ? 82 : 110))) : 38,
                isBoss ? (stage == 1 ? 82 : (stage == 2 ? 86 : (stage == 3 ? 84 : 68))) : 48,
                isBoss ? (16 + stage * 7) : 3);
        this.isBoss = isBoss;
        this.bossStage = stage;
        this.velX = isBoss ? -2.8 : -1.7;

        // Configuro os títulos e padrões de ataque específicos para cada um dos 4 chefões do jogo!
        if (isBoss) {
            if (stage == 1) {
                this.bossTitle = "KENSHIN, O SHOGUN DAS CEREJEIRAS (SAMURAI)";
                this.attackPattern = "SAMURAI";
            } else if (stage == 2) {
                this.bossTitle = "POSEIDON, O SOBERANO DE ATLÂNTIDA";
                this.attackPattern = "POSEIDON";
                this.y = 190;
            } else if (stage == 3) {
                this.bossTitle = "BARÃO CUPCAKE, O SOBERANO DO REINO DOCE";
                this.attackPattern = "CANDY_CUPCAKE";
                this.y = 400;
            } else {
                this.bossTitle = "ZORGON, O IMPERADOR ALIENÍGENA (NAVE MÃE)";
                this.attackPattern = "ALIEN_UFO";
                this.y = 135;
            }
        }
    }

    // Atualização de quadros (polimorfismo sobrescrevendo o update da Entity/Enemy)
    @Override
    public void update() {
        if (hitFlashTimer > 0) hitFlashTimer--;
        aiTimer++;

        // Se for chefe, desvia para a IA avançada dos chefões
        if (isBoss) {
            updateBossAI();
            return;
        }

        // Lógica do casco rolando e rebatendo nas paredes
        if (inShell && shellKicked) {
            x += velX;
            y += velY;

            if (x <= 15 || x >= (985 - width)) {
                shellBounces++;
                if (shellBounces >= 3) {
                    active = false; // Se bateu 3 vezes, o casco some
                } else {
                    velX *= -1; // Inverte direção na parede
                }
            }
            return;
        }

        // Movimento normal do Koopa andando
        x += velX;
        y += velY;
        if (x <= 25 || x >= (965 - width)) {
            velX *= -1;
        }
    }

    // Inteligência Artificial exclusiva dos Chefões (cada um com seu padrão de ataque)
    private void updateBossAI() {
        if ("SAMURAI".equals(attackPattern)) {
            // Chefe 1: Samurai correndo rápido e pulando
            double speed = 3.2;
            if (aiTimer % 110 == 0) {
                velX = (velX >= 0 ? 1 : -1) * 11.5;
                wantsToShoot = true;
            }
            if (aiTimer % 165 == 0 && Math.abs(velY) < 0.5) {
                velY = -13.5;
            }
            if (aiTimer % 75 == 0) wantsToShoot = true;
            if (Math.abs(velX) > speed) velX *= 0.94;

            x += velX;
            y += velY;

        } else if ("POSEIDON".equals(attackPattern)) {
            // Chefe 2: Poseidon flutuando com ondas senoidais na água
            double targetY = anchorY + Math.sin(aiTimer * 0.04) * 115;
            if (aiTimer % 160 > 125) {
                velX = (velX >= 0 ? 1 : -1) * 7.2;
            } else if (Math.abs(velX) > 2.8) {
                velX *= 0.94;
            }

            y += (targetY - y) * 0.07;
            velY = 0;

            if (aiTimer % 68 == 0) wantsToShoot = true;
            x += velX;

        } else if ("CANDY_CUPCAKE".equals(attackPattern)) {
            // Chefe 3: Barão Cupcake saltitando no reino de doces
            if (aiTimer % 85 == 0 && Math.abs(velY) < 0.5) {
                velY = -13.0;
                velX = (velX >= 0 ? 1 : -1) * 5.2;
                wantsToShoot = true;
            }
            if (aiTimer % 60 == 0) wantsToShoot = true;
            if (Math.abs(velX) > 3.2) velX *= 0.96;

            x += velX;
            y += velY;

        } else {
            // Chefe 4: Imperador Alienígena na Nave Mãe (com modo "furioso/overdrive" quando o HP cai pela metade!)
            boolean overdrive = isAwakened();
            double speedMult = overdrive ? 1.55 : 1.0;

            double targetY = 135 + Math.sin(aiTimer * 0.045) * 55;
            y += (targetY - y) * 0.08;
            velY = 0;

            int cycle = aiTimer % 200;
            if (cycle > 135 && cycle < 190) {
                firingTractorBeam = true; // Raio trator alienígena ligado!
                x += velX * 0.45;
            } else {
                firingTractorBeam = false;
                x += velX * speedMult;
            }

            if (aiTimer % (overdrive ? 42 : 62) == 0) {
                wantsToShoot = true;
            }
        }

        // Limites de tela pros chefes não fugirem da arena
        if (x <= 25) {
            x = 25;
            velX = Math.abs(velX);
        } else if (x >= (965 - width)) {
            x = 965 - width;
            velX = -Math.abs(velX);
        }
    }

    // Modo furioso do chefão alienígena (quando o HP fica menor ou igual à metade)
    public boolean isAwakened() {
        return isBoss && bossStage == 4 && hp <= (maxHp / 2);
    }

    public boolean isFlyingBoss() {
        if (!isBoss) return false;
        return "POSEIDON".equals(attackPattern) || "ALIEN_UFO".equals(attackPattern);
    }

    // O que acontece quando o Mario colide com o Koopa ou com o Chefe
    @Override
    public void onPlayerCollision(Mario player) {
        boolean stomped = player.getVelY() > 0 && (player.getY() + player.getHeight() - player.getVelY()) <= y + 24;

        if (stomped) {
            player.setVelY(player.isUnderwater() ? -6.5 : -11.5);
            if (isBoss) {
                takeHit(2); // Dar stomp no chefe arranca 2 de HP dele!
                player.addScore(500);
                if (!active) player.addScore(5000);
            } else {
                // Se for koopa normal, vira casco ou chuta o casco
                if (!inShell) {
                    inShell = true;
                    shellKicked = true;
                    height = 28;
                    velX = player.getFacing() * 11.5;
                    player.addScore(300);
                } else {
                    shellKicked = true;
                    velX = player.getFacing() * 11.5;
                    player.addScore(500);
                }
            }
        } else {
            if (!isBoss && inShell && !shellKicked) {
                shellKicked = true;
                velX = player.getFacing() * 11.5;
            } else if (!isBoss && inShell && shellKicked) {
                // Casco em movimento não machuca se estiver sendo chutado estrategicamente
            } else {
                player.takeDamage(); // Mario toma dano se encostar errado
            }
        }
    }

    // Métodos auxiliares de controle de tiro e getters dos chefes
    public boolean consumeShootSignal() {
        if (wantsToShoot) {
            wantsToShoot = false;
            return true;
        }
        return false;
    }

    public boolean isInShell() { return inShell; }
    public boolean isShellKicked() { return shellKicked; }
    public boolean isBoss() { return isBoss; }
    public int getBossStage() { return bossStage; }
    public String getBossTitle() { return bossTitle; }
    public String getAttackPattern() { return attackPattern; }
    public boolean isFiringTractorBeam() { return firingTractorBeam; }
}