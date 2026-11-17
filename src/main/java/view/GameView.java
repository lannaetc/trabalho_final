package org.example.trabalho_final.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.example.trabalho_final.model.*;
import org.example.trabalho_final.model.state.FireMarioState;
import org.example.trabalho_final.model.state.SuperMarioState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Minha classe GameView! O "V" do padrão MVC.
 * É aqui que a mágica visual acontece: uso o Canvas e o GraphicsContext do JavaFX
 * para desenhar cada detalhe dos 4 reinos (fundo, plataformas, chefões, itens,
 * HUD, telas de vitória e game over), aplicando efeitos de brilho (Bloom/Glow) e partículas!
 */
public class GameView {
    // Definição das fontes estilizadas usadas em todo o jogo
    private static final Font FONT_TITLE_MAIN = Font.font("Serif", FontWeight.EXTRA_BOLD, 46);
    private static final Font FONT_TITLE_SUB = Font.font("Monospaced", FontWeight.BOLD, 18);
    private static final Font FONT_TITLE_TAG = Font.font("Monospaced", FontWeight.BOLD, 13);
    private static final Font FONT_AUTHOR = Font.font("Monospaced", FontWeight.EXTRA_BOLD, 16);
    private static final Font FONT_BLINK = Font.font("Monospaced", FontWeight.EXTRA_BOLD, 18);
    private static final Font FONT_HUD = Font.font("Monospaced", FontWeight.EXTRA_BOLD, 13);
    private static final Font FONT_HINT = Font.font("Monospaced", FontWeight.BOLD, 11);
    private static final Font FONT_BOSS_NAME = Font.font("Monospaced", FontWeight.EXTRA_BOLD, 12);
    private static final Font FONT_ALERT_BIG = Font.font("Monospaced", FontWeight.EXTRA_BOLD, 26);
    private static final Font FONT_ALERT_SUB = Font.font("Monospaced", FontWeight.BOLD, 17);
    private static final Font FONT_OVERLAY_BIG = Font.font("Monospaced", FontWeight.EXTRA_BOLD, 38);
    private static final Font FONT_OVERLAY_SUB = Font.font("Monospaced", FontWeight.BOLD, 15);

    private Canvas canvas;
    private GraphicsContext gc;
    private Bloom bloomEffect;
    private DropShadow glowEffect;
    private long tick = 0; // Contador de frames para animações dinâmicas

    // Arrays para o sistema de partículas de fundo (pétalas de cerejeira, poeira estelar, bolhas)
    private final double[] particleX = new double[85];
    private final double[] particleY = new double[85];
    private final double[] particleSpeed = new double[85];
    private final double[] particleSize = new double[85];
    private final double[] particleAngle = new double[85];
    private final List<double[]> swimBubbles = new ArrayList<>();

    // Construtor: inicializa o canvas, gera posições determinísticas para as partículas e aplica efeitos visuais
    public GameView(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        Random rand = new Random(42);
        for (int i = 0; i < particleX.length; i++) {
            particleX[i] = rand.nextDouble() * 1000;
            particleY[i] = rand.nextDouble() * 620;
            particleSpeed[i] = 0.7 + rand.nextDouble() * 1.9;
            particleSize[i] = 3.0 + rand.nextDouble() * 5.0;
            particleAngle[i] = rand.nextDouble() * Math.PI * 2;
        }

        // Configuro efeitos de sombra e brilho neon para dar um visual moderno ao jogo
        this.glowEffect = new DropShadow(14, Color.web("#f472b6"));
        this.glowEffect.setSpread(0.18);
        this.bloomEffect = new Bloom(0.22);
        this.bloomEffect.setInput(glowEffect);
        this.canvas.setEffect(bloomEffect);
    }

    // Tela de Título do Jogo (Menu inicial bonito com recorde, créditos e easter egg)
    public void renderTitleScreen() {
        tick++;
        bloomEffect.setThreshold(0.22);
        glowEffect.setRadius(14);
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        renderJapaneseSakuraBackdrop(w, h);

        gc.setFill(Color.web("#07030d", 0.30));
        gc.fillRect(0, 0, w, h);

        gc.save();
        gc.setTextAlign(TextAlignment.CENTER);

        // Se o jogador já zerou o jogo, exibe uma badge especial de conquista!
        if (RewardManager.hasBeatenGame()) {
            double pulseBadge = Math.sin(tick * 0.08) * 4;
            gc.setFill(Color.web("#fbbf24", 0.95));
            gc.fillRoundRect(w / 2.0 - 180, 95 + pulseBadge / 2, 360, 30, 10, 10);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(w / 2.0 - 180, 95 + pulseBadge / 2, 360, 30, 10, 10);

            gc.setFont(FONT_TITLE_TAG);
            gc.setFill(Color.web("#18181b"));
            gc.fillText("✨ [STATUS: DEUS DA SAKURA ETERNA] ✨", w / 2.0, 115 + pulseBadge / 2);
        }

        gc.setFont(FONT_TITLE_TAG);
        gc.setFill(Color.web("#fbcfe8", 0.85));
        gc.fillText("✦  UMA JORNADA ÉPICA PELOS QUATRO REINOS  ✦", w / 2.0, 155);

        // Título principal com efeito de sombra estilizada
        gc.setFont(FONT_TITLE_MAIN);
        gc.setFill(Color.web("#000000", 0.65));
        gc.fillText("CHRONICLES OF THE FOUR REALMS", w / 2.0 + 3, 234);
        gc.setFill(Color.web("#f43f5e"));
        gc.fillText("CHRONICLES OF THE FOUR REALMS", w / 2.0, 230);

        gc.setFont(FONT_TITLE_SUB);
        gc.setFill(Color.web("#fbbf24"));
        gc.fillText("— SAKURA ETERNA —", w / 2.0, 270);

        int highScore = ScoreManager.loadHighScore();
        gc.setFont(FONT_TITLE_TAG);
        gc.setFill(Color.web("#4ade80"));
        gc.fillText("🏆 RECORDE: " + String.format("%06d", highScore) + " PTS", w / 2.0, 310);

        // Caixa de créditos com seu nome!
        double boxW = 420;
        double boxH = 46;
        gc.setFill(Color.web("#0f0514", 0.70));
        gc.fillRoundRect(w / 2.0 - boxW / 2.0, 345, boxW, boxH, 10, 10);
        gc.setStroke(Color.web("#f472b6", 0.5));
        gc.setLineWidth(1.2);
        gc.strokeRoundRect(w / 2.0 - boxW / 2.0, 345, boxW, boxH, 10, 10);

        gc.setFont(FONT_AUTHOR);
        gc.setFill(Color.WHITE);
        gc.fillText("Desenvolvido por Alana S. Lima", w / 2.0, 373);

        // Texto piscando convidando para jogar
        double alpha = 0.55 + 0.45 * Math.sin(tick * 0.08);
        gc.setFont(FONT_BLINK);
        gc.setFill(Color.color(1.0, 0.94, 0.54, alpha));
        gc.fillText("▶  PRESSIONE [ENTER] OU [ESPAÇO] PARA JOGAR  ◀", w / 2.0, 460);

        gc.setFont(FONT_HINT);
        gc.setFill(Color.web("#d6d3d1", 0.75));
        gc.fillText("[A/D] Mover  •  [W/Espaço] Pular  •  [J/Z] Mea Sakura  •  [K/X] Especial  •  [1/2/3] Armas", w / 2.0, 530);

        gc.restore();
        renderForegroundSakura(w, h);
    }

    private void drawHeroEmblem(double cx, double cy) {}

    // Método principal de renderização da gameplay (atualiza e desenha tudo na tela a cada frame)
    public void render(Level level, int totalStages, boolean gameOver, boolean victory) {
        tick++;
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        int stageNumber = level.getStageNumber();

        // Ajuste dinâmico de Bloom de acordo com a fase (Fase 3 do reino doce brilha mais!)
        if (stageNumber == 3) {
            bloomEffect.setThreshold(0.78);
            glowEffect.setRadius(6);
        } else {
            bloomEffect.setThreshold(0.22);
            glowEffect.setRadius(14);
        }

        gc.save();
        gc.setTransform(1, 0, 0, 1, 0, 0);

        // Efeito de tremor de tela (Screen Shake) quando toma dano ou bate no chefe
        if (level.getScreenShake() > 0) {
            double sx = (Math.random() - 0.5) * level.getScreenShake();
            double sy = (Math.random() - 0.5) * level.getScreenShake();
            gc.translate(sx, sy);
        }

        // Desenha o cenário de fundo animado correspondente ao reino atual
        renderAnimatedBackdrop(w, h, stageNumber);

        Mario player = null;
        Koopa activeBoss = null;

        // Varre todas as entidades da fase e desenha cada uma com seu respectivo método
        for (Entity e : level.getEntities()) {
            if (!e.isActive()) continue;

            gc.save();
            if (e instanceof Platform p) {
                drawPlatform(p, stageNumber);
            } else if (e instanceof PowerUpItem item) {
                drawPowerUp(item, stageNumber);
            } else if (e instanceof Goomba g) {
                drawGoomba(g, stageNumber);
            } else if (e instanceof Koopa k) {
                if (k.isBoss()) activeBoss = k;
                drawKoopa(k, stageNumber);
            } else if (e instanceof Projectile proj) {
                drawProjectile(proj);
            } else if (e instanceof Mario m) {
                player = m;
                if (m.isUnderwater()) {
                    spawnAndDrawSwimBubbles(m);
                }
                drawThemedSkin(m, stageNumber);
            }
            gc.restore();
        }

        if (stageNumber == 1) {
            renderForegroundSakura(w, h);
        }

        if (player != null) {
            renderHud(player, stageNumber, totalStages, w);
        }
        if (activeBoss != null) {
            renderBossHud(activeBoss, w);
        }

        if (level.getBossBannerTimer() > 0 && activeBoss != null) {
            renderBossAlert(getBossCustomTitle(stageNumber), w, h);
        }

        gc.restore();

        // Se o jogo acabou ou se ganhou, exibe as telas de overlay correspondentes
        if (gameOver) {
            renderOverlay("GAME OVER", "A LUZ SE APAGOU • [R] Renascer • [ESC] Capa", Color.web("#dc2626"), w, h);
        } else if (victory) {
            RewardManager.saveGameCompleted();
            renderSpectacularVictoryCelebration(w, h);
        }
    }

