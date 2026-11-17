package org.example.trabalho_final.model;

import org.example.trabalho_final.factory.EntityFactory;
import org.example.trabalho_final.model.state.FireMarioState;
import org.example.trabalho_final.model.state.SmallMarioState;
import org.example.trabalho_final.model.state.SuperMarioState;

import java.util.ArrayList;
import java.util.List;

/**
 * Minha classe Level (O Coração da Fase / Model Principal)!
 * Aqui eu gerencio a lista de todas as entidades do jogo (Mario, inimigos, plataformas, projéteis),
 * aplico a gravidade que muda em cada reino, monto a fase dinamicamente e checo todas as colisões (AABB)!
 */
public class Level {
    private List<Entity> entities; // Lista contendo todas as entidades ativas na fase
    private double gravity;
    private Mario player;
    private int stageNumber;
    private boolean bossSpawned = false;
    private boolean stageCleared = false;
    private int bossBannerTimer = 0;
    private int screenShake = 0; // Controle de tremor de tela (efeito visual maneiro)
    private int hitCooldown = 0;

    // Construtor: define a gravidade de acordo com o número do reino/fase e monta o cenário
    public Level(int stageNumber) {
        this.stageNumber = stageNumber;

        // Gravidade personalizada por fase (Fase 2 é subaquática com gravidade baixa, Fase 4 é mais pesada)
        if (stageNumber == 2) {
            this.gravity = 0.15;
        } else if (stageNumber == 4) {
            this.gravity = 0.34;
        } else {
            this.gravity = 0.58;
        }

        this.entities = new ArrayList<>();
        buildStage(stageNumber);
    }

    // Constrói o layout das plataformas, itens e inimigos de cada fase usando a Factory
    private void buildStage(int stage) {
        this.player = new Mario(90, stage == 2 ? 240 : 420);
        this.player.setUnderwater(stage == 2);
        entities.add(player);

        // Chão padrão da fase
        entities.add(new Platform(0, 525, 1000, 95));

        if (stage == 1) {
            // Fase 1: Reino das Cerejeiras (Plataformas estáticas e inimigos iniciais)
            entities.add(new Platform(130, 405, 210, 18));
            entities.add(new Platform(405, 295, 210, 18));
            entities.add(new Platform(680, 405, 210, 18));

            entities.add(new PowerUpItem(498, 250, "SUPER"));
            entities.add(EntityFactory.createEnemy("GOOMBA", 420, 493));
            entities.add(EntityFactory.createEnemy("GOOMBA", 740, 373));
            entities.add(EntityFactory.createEnemy("KOOPA", 860, 477));

        } else if (stage == 2) {
            // Fase 2: Atlântida / Subaquática
            entities.add(new Platform(120, 390, 190, 16));
            entities.add(new Platform(380, 280, 190, 16));
            entities.add(new Platform(640, 370, 190, 16));

            entities.add(new PowerUpItem(195, 345, "SUPER"));
            entities.add(new PowerUpItem(710, 325, "FIRE"));

            entities.add(EntityFactory.createEnemy("GOOMBA", 280, 493));
            entities.add(EntityFactory.createEnemy("GOOMBA", 450, 245));
            entities.add(EntityFactory.createEnemy("KOOPA", 700, 335));
            entities.add(EntityFactory.createEnemy("KOOPA", 860, 477));

        } else if (stage == 3) {
            // Fase 3: Reino Doce (Plataformas móveis com trigonometria)
            entities.add(new Platform(110, 395, 185, 20, 35, 0.035, 0.0));
            entities.add(new Platform(400, 295, 200, 20, 30, 0.03, 1.5));
            entities.add(new Platform(705, 395, 185, 20, 35, 0.035, 3.1));

            entities.add(new PowerUpItem(488, 220, "FIRE"));
            entities.add(EntityFactory.createEnemy("GOOMBA", 250, 493));
            entities.add(EntityFactory.createEnemy("GOOMBA", 520, 493));
            entities.add(EntityFactory.createEnemy("KOOPA", 650, 477));
            entities.add(EntityFactory.createEnemy("KOOPA", 840, 477));

        } else {
            // Fase 4: Espaço Sideral / Último Reino
            entities.add(new Platform(95, 405, 195, 18, 25, 0.03, 0.0));
            entities.add(new Platform(390, 285, 220, 18, 20, 0.03, 2.0));
            entities.add(new Platform(710, 405, 195, 18, 25, 0.03, 4.0));

            entities.add(new PowerUpItem(180, 345, "SUPER"));
            entities.add(new PowerUpItem(785, 345, "FIRE"));
            entities.add(EntityFactory.createEnemy("GOOMBA", 320, 493));
            entities.add(EntityFactory.createEnemy("KOOPA", 500, 493));
            entities.add(EntityFactory.createEnemy("KOOPA", 680, 477));
            entities.add(EntityFactory.createEnemy("KOOPA", 860, 477));
        }
    }