    private String getBossCustomTitle(int stage) {
        if (stage == 1) return "O GRANDE SAMURAI, SHOGUN DA SAKURA";
        if (stage == 2) return "POSEIDON, O REI DOS MARES";
        if (stage == 3) return "BARÃO CUPCAKE, O SOBERANO DOCE";
        return "O ALIEN CÓSMICO, O DESTRUIDOR DE MUNDOS";
    }

    // Tela espetacular de comemoração da vitória ao zerar todos os 4 reinos!
    private void renderSpectacularVictoryCelebration(double w, double h) {
        gc.setFill(Color.web("#07030d", 0.90));
        gc.fillRect(0, 0, w, h);

        Color[] confettis = {
                Color.web("#ef4444"), Color.web("#facc15"), Color.web("#4ade80"),
                Color.web("#38bdf8"), Color.web("#ec4899"), Color.web("#c084fc"), Color.web("#fb923c")
        };
        for (int i = 0; i < 70; i++) {
            double cx = (i * 137 + tick * 4) % w;
            double cy = (i * 83 + tick * 5) % h;
            gc.save();
            gc.translate(cx, cy);
            gc.rotate(tick * 3 + i * 20);
            gc.setFill(confettis[i % confettis.length]);
            gc.fillRoundRect(-6, -3, 12, 6, 3, 3);
            gc.restore();
        }

        gc.save();
        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFill(Color.web("#f59e0b", 0.22));
        gc.fillOval(w / 2.0 - 260, h / 2.0 - 200, 520, 360);

        gc.setFont(FONT_OVERLAY_BIG);
        gc.setFill(Color.web("#4ade80"));
        gc.fillText("VOCÊ GANHOU!", w / 2.0, h / 2.0 - 70);

        double crownY = h / 2.0 - 115 + Math.sin(tick * 0.12) * 12;
        gc.setFill(Color.web("#fde047"));
        gc.fillPolygon(
                new double[]{w / 2.0 - 45, w / 2.0 + 45, w / 2.0 + 34, w / 2.0 - 34},
                new double[]{crownY - 10, crownY - 10, crownY - 55, crownY - 55},
                4
        );
        gc.setFill(Color.web("#ef4444"));
        gc.fillOval(w / 2.0 - 6, crownY - 42, 12, 12);
        gc.setFill(Color.web("#38bdf8"));
        gc.fillOval(w / 2.0 - 28, crownY - 38, 9, 9);
        gc.fillOval(w / 2.0 + 19, crownY - 38, 9, 9);

        gc.setFont(FONT_TITLE_TAG);
        gc.setFill(Color.web("#fbbf24"));
        gc.fillText("👑 [TODOS OS QUATRO REINOS FORAM SALVOS] 👑", w / 2.0, h / 2.0 + 25);

        gc.setFont(FONT_OVERLAY_SUB);
        gc.setFill(Color.WHITE);
        gc.fillText("Parabéns! [R] Jogar Novamente • [ESC] Capa", w / 2.0, h / 2.0 + 80);
        gc.restore();
    }

    private void spawnAndDrawSwimBubbles(Mario m) {
        boolean moving = Math.abs(m.getVelX()) > 0.2 || m.getVelY() < -1.0;
        if (tick % (moving ? 5 : 16) == 0) {
            double bx = m.getX() + m.getWidth() / 2.0 + (m.getFacing() * 8);
            double by = m.getY() + 14;
            double radius = 3.0 + Math.random() * 5.0;
            swimBubbles.add(new double[]{bx, by, radius, 1.2 + Math.random() * 1.4});
        }

        Iterator<double[]> it = swimBubbles.iterator();
        while (it.hasNext()) {
            double[] b = it.next();
            b[1] -= b[3];
            b[0] += Math.sin(tick * 0.1 + b[1] * 0.05) * 0.8;

            if (b[1] < 65) {
                it.remove();
                continue;
            }

            gc.setStroke(Color.web("#bae6fd", 0.85));
            gc.setLineWidth(1.5);
            gc.strokeOval(b[0] - b[2] / 2, b[1] - b[2] / 2, b[2], b[2]);
            gc.setFill(Color.web("#ffffff", 0.6));
            gc.fillOval(b[0] - b[2] / 4, b[1] - b[2] / 4, b[2] * 0.35, b[2] * 0.35);
        }
    }

    private void renderAnimatedBackdrop(double w, double h, int stage) {
        if (stage == 1) {
            renderJapaneseSakuraBackdrop(w, h);
        } else if (stage == 2) {
            renderAtlantisUnderwaterBackdrop(w, h);
        } else if (stage == 3) {
            renderCandyWorldBackdrop(w, h);
        } else {
            renderCosmicSpaceBackdrop(w, h);
        }
    }

    // Fundo da Fase 1: Reino das Cerejeiras (Japão Feudal com pétalas caindo)
    private void renderJapaneseSakuraBackdrop(double w, double h) {
        LinearGradient sky = new LinearGradient(0, 0, 0, h, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#06020c")),
                new Stop(0.45, Color.web("#170924")),
                new Stop(1.0, Color.web("#33102b")));
        gc.setFill(sky);
        gc.fillRect(0, 0, w, h);

        gc.setFill(Color.web("#ffffff", 0.025));
        gc.fillPolygon(new double[]{0, 180, 320, 0}, new double[]{0, 0, h, h}, 4);
        gc.fillPolygon(new double[]{450, 700, 900, 650}, new double[]{0, 0, h, h}, 4);

        double moonPulse = Math.sin(tick * 0.03) * 3;
        gc.setFill(Color.web("#fbcfe8", 0.06));
        gc.fillOval(w / 2.0 - 170 - moonPulse / 2, 40 - moonPulse / 2, 340 + moonPulse, 340 + moonPulse);
        gc.setFill(Color.web("#fff1f2", 0.42));
        gc.fillOval(w / 2.0 - 110, 90, 220, 220);

        double leftX = 160;
        gc.setFill(Color.web("#7f1d1d"));
        gc.fillRect(leftX - 45, 275, 18, 250);
        gc.fillRect(leftX + 27, 275, 18, 250);
        gc.fillRoundRect(leftX - 65, 260, 160, 18, 8, 8);
        gc.fillRoundRect(leftX - 55, 285, 140, 14, 6, 6);

        gc.setFill(Color.web("#140a1b"));
        gc.fillRect(leftX + 90, 340, 110, 185);
        gc.setFill(Color.web("#4c1322"));
        gc.fillPolygon(new double[]{leftX + 70, leftX + 220, leftX + 200, leftX + 90}, new double[]{340, 340, 315, 315}, 4);

        gc.setFill(Color.web("#fde047", 0.85));
        gc.fillOval(leftX + 135, 370, 24, 24);

        double rightX = w - 220;
        gc.setFill(Color.web("#110619"));
        gc.fillPolygon(new double[]{rightX - 280, rightX + 60, rightX + 320}, new double[]{525, 150, 525}, 3);
        gc.setFill(Color.web("#220a2e"));
        gc.fillPolygon(new double[]{rightX - 120, rightX + 120, rightX + 280}, new double[]{525, 240, 525}, 3);

        double swayMtn = Math.sin(tick * 0.035) * 6;
        gc.setFill(Color.web("#f472b6", 0.75));
        gc.fillOval(rightX - 160 + swayMtn, 210, 240, 160);
        gc.setFill(Color.web("#f9a8d4", 0.85));
        gc.fillOval(rightX - 80 + swayMtn * 1.2, 170, 200, 130);
        gc.setFill(Color.web("#fbcfe8", 0.9));
        gc.fillOval(rightX - 20 + swayMtn, 140, 130, 100);

        gc.setFill(Color.web("#3b1302"));
        gc.fillRect(rightX + 120, 380, 16, 145);
        gc.setFill(Color.web("#f472b6", 0.8));
        gc.fillOval(rightX + 60, 335 + swayMtn, 140, 100);
        gc.setFill(Color.web("#f9a8d4", 0.85));
        gc.fillOval(rightX + 90, 310, 100, 80);

        // Atualiza e desenha as pétalas de cerejeira caindo
        for (int i = 0; i < particleX.length; i++) {
            particleY[i] += particleSpeed[i] * 0.85;
            particleX[i] -= 0.9 + Math.sin(tick * 0.03 + i) * 1.1;
            particleAngle[i] += 0.06;

            if (particleY[i] > h) particleY[i] = -10;
            if (particleX[i] < -10) particleX[i] = w + 10;

            gc.save();
            gc.translate(particleX[i], particleY[i]);
            gc.rotate(Math.toDegrees(particleAngle[i]));
            gc.setFill(i % 2 == 0 ? Color.web("#f9a8d4", 0.85) : Color.web("#fbcfe8", 0.9));
            gc.fillOval(-particleSize[i] / 2, -particleSize[i] / 4, particleSize[i], particleSize[i] * 0.55);
            gc.restore();
        }
    }