    // O loop de atualização da fase: aplica gravidade, atualiza entidades e gerencia o surgimento do Chefe
    public void update() {
        if (bossBannerTimer > 0) bossBannerTimer--;
        if (screenShake > 0) screenShake--;
        if (hitCooldown > 0) hitCooldown--;

        List<Entity> newEntities = new ArrayList<>();

        for (Entity e : entities) {
            // Aplicação da física de gravidade em cada tipo de entidade
            if (e instanceof Mario) {
                double currentGravity = gravity;
                if (stageNumber == 2 && e.getVelY() > 2.5) {
                    currentGravity = 0.03; // Efeito de flutuação na água
                }
                e.setVelY(e.getVelY() + currentGravity);
            } else if (e instanceof Koopa k) {
                if (!k.isFlyingBoss()) {
                    e.setVelY(e.getVelY() + gravity);
                }
            } else if (e instanceof Enemy) {
                e.setVelY(e.getVelY() + gravity);
            }

            e.update(); // Atualiza a posição individual

            // Se o chefe atirar, gero os projéteis específicos de cada boss na tela
            if (e instanceof Koopa k && k.isActive() && k.isBoss() && k.consumeShootSignal()) {
                double dir = player.getX() < k.getX() ? -1 : 1;
                double spawnX = dir == -1 ? k.getX() - 22 : k.getX() + k.getWidth();
                double spawnY = k.getY() + k.getHeight() * 0.55;

                if ("SAMURAI".equals(k.getAttackPattern())) {
                    newEntities.add(new Projectile(spawnX, spawnY, dir * 8.5, 0, false, "KATANA_SLASH"));
                } else if ("POSEIDON".equals(k.getAttackPattern())) {
                    double dx = (player.getX() - spawnX);
                    double dy = (player.getY() - spawnY);
                    double dist = Math.max(1, Math.hypot(dx, dy));
                    newEntities.add(new Projectile(spawnX, spawnY, (dx / dist) * 7.2, (dy / dist) * 7.2, false, "TRIDENT_WAVE"));
                    newEntities.add(new Projectile(spawnX, spawnY + 18, dir * 6.0, 0.8, false, "WATER_ORB"));
                } else if ("CANDY_CUPCAKE".equals(k.getAttackPattern())) {
                    newEntities.add(new Projectile(spawnX, spawnY - 10, dir * 7.2, -3.2, false, "CONFETTI"));
                    newEntities.add(new Projectile(spawnX, spawnY, dir * 7.8, -1.2, false, "CONFETTI"));
                    newEntities.add(new Projectile(spawnX, spawnY + 8, dir * 7.2, 1.0, false, "CONFETTI"));
                    newEntities.add(new Projectile(spawnX, spawnY - 15, dir * 6.2, -4.5, false, "CONFETTI"));
                } else {
                    double centerX = k.getX() + k.getWidth() / 2.0;
                    double bottomY = k.getY() + k.getHeight();
                    double dx = (player.getX() - centerX);
                    double dy = (player.getY() - bottomY);
                    double dist = Math.max(1, Math.hypot(dx, dy));

                    newEntities.add(new Projectile(centerX, bottomY, (dx / dist) * 8.2, (dy / dist) * 8.2, false, "ALIEN_PLASMA"));
                    newEntities.add(new Projectile(centerX - 20, bottomY, (dx / dist) * 7.2 - 2.2, (dy / dist) * 7.2, false, "ALIEN_PLASMA"));
                    newEntities.add(new Projectile(centerX + 20, bottomY, (dx / dist) * 7.2 + 2.2, (dy / dist) * 7.2, false, "ALIEN_PLASMA"));
                }
            }
        }

        entities.addAll(newEntities);
        checkCollisions(); // Roda todas as colisões do jogo

        // Derrotou todos os inimigos comuns? Hora de spawnar o Chefão da fase!
        boolean anyEnemyLeft = entities.stream().anyMatch(e -> e instanceof Enemy && e.isActive());

        if (!anyEnemyLeft) {
            if (!bossSpawned) {
                bossSpawned = true;
                bossBannerTimer = 150; // Tempo do banner de aviso do chefe
                screenShake = 20;      // Treme a tela na entrada épica do boss
                entities.add(new Koopa(810, stageNumber == 2 || stageNumber == 4 ? 150 : 380, true, stageNumber));
            } else {
                stageCleared = true; // Se o chefe também morreu, limpas a fase!
            }
        }

        // Remove entidades inativas da memória (exceto o Mario)
        entities.removeIf(e -> !e.isActive() && !(e instanceof Mario));
    }