    private void renderForegroundSakura(double w, double h) {
        for (int i = 0; i < 14; i++) {
            double fx = (particleX[i] * 1.3 + tick * 0.4) % w;
            double fy = (particleY[i] * 1.2) % h;
            gc.save();
            gc.translate(fx, fy);
            gc.rotate(tick * 2 + i * 25);
            gc.setFill(Color.web("#fbcfe8", 0.55));
            gc.fillOval(-6, -3, 12, 6);
            gc.restore();
        }
    }

    // Fundo da Fase 2: Atlântida / Subaquática (com feixes de luz na água e bolhas subindo)
    private void renderAtlantisUnderwaterBackdrop(double w, double h) {
        LinearGradient ocean = new LinearGradient(0, 0, 0, h, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#0369a1")),
                new Stop(0.45, Color.web("#0c4a6e")),
                new Stop(1.0, Color.web("#041729")));
        gc.setFill(ocean);
        gc.fillRect(0, 0, w, h);

        for (int r = 0; r < 5; r++) {
            double sway = Math.sin(tick * 0.02 + r) * 35;
            double rx = 140 + r * 190;
            gc.setFill(Color.web("#7dd3fc", 0.08));
            gc.fillPolygon(new double[]{rx - 30, rx + 30, rx + 110 + sway, rx - 70 + sway}, new double[]{0, 0, h, h}, 4);
        }

        gc.setFill(Color.web("#0f766e", 0.7));
        for (double ax = 50; ax < w; ax += 120) {
            double waveAlgae = Math.sin(tick * 0.04 + ax) * 15;
            gc.fillPolygon(new double[]{ax, ax + 12 + waveAlgae, ax + 24}, new double[]{525, 380, 525}, 3);
        }

        for (int f = 0; f < 6; f++) {
            double fx = ((tick * (1.2 + f * 0.3) + f * 180) % (w + 120)) - 60;
            double fy = 150 + (f * 65) % 250 + Math.sin(tick * 0.05 + f) * 12;
            gc.setFill(Color.web("#38bdf8", 0.8));
            gc.fillOval(fx, fy, 16, 9);
            gc.fillPolygon(new double[]{fx + 14, fx + 22, fx + 22}, new double[]{fy + 4.5, fy - 3, fy + 12}, 3);
        }

        for (int t = 0; t < 2; t++) {
            double tx = ((tick * 0.8 + t * 450) % (w + 200)) - 100;
            double ty = 220 + t * 160 + Math.sin(tick * 0.03 + t) * 20;
            double flipperAngle = Math.sin(tick * 0.1) * 12;

            gc.save();
            gc.translate(tx, ty);
            gc.setFill(Color.web("#047857", 0.85));
            gc.fillOval(-22, -14, 44, 28);
            gc.save();
            gc.rotate(flipperAngle);
            gc.setFill(Color.web("#065f46", 0.85));
            gc.fillOval(12, -22, 16, 10);
            gc.fillOval(12, 12, 16, 10);
            gc.restore();
            gc.setFill(Color.web("#059669", 0.85));
            gc.fillOval(-30, -7, 12, 12);
            gc.restore();
        }

        gc.setFill(Color.web("#072a47", 0.72));
        gc.fillPolygon(new double[]{270, 500, 730}, new double[]{225, 145, 225}, 3);
        gc.fillRect(285, 225, 430, 16);
        for (int cx = 310; cx <= 670; cx += 72) {
            gc.fillRect(cx, 241, 22, 284);
        }

        for (int i = 0; i < particleX.length; i++) {
            particleY[i] -= particleSpeed[i] * 1.1;
            particleX[i] += Math.sin(tick * 0.04 + i) * 0.9;
            if (particleY[i] < 60) particleY[i] = h;

            gc.setStroke(Color.web("#bae6fd", 0.55));
            gc.setLineWidth(1.2);
            gc.strokeOval(particleX[i], particleY[i], particleSize[i] + 2, particleSize[i] + 2);
        }
    }

    // Fundo da Fase 3: Reino Doce / Confeitaria (com pirulitos e doces coloridos)
    private void renderCandyWorldBackdrop(double w, double h) {
        LinearGradient candySky = new LinearGradient(0, 0, 0, h, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#3b0764")),
                new Stop(0.50, Color.web("#831843")),
                new Stop(1.0, Color.web("#be185d")));
        gc.setFill(candySky);
        gc.fillRect(0, 0, w, h);

        for (int s = 0; s < 30; s++) {
            double alpha = 0.3 + 0.6 * Math.abs(Math.sin(tick * 0.05 + s));
            gc.setFill(Color.web("#fef08a", alpha));
            gc.fillOval((s * 67) % w, 50 + (s * 31) % 190, 3.5, 3.5);
        }

        Color[] rainbow = {
                Color.web("#f43f5e", 0.6), Color.web("#fb923c", 0.6),
                Color.web("#facc15", 0.6), Color.web("#4ade80", 0.6),
                Color.web("#38bdf8", 0.6), Color.web("#c084fc", 0.6)
        };
        for (int r = 0; r < rainbow.length; r++) {
            gc.setStroke(rainbow[r]);
            gc.setLineWidth(14);
            double rad = 360 - r * 14;
            gc.strokeArc(w / 2.0 - rad, 525 - rad, rad * 2, rad * 2, 0, 180, ArcType.OPEN);
        }

        double cx = w / 2.0;

        gc.setFill(Color.web("#38bdf8"));
        gc.fillOval(45, 360, 110, 90);
        gc.setFill(Color.web("#a7f3d0"));
        gc.fillOval(65, 305, 80, 75);
        gc.setFill(Color.web("#78350f"));
        gc.fillOval(68, 310, 35, 20);
        gc.fillOval(105, 330, 25, 25);

        gc.setFill(Color.web("#f472b6"));
        gc.fillOval(840, 350, 120, 95);
        gc.setFill(Color.web("#fde047"));
        gc.fillOval(870, 290, 85, 80);
        gc.setFill(Color.web("#ffffff"));
        gc.fillOval(890, 255, 60, 60);
        gc.setFill(Color.web("#d97706"));
        gc.fillPolygon(new double[]{870, 955, 912}, new double[]{435, 435, 525}, 3);

        gc.setFill(Color.web("#e2e8f0"));
        gc.fillRoundRect(cx - 210, 480, 420, 20, 10, 10);
        gc.setFill(Color.web("#94a3b8"));
        gc.fillRect(cx - 190, 500, 380, 12);

        gc.setFill(Color.web("#78350f"));
        gc.fillRect(cx - 160, 380, 320, 100);
        gc.setFill(Color.web("#fbcfe8"));
        gc.fillRect(cx - 160, 465, 320, 15);

        gc.setFill(Color.web("#fde047"));
        gc.fillRect(cx - 130, 280, 260, 100);
        gc.setFill(Color.web("#ec4899"));
        gc.fillRect(cx - 130, 365, 260, 15);

        gc.setFill(Color.web("#f43f5e"));
        gc.fillRect(cx - 90, 195, 180, 85);

        gc.setFill(Color.WHITE);
        gc.fillOval(cx - 95, 190, 190, 40);
        gc.fillOval(cx - 135, 275, 270, 35);
        gc.fillOval(cx - 165, 375, 330, 35);

        gc.setFill(Color.web("#1e1b4b"));
        gc.fillOval(cx - 45, 315, 24, 30);
        gc.setFill(Color.web("#38bdf8"));
        gc.fillOval(cx - 41, 322, 16, 16);

        gc.setFill(Color.web("#1e1b4b"));
        gc.fillOval(cx + 21, 315, 24, 30);
        gc.setFill(Color.web("#facc15"));
        gc.fillOval(cx + 25, 322, 16, 16);

        gc.setFill(Color.web("#1e1b4b"));
        gc.fillOval(cx - 12, 220, 24, 28);
        gc.setFill(Color.web("#4ade80"));
        gc.fillOval(cx - 8, 226, 16, 16);

        gc.setFill(Color.web("#38bdf8"));
        gc.fillRect(cx - 32, 140, 12, 60);
        gc.setFill(Color.web("#f43f5e"));
        gc.fillRect(cx + 20, 140, 12, 60);

        gc.setFill(Color.web("#fef08a"));
        gc.fillOval(cx - 34, 120, 16, 22);
        gc.fillOval(cx + 18, 120, 16, 22);
        gc.setFill(Color.web("#f97316"));
        gc.fillOval(cx - 30, 124, 8, 14);
        gc.fillOval(cx + 22, 124, 8, 14);

        for (int c = 0; c < 4; c++) {
            double ckx = ((c * 280 + tick * 0.45) % (w + 200)) - 100;
            double cky = 115 + (c % 2) * 50;
            gc.setFill(Color.web("#fbcfe8", 0.55));
            gc.fillOval(ckx, cky, 90, 40);
            gc.fillOval(ckx + 25, cky - 14, 65, 44);
            gc.fillOval(ckx + 55, cky + 4, 75, 36);
        }

        gc.setFill(Color.web("#831843"));
        gc.fillOval(-60, 395, 420, 260);
        gc.setFill(Color.web("#581c87"));
        gc.fillOval(320, 415, 440, 240);
        gc.setFill(Color.web("#155e75"));
        gc.fillOval(680, 390, 400, 260);

        drawSpinningLollipop(175, 370, 40, Color.web("#ec4899"), Color.web("#fbcfe8"));
        drawSpinningLollipop(825, 350, 46, Color.web("#8b5cf6"), Color.web("#fef08a"));
        drawSpinningLollipop(510, 420, 32, Color.web("#10b981"), Color.web("#a7f3d0"));

        Color[] sprinkleColors = {
                Color.web("#f87171"), Color.web("#facc15"), Color.web("#4ade80"),
                Color.web("#38bdf8"), Color.web("#f472b6"), Color.web("#c084fc")
        };
        for (int i = 0; i < particleX.length; i++) {
            particleY[i] += particleSpeed[i] * 0.9;
            particleX[i] += Math.sin(tick * 0.04 + i) * 1.1;
            particleAngle[i] += 0.08;
            if (particleY[i] > h) particleY[i] = -10;

            gc.save();
            gc.translate(particleX[i], particleY[i]);
            gc.rotate(Math.toDegrees(particleAngle[i]));
            gc.setFill(sprinkleColors[i % sprinkleColors.length]);
            gc.fillRoundRect(-5, -2.5, 10, 5, 4, 4);
            gc.restore();
        }
    }

    private void drawSpinningLollipop(double cx, double cy, double radius, Color c1, Color c2) {
        gc.setFill(Color.web("#fde68a"));
        gc.fillRect(cx - 4, cy, 8, 525 - cy);

        gc.save();
        gc.translate(cx, cy);
        gc.rotate(tick * 1.6);
        gc.setFill(c1);
        gc.fillOval(-radius, -radius, radius * 2, radius * 2);

        gc.setStroke(c2);
        gc.setLineWidth(5.0);
        for (int s = 0; s < 4; s++) {
            gc.rotate(90);
            gc.strokeArc(0, -radius * 0.5, radius * 0.9, radius * 0.9, 0, 180, ArcType.OPEN);
        }
        gc.setStroke(c2);
        gc.setLineWidth(2.5);
        gc.strokeOval(-radius, -radius, radius * 2, radius * 2);
        gc.restore();
    }

    // Fundo da Fase 4: Espaço Sideral (com estrelas brilhantes, planetas e naves)
    private void renderCosmicSpaceBackdrop(double w, double h) {
        LinearGradient space = new LinearGradient(0, 0, 0, h, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#03010c")),
                new Stop(0.50, Color.web("#0f0a2e")),
                new Stop(1.0, Color.web("#22124d")));
        gc.setFill(space);
        gc.fillRect(0, 0, w, h);

        for (int band = 0; band < 2; band++) {
            gc.setStroke(band == 0 ? Color.web("#34d399", 0.16) : Color.web("#c084fc", 0.16));
            gc.setLineWidth(38);
            double wave1 = Math.sin(tick * 0.025 + band) * 35;
            double wave2 = Math.cos(tick * 0.025 + band) * 35;
            gc.beginPath();
            gc.moveTo(0, 140 + band * 45);
            gc.bezierCurveTo(320, 90 + wave1, 680, 190 + wave2, w, 120 + band * 40);
            gc.stroke();
        }

        gc.save();
        gc.translate(215, 185);
        gc.rotate(tick * 0.45);
        gc.setFill(Color.web("#c084fc", 0.22));
        gc.fillOval(-100, -34, 200, 68);
        gc.rotate(60);
        gc.setFill(Color.web("#38bdf8", 0.20));
        gc.fillOval(-100, -34, 200, 68);
        gc.setFill(Color.web("#fef08a", 0.9));
        gc.fillOval(-14, -14, 28, 28);
        gc.restore();

        double bx = 775;
        double by = 180;
        double coronaPulse = Math.sin(tick * 0.05) * 12;
        RadialGradient corona = new RadialGradient(0, 0, bx, by, 120 + coronaPulse, false, CycleMethod.NO_CYCLE,
                new Stop(0.35, Color.web("#a855f7", 0.85)),
                new Stop(0.70, Color.web("#38bdf8", 0.32)),
                new Stop(1.0, Color.TRANSPARENT));
        gc.setFill(corona);
        gc.fillOval(bx - 125 - coronaPulse / 2, by - 125 - coronaPulse / 2, 250 + coronaPulse, 250 + coronaPulse);

        gc.save();
        gc.translate(bx, by);
        gc.rotate(-tick * 0.85);
        gc.setStroke(Color.web("#67e8f9", 0.75));
        gc.setLineWidth(2.8);
        gc.strokeOval(-94, -26, 188, 52);
        gc.restore();

        gc.setFill(Color.web("#03010c"));
        gc.fillOval(bx - 42, by - 42, 84, 84);
        gc.setStroke(Color.web("#fde047", 0.95));
        gc.setLineWidth(2.2);
        gc.strokeOval(bx - 42, by - 42, 84, 84);

        double[][] stars = {
                {410, 130}, {465, 105}, {525, 135}, {575, 110}, {540, 175}, {455, 175}
        };
        gc.setStroke(Color.web("#fde047", 0.32));
        gc.setLineWidth(1.4);
        for (int s = 0; s < stars.length - 1; s++) {
            gc.strokeLine(stars[s][0], stars[s][1], stars[s + 1][0], stars[s + 1][1]);
        }
        for (int s = 0; s < stars.length; s++) {
            double glow = 3.5 + Math.sin(tick * 0.08 + s) * 1.5;
            gc.setFill(Color.web("#fef08a", 0.9));
            gc.fillOval(stars[s][0] - glow, stars[s][1] - glow, glow * 2, glow * 2);
        }

        for (int i = 0; i < particleX.length; i++) {
            particleX[i] -= particleSpeed[i] * 0.95;
            if (particleX[i] < 0) particleX[i] = w;

            double twinkle = 0.4 + 0.6 * Math.abs(Math.sin(tick * 0.06 + i));
            if (i % 9 == 0) {
                double sx = particleX[i];
                double sy = particleY[i];
                double arm = 6.0 * twinkle;

                gc.setStroke(Color.web("#fef08a", twinkle));
                gc.setLineWidth(1.6);
                gc.strokeLine(sx - arm, sy, sx + arm, sy);
                gc.strokeLine(sx, sy - arm, sx, sy + arm);
            } else {
                gc.setFill(i % 3 == 0 ? Color.web("#67e8f9", twinkle) : Color.web("#f8fafc", twinkle));
                double sz = (i % 4 == 0) ? 3.2 : 2.0;
                gc.fillOval(particleX[i], particleY[i], sz, sz);
            }
        }

        for (int u = 0; u < 3; u++) {
            double ux = ((tick * (1.5 + u * 0.4) + u * 340) % (w + 140)) - 70;
            double uy = 115 + u * 65 + Math.sin(tick * 0.05 + u) * 12;

            gc.setFill(Color.web("#38bdf8", 0.55));
            gc.fillArc(ux - 10, uy - 10, 20, 14, 0, 180, ArcType.CHORD);
            gc.setFill(Color.web("#64748b", 0.75));
            gc.fillOval(ux - 18, uy - 4, 36, 9);
            gc.setFill((tick / 8 + u) % 2 == 0 ? Color.web("#4ade80") : Color.web("#facc15"));
            gc.fillOval(ux - 3, uy - 1, 6, 4);
        }
    }

    private void drawPlatform(Platform p, int stage) {
        if (stage == 3) {
            gc.setFill(Color.web("#451a03"));
            gc.fillRoundRect(p.getX(), p.getY(), p.getWidth(), p.getHeight(), 10, 10);
            gc.setFill(Color.web("#ec4899"));
            gc.fillRoundRect(p.getX(), p.getY(), p.getWidth(), 7, 8, 8);
            Color[] dots = {Color.web("#fef08a"), Color.web("#38bdf8"), Color.web("#4ade80"), Color.web("#f9a8d4")};
            int idx = 0;
            for (double sx = p.getX() + 10; sx < p.getX() + p.getWidth() - 10; sx += 18) {
                gc.setFill(dots[idx % dots.length]);
                gc.fillRect(sx, p.getY() + 2, 6, 3);
                idx++;
            }
            return;
        }

        Color stone = stage == 1 ? Color.web("#23111c") : (stage == 2 ? Color.web("#082f49") : Color.web("#0f172a"));
        Color trim = stage == 1 ? Color.web("#f472b6") : (stage == 2 ? Color.web("#2dd4bf") : Color.web("#4ade80"));

        gc.setFill(stone);
        gc.fillRect(p.getX(), p.getY(), p.getWidth(), p.getHeight());

        gc.setStroke(Color.web("#000000", 0.45));
        gc.setLineWidth(1.5);
        for (double bx = p.getX(); bx < p.getX() + p.getWidth(); bx += 32) {
            gc.strokeLine(bx, p.getY(), bx, p.getY() + p.getHeight());
        }

        gc.setFill(trim);
        gc.fillRect(p.getX(), p.getY(), p.getWidth(), 4);
    }