    // Sistema completo de colisões (Plataformas, Inimigos, Itens, Projéteis e Ataques)
    public void checkCollisions() {
        player.setOnGround(false);

        for (Entity e : entities) {
            if (!e.isActive() || e == player) continue;

            // Colisão com Plataformas
            if (e instanceof Platform p) {
                if (player.getBounds().intersects(p.getBounds())) {
                    if (player.getVelY() >= 0 && (player.getY() + player.getHeight() - player.getVelY()) <= p.getY() + 18) {
                        player.setY(p.getY() - player.getHeight());
                        player.setVelY(0);
                        player.setOnGround(true);
                    }
                }

                for (Entity other : entities) {
                    if (other instanceof Koopa k && k.isFlyingBoss()) continue;
                    if (other instanceof Enemy en && en.isActive() && en.getBounds().intersects(p.getBounds())) {
                        if (en.getVelY() >= 0 && (en.getY() + en.getHeight() - en.getVelY()) <= p.getY() + 18) {
                            en.setY(p.getY() - en.getHeight());
                            en.setVelY(0);
                        }
                    }
                }
            }

            // Colisão de cascos de tartaruga rebatendo em outros inimigos
            if (e instanceof Koopa shell && shell.isInShell() && shell.isShellKicked()) {
                for (Entity target : entities) {
                    if (target != shell && target instanceof Enemy victim && victim.isActive() && !isBossEntity(victim)) {
                        if (shell.getBounds().intersects(victim.getBounds())) {
                            victim.setActive(false);
                            player.addScore(800);
                            screenShake = 8;
                        }
                    }
                }
            }

            // Colisão entre o Mario e os Inimigos
            if (e instanceof Enemy enemy && player.getBounds().intersects(enemy.getBounds())) {
                enemy.onPlayerCollision(player);
            }

            // Coleta de itens de poder (Super e Fire)
            if (e instanceof PowerUpItem item && player.getBounds().intersects(item.getBounds())) {
                if ("SUPER".equals(item.getType())) {
                    player.changeState(new SuperMarioState());
                } else if ("FIRE".equals(item.getType())) {
                    player.changeState(new FireMarioState());
                }
                player.addScore(1000);
                item.setActive(false);
            }

            // Projéteis inimigos atingindo o Mario
            if (e instanceof Projectile proj && !proj.isFromPlayer() && player.getBounds().intersects(proj.getBounds())) {
                player.takeDamage();
                proj.setActive(false);
                screenShake = 10;
            }
        }

        // Ataques do Mario contra os inimigos (espadas e projéteis mágicos)
        double range = player.getState().getAttackRange();
        int dmg = player.getState().getWeaponDamage();

        for (Entity e : entities) {
            if (!(e instanceof Enemy enemy) || !enemy.isActive()) continue;

            if (player.getAttackTimer() > 0 && hitCooldown <= 0) {
                double atkX = player.getFacing() == 1 ? player.getX() + player.getWidth() - 10 : player.getX() - range + 10;
                if (enemy.getX() < atkX + range && enemy.getX() + enemy.getWidth() > atkX &&
                        Math.abs((enemy.getY() + enemy.getHeight() / 2.0) - (player.getY() + player.getHeight() / 2.0)) < 85) {
                    enemy.takeHit(dmg);
                    hitCooldown = 10;
                    screenShake = (player.getState() instanceof SuperMarioState) ? 10 : 5;
                    if (!enemy.isActive()) {
                        player.addScore(isBossEntity(enemy) ? 5000 : 400);
                    }
                }
            }

            for (Entity pEntity : entities) {
                if (pEntity instanceof Projectile p && p.isActive() && p.isFromPlayer() && p.getBounds().intersects(enemy.getBounds())) {
                    enemy.takeHit("WAVE".equals(p.getStyle()) ? 3 : 2);
                    p.setActive(false);
                    screenShake = 6;
                    if (!enemy.isActive()) {
                        player.addScore(isBossEntity(enemy) ? 5000 : 400);
                    }
                }
            }
        }
    }