    private void drawPowerUp(PowerUpItem item, int stage) {
        double px = item.getX();
        double py = item.getY();
        double pulse = Math.sin(tick * 0.18) * 6;

        gc.save();

        RadialGradient glow = new RadialGradient(0, 0, px + 12, py + 12, 26 + pulse, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#ffffff", 0.9)),
                new Stop(0.5, Color.web("#fde047", 0.6)),
                new Stop(1.0, Color.TRANSPARENT));
        gc.setFill(glow);
        gc.fillOval(px - 14, py - 14, 52, 52);

        gc.setStroke(Color.web("#fef08a"));
        gc.setLineWidth(2.5);
        gc.strokeOval(px - 4, py - 4, 32, 32);

        if (stage == 1) {
            gc.setFill(Color.web("#991b1b"));
            gc.fillRoundRect(px + 2, py + 4, 20, 18, 6, 6);
            gc.setFill(Color.web("#fbbf24"));
            gc.fillOval(px + 5, py, 14, 6);
            gc.setStroke(Color.web("#fef08a"));
            gc.setLineWidth(1.5);
            gc.strokeLine(px + 6, py + 10, px + 18, py + 14);

        } else if (stage == 2) {
            gc.setFill(Color.web("#38bdf8"));
            gc.fillArc(px, py, 24, 24, 0, 180, ArcType.CHORD);
            gc.setFill(Color.web("#fef08a"));
            gc.fillOval(px + 7, py + 6, 10, 10);

        } else if (stage == 3) {
            gc.setFill(Color.web("#78350f"));
            gc.fillPolygon(new double[]{px + 2, px + 22, px + 18, px + 6}, new double[]{py + 24, py + 24, py + 12, py + 12}, 4);
            gc.setFill(Color.web("#f43f5e"));
            gc.fillOval(px - 2, py + 4, 28, 14);
            gc.setFill(Color.web("#dc2626"));
            gc.fillOval(px + 8, py - 2, 8, 8);
            gc.setFill(Color.WHITE);
            gc.fillOval(px + 9, py - 1, 3, 3);

        } else {
            gc.setFill(Color.web("#22c55e"));
            gc.fillOval(px - 2, py + 8, 28, 10);
            gc.setFill(Color.web("#67e8f9"));
            gc.fillOval(px + 5, py, 14, 12);
            gc.setFill(Color.web("#facc15"));
            gc.fillOval(px + 10, py + 16, 4, 8);
        }

        gc.restore();
    }

    private void drawGoomba(Goomba g, int stage) {
        double x = g.getX();
        double y = g.getY();
        double w = g.getWidth();
        double h = g.getHeight();
        boolean flash = g.getHitFlashTimer() > 0;
        double bob = Math.sin(tick * 0.22 + x * 0.05) * 2.5;
        int dir = g.getVelX() >= 0 ? 1 : -1;

        gc.save();
        gc.translate(x + w / 2.0, y + h / 2.0 + bob);
        gc.scale(dir, 1);

        if (stage == 1) {
            double ribbon = Math.sin(tick * 0.3) * 4;
            gc.setStroke(Color.web("#ef4444"));
            gc.setLineWidth(3.5);
            gc.strokeLine(-10, -8, -22, -12 + ribbon);
            gc.strokeLine(-10, -6, -20, -4);

            gc.setFill(flash ? Color.WHITE : Color.web("#18181b"));
            gc.fillRoundRect(-w / 2.0, -h / 2.0, w, h, 14, 14);
            gc.setStroke(Color.web("#f43f5e"));
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(-w / 2.0, -h / 2.0, w, h, 14, 14);

            gc.setFill(Color.web("#fed7aa"));
            gc.fillRect(-6, -8, 18, 6);
            gc.setFill(Color.web("#dc2626"));
            gc.fillOval(4, -7, 4, 4);

            gc.setStroke(Color.web("#e2e8f0"));
            gc.setLineWidth(2);
            gc.strokeLine(10, 2, 18, 2);
            gc.strokeLine(14, -2, 14, 6);

        } else if (stage == 2) {
            double tailWag = Math.sin(tick * 0.35) * 5;
            gc.setFill(flash ? Color.WHITE : Color.web("#0284c7"));
            gc.fillPolygon(new double[]{-12, -24, -24}, new double[]{0, -9 + tailWag, 9 + tailWag}, 3);

            gc.setFill(flash ? Color.WHITE : Color.web("#0d9488"));
            gc.fillOval(-w / 2.0, -h / 2.0, w, h);
            gc.setStroke(Color.web("#5eead4"));
            gc.setLineWidth(2);
            gc.strokeOval(-w / 2.0, -h / 2.0, w, h);

            gc.setFill(Color.web("#ccfbf1", 0.7));
            gc.fillArc(-w / 2.0 + 2, -2, w - 4, h / 2.0, 180, 180, ArcType.CHORD);
            gc.setFill(Color.WHITE);
            gc.fillOval(4, -8, 8, 8);
            gc.setFill(Color.BLACK);
            gc.fillOval(7, -6, 4, 4);

        } else if (stage == 3) {
            gc.setFill(flash ? Color.WHITE : Color.web("#f472b6"));
            gc.fillPolygon(new double[]{-12, -22, -22}, new double[]{0, -8, 8}, 3);
            gc.fillPolygon(new double[]{12, 22, 22}, new double[]{0, -8, 8}, 3);

            gc.setFill(flash ? Color.WHITE : Color.web("#ec4899"));
            gc.fillOval(-14, -14, 28, 28);
            gc.setStroke(Color.web("#fef08a"));
            gc.setLineWidth(2.5);
            gc.strokeOval(-14, -14, 28, 28);

            gc.setFill(Color.WHITE);
            gc.fillOval(1, -6, 6, 7);
            gc.setFill(Color.web("#1e1b4b"));
            gc.fillOval(3, -4, 3, 4);

        } else {
            gc.setFill(flash ? Color.WHITE : Color.web("#16a34a"));
            gc.fillOval(-14, -14, 28, 28);
            gc.setStroke(Color.web("#86efac"));
            gc.setLineWidth(2);
            gc.strokeOval(-14, -14, 28, 28);

            gc.setStroke(Color.web("#4ade80"));
            gc.strokeLine(0, -14, 0, -22);
            gc.setFill(Color.web("#fef08a"));
            gc.fillOval(-3, -25, 6, 6);

            gc.setFill(Color.BLACK);
            gc.fillOval(2, -6, 6, 9);
        }

        gc.restore();
    }

    private void drawKoopa(Koopa k, int stage) {
        if (k.isBoss()) {
            drawUniqueBoss(k);
            return;
        }

        double x = k.getX();
        double y = k.getY();
        double w = k.getWidth();
        double h = k.getHeight();
        boolean flash = k.getHitFlashTimer() > 0;
        int dir = k.getVelX() >= 0 ? 1 : -1;

        if (k.isInShell()) {
            gc.save();
            gc.translate(x + w / 2.0, y + h / 2.0);
            if (k.isShellKicked()) {
                gc.rotate(tick * 18);
            }
            if (stage == 3) {
                gc.setFill(Color.web("#d97706"));
                gc.fillOval(-w / 2.0, -h / 2.0, w, h);
                gc.setStroke(Color.web("#f472b6"));
                gc.setLineWidth(4);
                gc.strokeOval(-w / 2.0 + 2, -h / 2.0 + 2, w - 4, h - 4);
            } else if (stage == 4) {
                gc.setFill(Color.web("#22c55e"));
                gc.fillOval(-w / 2.0, -h / 2.0, w, h);
                gc.setStroke(Color.web("#fef08a"));
                gc.setLineWidth(2.5);
                gc.strokeLine(-w / 2.0, 0, w / 2.0, 0);
            } else {
                gc.setFill(stage == 2 ? Color.web("#0284c7") : Color.web("#b45309"));
                gc.fillOval(-w / 2.0, -h / 2.0, w, h);
                gc.setStroke(stage == 2 ? Color.web("#5eead4") : Color.web("#fbbf24"));
                gc.setLineWidth(2.5);
                gc.strokeLine(-w / 2.0, 0, w / 2.0, 0);
            }
            gc.restore();
            return;
        }

        gc.save();
        gc.translate(x + w / 2.0, y + h / 2.0);
        gc.scale(dir, 1);

        if (stage == 1) {
            gc.setFill(flash ? Color.WHITE : Color.web("#3f121d"));
            gc.fillRoundRect(-14, -14, 28, 38, 8, 8);
            gc.setStroke(Color.web("#fbbf24"));
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(-14, -14, 28, 38, 8, 8);

            gc.setFill(flash ? Color.WHITE : Color.web("#d97706"));
            gc.fillPolygon(new double[]{-22, 0, 22}, new double[]{-12, -26, -12}, 3);

            gc.setFill(Color.web("#ef4444"));
            gc.fillRect(3, -8, 7, 3);
            gc.setStroke(Color.web("#e2e8f0"));
            gc.setLineWidth(2.5);
            gc.strokeLine(8, 6, 26, -10);

        } else if (stage == 2) {
            double swim = Math.sin(tick * 0.25) * 4;
            gc.setFill(flash ? Color.WHITE : Color.web("#0369a1"));
            gc.fillPolygon(new double[]{-14, -28, -28}, new double[]{0, -12 + swim, 12 + swim}, 3);
            gc.fillPolygon(new double[]{-4, 2, -10}, new double[]{-14, -26, -14}, 3);

            gc.setFill(flash ? Color.WHITE : Color.web("#0284c7"));
            gc.fillOval(-18, -15, 36, 30);

            gc.setFill(Color.web("#bae6fd"));
            gc.fillPolygon(new double[]{16, 30, 16}, new double[]{-4, 0, 4}, 3);

            gc.setFill(Color.web("#fef08a"));
            gc.fillOval(6, -6, 6, 6);

        } else if (stage == 3) {
            gc.setFill(flash ? Color.WHITE : Color.web("#b45309"));
            gc.fillRoundRect(-14, -18, 28, 42, 12, 12);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeRoundRect(-14, -18, 28, 42, 12, 12);

            gc.setFill(Color.web("#f472b6"));
            gc.fillArc(-14, -22, 28, 16, 0, 180, ArcType.ROUND);
            gc.setFill(Color.web("#38bdf8"));
            gc.fillOval(-2, 0, 5, 5);
            gc.setFill(Color.web("#facc15"));
            gc.fillOval(-2, 10, 5, 5);

            gc.setFill(Color.WHITE);
            gc.fillOval(4, -11, 6, 6);

        } else {
            gc.setFill(flash ? Color.WHITE : Color.web("#1e293b"));
            gc.fillRoundRect(-14, -14, 28, 38, 10, 10);
            gc.setStroke(Color.web("#4ade80"));
            gc.setLineWidth(2);
            gc.strokeRoundRect(-14, -14, 28, 38, 10, 10);

            gc.setFill(Color.web("#22c55e"));
            gc.fillOval(-10, -24, 20, 18);
            gc.setStroke(Color.web("#38bdf8"));
            gc.setLineWidth(2);
            gc.strokeOval(-13, -27, 26, 22);

            gc.setFill(Color.BLACK);
            gc.fillOval(2, -18, 5, 7);
        }

        gc.restore();
    }