    private boolean isBossEntity(Entity e) {
        return (e instanceof Koopa k) && k.isBoss();
    }

    // Executa o ataque especial do Mario disparando projéteis personalizados por estado
    public void performSpecialAttack() {
        if (!player.triggerAttack()) return;

        int dir = player.getFacing();
        double px = dir == 1 ? player.getX() + player.getWidth() : player.getX() - 16;
        double py = player.getY() + 20;

        if (player.getState() instanceof FireMarioState) {
            entities.add(new Projectile(px, py, dir * 12.0, 0, true, "FIRE"));
            entities.add(new Projectile(px, py - 8, dir * 11.5, -2.5, true, "FIRE"));
            entities.add(new Projectile(px, py + 8, dir * 11.5, 2.5, true, "FIRE"));
        } else if (player.getState() instanceof SuperMarioState) {
            entities.add(new Projectile(px, py, dir * 9.0, 0, true, "WAVE"));
        } else {
            entities.add(new Projectile(px, py, dir * 10.0, 0, true, "SLASH"));
        }
    }

    // Equipar armas diretamente pelas teclas numéricas (1, 2, 3)
    public void equipWeaponDirectly(int slot) {
        if (slot == 1) player.changeState(new SmallMarioState());
        else if (slot == 2) player.changeState(new SuperMarioState());
        else if (slot == 3) player.changeState(new FireMarioState());
    }

    // Cheat para forçar o aparecimento do chefe direto para demonstração (tecla B)
    public void forceSpawnBossForDemo() {
        entities.removeIf(e -> e instanceof Enemy);
        bossSpawned = true;
        bossBannerTimer = 150;
        screenShake = 20;
        entities.add(new Koopa(810, stageNumber == 2 || stageNumber == 4 ? 150 : 380, true, stageNumber));
    }

    // Getters úteis
    public List<Entity> getEntities() { return entities; }
    public Mario getPlayer() { return player; }
    public int getStageNumber() { return stageNumber; }
    public boolean isStageCleared() { return stageCleared; }
    public int getBossBannerTimer() { return bossBannerTimer; }
    public int getScreenShake() { return screenShake; }
}