    // Renderização detalhada de cada um dos 4 Chefões épicos do jogo
    private void drawUniqueBoss(Koopa boss) {
        int stage = boss.getBossStage();
        double x = boss.getX();
        double y = boss.getY();
        double w = boss.getWidth();
        double h = boss.getHeight();
        boolean flash = boss.getHitFlashTimer() > 0;
        int dir = boss.getVelX() >= 0 ? 1 : -1;

        if (stage == 4 && boss.isFiringTractorBeam()) {
            double beamX = x + w / 2.0;
            gc.setFill(Color.web("#22c55e", 0.28));
            gc.fillPolygon(
                    new double[]{beamX - 14, beamX + 14, beamX + 46, beamX - 46},
                    new double[]{y + h - 8, y + h - 8, 525, 525},
                    4
            );
            gc.setStroke(Color.web("#86efac", 0.9));
            gc.setLineWidth(3.0);
            gc.strokeLine(beamX, y + h - 8, beamX, 525);
        }

        gc.save();
        gc.translate(x + w / 2.0, y + h / 2.0);
        if (stage != 4) {
            gc.scale(dir, 1);
        }

        if (stage == 1) {
            // CHEFE 1: Kenshin, o Shogun das Cerejeiras (Samurai com katana)
            gc.setFill(Color.web("#f43f5e", 0.22));
            gc.fillOval(-w * 0.75, -h * 0.65, w * 1.5, h * 1.3);

            gc.setFill(flash ? Color.WHITE : Color.web("#7f1d1d"));
            gc.fillRoundRect(-22, -16, 44, 52, 10, 10);
            gc.setStroke(Color.web("#fbbf24"));
            gc.setLineWidth(2.2);
            gc.strokeRoundRect(-22, -16, 44, 52, 10, 10);

            gc.setFill(flash ? Color.WHITE : Color.web("#991b1b"));
            gc.fillRect(-30, -18, 12, 24);
            gc.fillRect(18, -18, 12, 24);

            gc.setFill(flash ? Color.WHITE : Color.web("#1c1917"));
            gc.fillArc(-20, -44, 40, 34, 0, 180, ArcType.ROUND);
            gc.setStroke(Color.web("#facc15"));
            gc.setLineWidth(3.5);
            gc.strokeArc(-26, -58, 24, 26, 200, 140, ArcType.OPEN);
            gc.strokeArc(2, -58, 24, 26, 200, 140, ArcType.OPEN);

            gc.setFill(Color.web("#ef4444"));
            gc.fillRect(4, -26, 10, 5);

            double katanaAngle = Math.sin(tick * 0.15) * 12;
            gc.rotate(katanaAngle);
            gc.setStroke(Color.web("#f43f5e"));
            gc.setLineWidth(6.0);
            gc.strokeLine(16, 4, 82, -26);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2.5);
            gc.strokeLine(16, 4, 82, -26);

        } else if (stage == 2) {
            // CHEFE 2: Poseidon, o Soberano de Atlântida (com tridente e coroa)
            double floatWave = Math.sin(tick * 0.1) * 5;
            gc.translate(0, floatWave);

            gc.setFill(Color.web("#2dd4bf", 0.22));
            gc.fillOval(-60, -60, 120, 120);

            gc.setFill(flash ? Color.WHITE : Color.web("#0284c7"));
            gc.fillPolygon(new double[]{-22, 22, 12, -28}, new double[]{14, 14, 46, 42}, 4);

            gc.setFill(flash ? Color.WHITE : Color.web("#0369a1"));
            gc.fillRoundRect(-22, -20, 44, 38, 10, 10);
            gc.setStroke(Color.web("#facc15"));
            gc.setLineWidth(2.5);
            gc.strokeRoundRect(-22, -20, 44, 38, 10, 10);

            gc.setFill(Color.web("#f8fafc"));
            gc.fillPolygon(new double[]{-14, 14, 8, -8}, new double[]{-18, -18, 10, 10}, 4);

            gc.setFill(Color.web("#facc15"));
            gc.fillPolygon(
                    new double[]{-18, -14, -6, 0, 6, 14, 18},
                    new double[]{-24, -44, -30, -48, -30, -44, -24},
                    7
            );

            gc.setFill(Color.web("#5eead4"));
            gc.fillOval(4, -24, 7, 5);

            gc.setStroke(Color.web("#facc15"));
            gc.setLineWidth(4.5);
            gc.strokeLine(-10, 26, 48, -32);
            gc.setStroke(Color.web("#5eead4"));
            gc.setLineWidth(3.5);
            gc.strokeLine(48, -32, 66, -50);
            gc.strokeLine(40, -38, 56, -56);
            gc.strokeLine(54, -24, 72, -40);

        } else if (stage == 3) {
            // CHEFE 3: Barão Cupcake, o Soberano do Reino Doce (com varinha e cereja)
            double squish = Math.sin(tick * 0.22) * 3.0;

            gc.setFill(Color.web("#000000", 0.35));
            gc.fillOval(-38, 38, 76, 12);

            gc.setFill(flash ? Color.WHITE : Color.web("#3b0764"));
            gc.fillPolygon(new double[]{-42, 42, 30, -30}, new double[]{2, 2, 42, 42}, 4);
            gc.setStroke(Color.web("#c084fc"));
            gc.setLineWidth(2.0);
            gc.strokePolygon(new double[]{-42, 42, 30, -30}, new double[]{2, 2, 42, 42}, 4);

            gc.setStroke(Color.web("#facc15"));
            gc.setLineWidth(2.2);
            for (int p = -24; p <= 24; p += 12) {
                gc.strokeLine(p, 4, p * 0.72, 41);
            }

            gc.setFill(Color.web("#facc15"));
            gc.fillPolygon(new double[]{0, -14, -14}, new double[]{14, 7, 21}, 3);
            gc.fillPolygon(new double[]{0, 14, 14}, new double[]{14, 7, 21}, 3);
            gc.fillOval(-4, 10, 8, 8);

            gc.setFill(flash ? Color.WHITE : Color.web("#be185d"));
            gc.fillRoundRect(-48, -14 + squish, 96, 26, 22, 22);
            gc.fillOval(-38, 4 + squish, 10, 16);
            gc.fillOval(-12, 6 + squish, 11, 18);
            gc.fillOval(18, 4 + squish, 10, 15);

            gc.setFill(flash ? Color.WHITE : Color.web("#ec4899"));
            gc.fillRoundRect(-38, -29 + squish, 76, 24, 20, 20);

            gc.setFill(flash ? Color.WHITE : Color.web("#f9a8d4"));
            gc.fillRoundRect(-26, -42 + squish, 52, 20, 16, 16);

            gc.setFill(flash ? Color.WHITE : Color.web("#fdf2f8"));
            gc.fillOval(-14, -52 + squish, 28, 16);

            gc.save();
            gc.translate(-22, -42 + squish);
            gc.rotate(-22);
            gc.setFill(Color.web("#facc15"));
            gc.fillPolygon(
                    new double[]{-12, -12, -6, 0, 6, 12, 12},
                    new double[]{4, -12, -4, -16, -4, -12, 4},
                    7
            );
            gc.setStroke(Color.web("#b45309"));
            gc.setLineWidth(1.5);
            gc.strokePolygon(
                    new double[]{-12, -12, -6, 0, 6, 12, 12},
                    new double[]{4, -12, -4, -16, -4, -12, 4},
                    7
            );
            gc.restore();

            gc.setStroke(Color.web("#16a34a"));
            gc.setLineWidth(3.0);
            gc.strokeArc(2, -74 + squish, 20, 24, 70, 120, ArcType.OPEN);
            gc.setFill(Color.web("#dc2626"));
            gc.fillOval(-11, -64 + squish, 22, 22);
            gc.setFill(Color.web("#fecaca"));
            gc.fillOval(-6, -59 + squish, 6, 6);

            drawSprinkle(-32, -20 + squish, 25, Color.web("#fef08a"));
            drawSprinkle(-16, -34 + squish, -30, Color.web("#38bdf8"));
            drawSprinkle(14, -33 + squish, 40, Color.web("#4ade80"));
            drawSprinkle(30, -21 + squish, -20, Color.web("#fef08a"));
            drawSprinkle(-28, -5 + squish, -15, Color.web("#a855f7"));

            gc.setFill(Color.web("#1e1b4b"));
            gc.fillOval(4, -11 + squish, 10, 12);
            gc.fillOval(22, -11 + squish, 10, 12);
            gc.setFill(Color.WHITE);
            gc.fillOval(6, -9 + squish, 4, 4);
            gc.fillOval(24, -9 + squish, 4, 4);
            gc.setStroke(Color.web("#4c1d95"));
            gc.setLineWidth(2.8);
            gc.strokeLine(3, -15 + squish, 14, -12 + squish);
            gc.strokeLine(21, -12 + squish, 32, -15 + squish);
            gc.setFill(Color.web("#fda4af", 0.8));
            gc.fillOval(-2, 0 + squish, 8, 5);
            gc.fillOval(30, 0 + squish, 8, 5);

            double wandBob = Math.cos(tick * 0.15) * 4;
            gc.setStroke(Color.web("#facc15"));
            gc.setLineWidth(4.0);
            gc.strokeLine(46, 20 + wandBob, 62, -22 + wandBob);
            gc.setFill(Color.web("#f472b6"));
            gc.fillOval(55, -32 + wandBob, 16, 16);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1.5);
            gc.strokeOval(55, -32 + wandBob, 16, 16);

        } else {
            // CHEFE 4: Zorgon, o Imperador Alienígena na Nave Mãe (com modo furioso / overdrive)
            boolean overdrive = boss.isAwakened();
            double hover = Math.sin(tick * 0.12) * 4;
            gc.translate(0, hover);

            gc.setFill(overdrive ? Color.web("#ef4444", 0.25) : Color.web("#22c55e", 0.22));
            gc.fillOval(-70, -45, 140, 90);

            gc.setFill(flash ? Color.WHITE : Color.web("#4ade80"));
            gc.fillOval(-14, -34, 28, 26);
            gc.fillRoundRect(-12, -12, 24, 16, 6, 6);
            gc.setStroke(Color.web("#86efac"));
            gc.setLineWidth(2.5);
            gc.strokeLine(-8, -32, -16, -44);
            gc.strokeLine(8, -32, 16, -44);
            gc.setFill(Color.web("#fef08a"));
            gc.fillOval(-19, -47, 6, 6);
            gc.fillOval(13, -47, 6, 6);

            gc.setFill(Color.BLACK);
            gc.fillOval(-10, -24, 7, 10);
            gc.fillOval(3, -24, 7, 10);
            gc.setFill(Color.WHITE);
            gc.fillOval(-8, -22, 2, 3);
            gc.fillOval(5, -22, 2, 3);

            gc.setFill(overdrive ? Color.web("#f87171", 0.35) : Color.web("#38bdf8", 0.32));
            gc.fillArc(-28, -42, 56, 52, 0, 180, ArcType.CHORD);
            gc.setStroke(overdrive ? Color.web("#fca5a5") : Color.web("#bae6fd"));
            gc.setLineWidth(2.0);
            gc.strokeArc(-28, -42, 56, 52, 0, 180, ArcType.OPEN);

            gc.setFill(flash ? Color.WHITE : Color.web("#334155"));
            gc.fillOval(-56, -8, 112, 28);
            gc.setStroke(overdrive ? Color.web("#ef4444") : Color.web("#94a3b8"));
            gc.setLineWidth(2.5);
            gc.strokeOval(-56, -8, 112, 28);

            gc.setFill(overdrive ? Color.web("#dc2626") : Color.web("#16a34a"));
            gc.fillOval(-26, 14, 52, 12);

            for (int l = -2; l <= 2; l++) {
                double lx = l * 20 + Math.sin(tick * 0.15) * 4;
                boolean blink = ((tick / 6 + l) % 2 == 0);
                gc.setFill(blink ? Color.web("#fef08a") : (overdrive ? Color.web("#ef4444") : Color.web("#4ade80")));
                gc.fillOval(lx - 4, 2, 8, 8);
            }
        }

        gc.restore();
    }

    private void drawSprinkle(double sx, double sy, double angleDeg, Color color) {
        gc.save();
        gc.translate(sx, sy);
        gc.rotate(angleDeg);
        gc.setFill(color);
        gc.fillRoundRect(-5, -2.2, 10, 4.4, 4, 4);
        gc.restore();
    }

    // Desenha o personagem Mario com skins temáticas personalizadas para cada reino
    private void drawThemedSkin(Mario m, int stage) {
        if (m.getInvulnTimer() > 0 && (m.getInvulnTimer() % 4 < 2)) return; // Efeito de piscar quando toma dano

        gc.save();
        double cx = m.getX() + m.getWidth() / 2.0;
        double footY = m.getY() + m.getHeight();
        double walkBob = m.isOnGround() ? Math.abs(Math.sin(m.getX() * 0.15)) * 3.0 : 0;
        double legSwing = m.isUnderwater()
                ? Math.sin(tick * 0.1) * 10.0
                : (m.isOnGround() && Math.abs(m.getVelX()) > 0.2 ? Math.sin(m.getX() * 0.15) * 7.0 : 0);

        gc.translate(cx, footY);
        gc.scale(m.getFacing(), 1);

        if (stage == 1) {
            // Skin Fase 1: Tema Sakura / Kimono
            gc.setFill(Color.web("#f43f5e", 0.35));
            gc.fillOval(-16, -m.getHeight() - 2 + walkBob, 32, m.getHeight() + 8);

            gc.setFill(Color.web("#18181b"));
            gc.fillRoundRect(-11, -m.getHeight() + 16 + walkBob, 22, m.getHeight() - 24, 6, 6);

            gc.setFill(Color.web("#991b1b"));
            gc.fillRoundRect(-9, -m.getHeight() + 20 + walkBob, 18, 16, 4, 4);
            gc.setFill(Color.web("#fbbf24"));
            gc.fillRect(-3, -m.getHeight() + 23 + walkBob, 6, 10);

            gc.setFill(Color.web("#581c87"));
            gc.fillPolygon(new double[]{-14, 0, 14}, new double[]{4 - m.getHeight() + walkBob, -18 - m.getHeight() + walkBob, 4 - m.getHeight() + walkBob}, 3);

            double ribbonSway = Math.sin(tick * 0.2) * 5;
            gc.setFill(Color.web("#f43f5e"));
            gc.fillPolygon(new double[]{-10, -24 + ribbonSway, -12}, new double[]{-10 - m.getHeight() + walkBob, -6 - m.getHeight() + walkBob, -4 - m.getHeight() + walkBob}, 3);

        } else if (stage == 2) {
            // Skin Fase 2: Tema Mergulhador / Atlântida
            gc.setFill(Color.web("#0e7490"));
            gc.fillRoundRect(-11, -m.getHeight() + 16 + walkBob, 22, m.getHeight() - 24, 8, 8);
            gc.setFill(Color.web("#22d3ee"));
            gc.fillOval(-5, -m.getHeight() + 24 + walkBob, 10, 10);

            gc.setFill(Color.web("#94a3b8"));
            gc.fillOval(-14, -m.getHeight() - 4 + walkBob, 28, 26);
            gc.setFill(Color.web("#a5f3fc", 0.85));
            gc.fillOval(-10, -m.getHeight() + 1 + walkBob, 20, 16);

            gc.setFill(Color.web("#334155"));
            gc.fillRoundRect(-18, -m.getHeight() + 18 + walkBob, 6, 20, 3, 3);

        } else if (stage == 3) {
            // Skin Fase 3: Tema Confeiteiro / Reino Doce
            gc.setFill(Color.web("#d97706"));
            gc.fillRoundRect(-11, -m.getHeight() + 16 + walkBob, 22, m.getHeight() - 24, 6, 6);
            gc.setFill(Color.web("#ec4899"));
            gc.fillRoundRect(-8, -m.getHeight() + 20 + walkBob, 16, 16, 4, 4);
            gc.setFill(Color.web("#fef08a"));
            gc.fillOval(-4, -m.getHeight() + 24 + walkBob, 4, 4);
            gc.fillOval(2, -m.getHeight() + 28 + walkBob, 4, 4);

            gc.setFill(Color.WHITE);
            gc.fillOval(-13, -m.getHeight() - 2 + walkBob, 26, 18);
            gc.fillOval(-9, -m.getHeight() - 10 + walkBob, 18, 14);
            gc.setFill(Color.web("#dc2626"));
            gc.fillOval(-5, -m.getHeight() - 17 + walkBob, 10, 10);

        } else {
            // Skin Fase 4: Tema Astronauta / Espacial
            gc.setFill(Color.web("#ffffff"));
            gc.fillRoundRect(-12, -m.getHeight() + 18 + walkBob, 24, m.getHeight() - 26, 6, 6);
            gc.setFill(Color.web("#38bdf8"));
            gc.fillRect(-5, -m.getHeight() + 24 + walkBob, 10, 16);

            gc.setFill(Color.web("#bae6fd"));
            gc.fillOval(-14, -m.getHeight() - 2 + walkBob, 28, 18);

            gc.setFill(Color.web("#fef08a"));
            gc.fillOval(-10, -m.getHeight() + 8 + walkBob, 20, 14);
            gc.setFill(Color.BLACK);
            gc.fillOval(-4, -m.getHeight() + 12 + walkBob, 4, 5);
            gc.fillOval(2, -m.getHeight() + 12 + walkBob, 4, 5);
        }

        gc.setStroke(Color.web("#1c1917"));
        gc.setLineWidth(5.5);
        gc.strokeLine(-4, -16, -4 + legSwing, 0);
        gc.strokeLine(4, -16, 4 + legSwing, 0);

        drawWeaponAndSlash(m, walkBob);

        gc.restore();
    }

    private void drawWeaponAndSlash(Mario m, double walkBob) {
        boolean isFire = m.getState() instanceof FireMarioState;
        boolean isSuper = m.getState() instanceof SuperMarioState;

        if (m.getAttackTimer() > 0) {
            if (isFire) {
                gc.setStroke(Color.web("#38bdf8"));
                gc.setLineWidth(4.5);
                gc.strokeLine(10, -28 + walkBob, 88, -32 + walkBob);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2.0);
                gc.strokeLine(14, -22 + walkBob, 82, -18 + walkBob);
            } else if (isSuper) {
                gc.setStroke(Color.web("#f59e0b"));
                gc.setLineWidth(7.0);
                gc.strokeArc(6, -62 + walkBob, 98, 70, -55, 130, ArcType.OPEN);
                gc.setStroke(Color.web("#fef08a"));
                gc.setLineWidth(2.5);
                gc.strokeArc(12, -56 + walkBob, 86, 58, -45, 110, ArcType.OPEN);
            } else {
                gc.setStroke(Color.web("#ef4444"));
                gc.setLineWidth(5.0);
                gc.strokeArc(8, -52 + walkBob, 72, 54, -50, 120, ArcType.OPEN);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2.0);
                gc.strokeArc(12, -48 + walkBob, 64, 46, -40, 100, ArcType.OPEN);
            }
        } else {
            gc.setStroke(isFire ? Color.web("#38bdf8") : (isSuper ? Color.web("#f59e0b") : Color.web("#e7e5e4")));
            gc.setLineWidth(3.0);
            gc.strokeLine(8, -22 + walkBob, 28, -36 + walkBob);
        }
    }

    private void drawProjectile(Projectile p) {
        String style = p.getStyle();
        if ("ALIEN_PLASMA".equals(style)) {
            gc.setFill(Color.web("#22c55e"));
            gc.fillOval(p.getX() - 3, p.getY() - 3, 22, 22);
            gc.setFill(Color.web("#fef08a"));
            gc.fillOval(p.getX() + 3, p.getY() + 3, 10, 10);
        } else if ("CONFETTI".equals(style)) {
            Color[] colors = {
                    Color.web("#ef4444"), Color.web("#facc15"),
                    Color.web("#22c55e"), Color.web("#38bdf8"), Color.web("#ec4899")
            };
            int colorIdx = (int) ((Math.abs(p.getX()) + Math.abs(p.getY())) % colors.length);
            gc.save();
            gc.translate(p.getX() + 8, p.getY() + 8);
            gc.rotate(tick * 12);
            gc.setFill(colors[colorIdx]);
            gc.fillRoundRect(-9, -5, 18, 10, 4, 4);
            gc.setStroke(Color.web("#1e1b4b"));
            gc.setLineWidth(1.2);
            gc.strokeRoundRect(-9, -5, 18, 10, 4, 4);
            gc.restore();
        } else if ("KATANA_SLASH".equals(style)) {
            gc.setStroke(Color.web("#f43f5e"));
            gc.setLineWidth(5.0);
            gc.strokeArc(p.getX() - 10, p.getY() - 18, 28, 46, -70, 140, ArcType.OPEN);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2.0);
            gc.strokeArc(p.getX() - 6, p.getY() - 14, 22, 38, -60, 120, ArcType.OPEN);
        } else if ("TRIDENT_WAVE".equals(style)) {
            gc.setStroke(Color.web("#2dd4bf"));
            gc.setLineWidth(5.0);
            gc.strokeArc(p.getX() - 12, p.getY() - 18, 32, 46, -75, 150, ArcType.OPEN);
            gc.setFill(Color.web("#facc15"));
            gc.fillOval(p.getX(), p.getY(), 14, 14);
        } else if ("WATER_ORB".equals(style)) {
            gc.setFill(Color.web("#0284c7"));
            gc.fillOval(p.getX() - 2, p.getY() - 2, 22, 22);
            gc.setStroke(Color.web("#bae6fd"));
            gc.setLineWidth(2);
            gc.strokeOval(p.getX() - 2, p.getY() - 2, 22, 22);
        } else if ("BOSS".equals(style)) {
            gc.setFill(Color.web("#ef4444"));
            gc.fillOval(p.getX(), p.getY(), p.getWidth(), p.getHeight());
            gc.setFill(Color.web("#fef08a"));
            gc.fillOval(p.getX() + 5, p.getY() + 5, 10, 10);
        } else if ("FIRE".equals(style)) {
            gc.setFill(Color.web("#38bdf8"));
            gc.fillOval(p.getX() - 4, p.getY() - 2, 22, 10);
            gc.setFill(Color.WHITE);
            gc.fillOval(p.getX(), p.getY(), 14, 6);
        } else if ("WAVE".equals(style)) {
            gc.setFill(Color.web("#f59e0b"));
            gc.fillOval(p.getX() - 6, p.getY() - 6, 26, 26);
            gc.setFill(Color.web("#fef08a"));
            gc.fillOval(p.getX(), p.getY(), 14, 14);
        } else {
            gc.setFill(Color.web("#dc2626"));
            gc.fillOval(p.getX(), p.getY(), 18, 12);
        }
    }

    // Renderiza o HUD (Painel Superior com Vidas, Pontuação, Recorde e Arma Ativa)
    private void renderHud(Mario player, int stage, int totalStages, double w) {
        gc.save();
        gc.setTransform(1, 0, 0, 1, 0, 0);

        double hudX = 18;
        double hudY = 12;
        double hudW = w - 36;
        double hudH = 48;

        gc.setFill(Color.web("#090505", 0.88));
        gc.fillRect(hudX, hudY, hudW, hudH);

        Color borderColor = stage == 1 ? Color.web("#f472b6") :
                (stage == 2 ? Color.web("#2dd4bf") :
                 (stage == 3 ? Color.web("#f472b6") : Color.web("#4ade80")));
        gc.setStroke(borderColor);
        gc.setLineWidth(1.5);
        gc.strokeRect(hudX, hudY, hudW, hudH);

        gc.setFont(FONT_HUD);

        gc.setFill(Color.web("#ef4444"));
        gc.fillText("LIVES: " + (player.isCreativeMode() ? "∞ (CREATIVE)" : "♥ ".repeat(Math.max(0, player.getLives()))), hudX + 20, hudY + 28);

        gc.setFill(Color.web("#facc15"));
        gc.fillText("SCORE: " + String.format("%06d", player.getScore()), hudX + 190, hudY + 28);

        gc.setFill(Color.web("#4ade80"));
        gc.fillText("RECORD: " + String.format("%06d", ScoreManager.loadHighScore()), hudX + 340, hudY + 28);

        gc.setFill(Color.web("#38bdf8"));
        gc.fillText("WEAPON: " + player.getState().getStateName(), hudX + 495, hudY + 28);

        gc.setFill(Color.WHITE);
        String faseTexto = "REALM 0" + stage + " / 0" + totalStages;
        gc.fillText(faseTexto, hudX + hudW - 110, hudY + 28);

        gc.setFont(FONT_HINT);
        gc.setFill(Color.web("#fde68a"));
        String hint = player.isCreativeMode()
                ? "★ CREATIVE MODE (GOD) • [N]: Next Realm | [B]: Call Boss | [1/2/3]: Weapons"
                : "[A/D]: Move | [W/Space]: Jump/Swim | [J/Z]: Mea Sakura | [K/X]: Special | [1/2/3]: Weapons";
        gc.fillText(hint, hudX + 20, hudY + 64);

        gc.restore();
    }

    // Renderiza a barra de vida do Chefe na parte inferior da tela durante as lutas
    private void renderBossHud(Koopa boss, double w) {
        double barW = 560;
        double bx = (w - barW) / 2.0;
        double by = 575;

        gc.setFill(Color.web("#090505", 0.92));
        gc.fillRect(bx - 6, by - 22, barW + 12, 36);
        gc.setStroke(Color.web("#f59e0b"));
        gc.strokeRect(bx - 6, by - 22, barW + 12, 36);

        gc.setFont(FONT_BOSS_NAME);
        gc.setFill(Color.web("#fbbf24"));
        String title = boss.isAwakened() ? boss.getBossTitle() + " [OVERDRIVE!]" : boss.getBossTitle();
        gc.fillText(title, bx, by - 7);

        double ratio = (double) boss.getHp() / boss.getMaxHp();
        gc.setFill(Color.web("#292524"));
        gc.fillRect(bx, by, barW, 10);
        gc.setFill(boss.isAwakened() ? Color.web("#22c55e") : Color.web("#dc2626"));
        gc.fillRect(bx, by, barW * Math.max(0, ratio), 10);
    }

    private void renderBossAlert(String title, double w, double h) {
        gc.setFill(Color.web("#000000", 0.68));
        gc.fillRect(0, h / 2.0 - 55, w, 100);
        gc.setStroke(Color.web("#f43f5e"));
        gc.setLineWidth(2);
        gc.strokeLine(0, h / 2.0 - 55, w, h / 2.0 - 55);
        gc.strokeLine(0, h / 2.0 + 45, w, h / 2.0 + 45);

        gc.setFont(FONT_ALERT_BIG);
        gc.setFill(Color.web("#f43f5e"));
        gc.fillText("GUARDIAN HAS AWAKENED!", w / 2.0 - 180, h / 2.0 - 15);

        gc.setFont(FONT_ALERT_SUB);
        gc.setFill(Color.web("#fbbf24"));
        gc.fillText(title, w / 2.0 - 210, h / 2.0 + 20);
    }

    private void renderOverlay(String title, String subtitle, Color color, double w, double h) {
        gc.setFill(Color.web("#000000", 0.88));
        gc.fillRect(0, 0, w, h);

        gc.save();
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(FONT_OVERLAY_BIG);
        gc.setFill(color);
        gc.fillText(title, w / 2.0, h / 2.0 - 20);

        gc.setFont(FONT_OVERLAY_SUB);
        gc.setFill(Color.WHITE);
        gc.fillText(subtitle, w / 2.0, h / 2.0 + 30);
        gc.restore();
    }
}