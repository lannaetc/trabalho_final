package org.example.trabalho_final.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.example.trabalho_final.model.Level;
import org.example.trabalho_final.model.Mario;
import org.example.trabalho_final.model.ScoreManager;
import org.example.trabalho_final.model.state.MarioState;
import org.example.trabalho_final.view.GameView;

import java.util.HashSet;
import java.util.Set;

/**
 * Controller do jogo! Basicamente é o cérebro que liga o teclado do usuário
 * com as regras de negócio (Model) e manda a View desenhar tudo na tela.
 */
public class GamePlayController {
    private static final int TOTAL_STAGES = 4;
    private static final String SECRET_CODE = "ALANA"; // Nosso easter egg secreto hehe, só digitar meu nome e fica no modo criativo.

    private AnimationTimer gameLoop; // O loop que roda a 60fps atualizando o jogo
    private Level currentLevel;       // A fase atual onde o player está sofrendo
    private Set<KeyCode> activeKeys;  // Guarda as teclas que tô apertando junto (pra andar e pular ao mesmo tempo)
    private GameView gameView;        // Quem desenha os gráficos no Canvas
    private int currentStage = 1;
    private boolean onTitleScreen = true;
    private boolean victory = false;
    private boolean creativeUnlocked = false;
    private StringBuilder typedBuffer = new StringBuilder(); // Juntei as letras aqui pra ver se digitam "ALANA"

    // Construtor: inicializo as listas de teclas e chamo o start
    public GamePlayController(GameView gameView) {
        this.gameView = gameView;
        this.activeKeys = new HashSet<>();
        initialize();
    }

    // Reseta ou inicia o jogo do zero
    public void initialize() {
        currentStage = 1;
        victory = false;
        currentLevel = new Level(currentStage);
        currentLevel.getPlayer().setCreativeMode(creativeUnlocked);

        if (gameLoop != null) gameLoop.stop();

        // Aqui o game loop roda sem parar atualizando os quadros
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();
    }

    // Quando aperto alguma tecla no teclado
    public void handleKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();
        activeKeys.add(code);

        // O código secreto do Modo Criativo! Se digitar A-L-A-N-A, ativa o cheat
        if (code.isLetterKey()) {
            typedBuffer.append(code.getName().toUpperCase());
            if (typedBuffer.length() > SECRET_CODE.length()) {
                typedBuffer.delete(0, typedBuffer.length() - SECRET_CODE.length());
            }
            if (typedBuffer.toString().equals(SECRET_CODE)) {
                boolean nextState = !currentLevel.getPlayer().isCreativeMode();
                creativeUnlocked = nextState;
                currentLevel.getPlayer().setCreativeMode(nextState);
                typedBuffer.setLength(0);
            }
        }

        // Na tela inicial, dar Enter ou Espaço começa o jogo
        if (onTitleScreen) {
            if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
                onTitleScreen = false;
                initialize();
            }
            return;
        }

        Mario player = currentLevel.getPlayer();

        // Apertei R? Reseta a fase
        if (code == KeyCode.R) {
            initialize();
            return;
        }

        // Apertei ESC? Volta pra tela de título
        if (code == KeyCode.ESCAPE) {
            onTitleScreen = true;
            return;
        }

        // Cheats extras só funcionam se o modo criativo estiver ativo
        if (player.isCreativeMode()) {
            if (code == KeyCode.N) { // Pula pra próxima fase direto (tecla N)
                MarioState savedState = player.getState();
                currentStage = (currentStage % TOTAL_STAGES) + 1;
                currentLevel = new Level(currentStage);
                currentLevel.getPlayer().setCreativeMode(true);
                currentLevel.getPlayer().changeState(savedState);
                return;
            }
            if (code == KeyCode.B) { // Força o chefe a aparecer (tecla B)
                currentLevel.forceSpawnBossForDemo();
                return;
            }
        }

        if (player.getLives() <= 0 || victory) return;

        // Pulos (W, Seta pra cima ou Espaço)
        if (code == KeyCode.W || code == KeyCode.UP || code == KeyCode.SPACE) {
            player.jump();
        }

        // Ataques normais (J ou Z)
        if (code == KeyCode.J || code == KeyCode.Z) {
            player.triggerAttack();
        }

        // Ataque especial (K ou X)
        if (code == KeyCode.K || code == KeyCode.X) {
            currentLevel.performSpecialAttack();
        }

        // Troca direta de armas pelas teclas 1, 2 e 3
        if (code == KeyCode.DIGIT1) currentLevel.equipWeaponDirectly(1);
        if (code == KeyCode.DIGIT2) currentLevel.equipWeaponDirectly(2);
        if (code == KeyCode.DIGIT3) currentLevel.equipWeaponDirectly(3);
    }

    // Tirei o dedo da tecla? Remove do set de teclas ativas
    public void handleKeyReleased(KeyEvent event) {
        activeKeys.remove(event.getCode());
    }

    // O update roda a cada frame: atualiza a física e manda desenhar
    public void update() {
        if (onTitleScreen) {
            gameView.renderTitleScreen();
            return;
        }

        Mario player = currentLevel.getPlayer();
        boolean isGameOver = player.getLives() <= 0;

        if (!isGameOver && !victory) {
            // Movimentação fluida pra esquerda e direita (A/D ou Setas)
            if (activeKeys.contains(KeyCode.A) || activeKeys.contains(KeyCode.LEFT)) {
                player.setVelX(-4.8);
                player.setFacing(-1);
            } else if (activeKeys.contains(KeyCode.D) || activeKeys.contains(KeyCode.RIGHT)) {
                player.setVelX(4.8);
                player.setFacing(1);
            } else {
                player.setVelX(0);
            }

            // Atualizo toda a lógica da fase (colisões, inimigos, etc)
            currentLevel.update();

            // Salvo o high score automaticamente
            ScoreManager.saveHighScore(player.getScore());

            // Passou de fase? Bora carregar o próximo reino!
            if (currentLevel.isStageCleared()) {
                if (currentStage < TOTAL_STAGES) {
                    // Guardo os status do player pra não perder vida nem pontos ao mudar de fase
                    int savedLives = player.getLives();
                    int savedScore = player.getScore();
                    MarioState savedState = player.getState();
                    boolean keepCreative = player.isCreativeMode();

                    currentStage++;
                    currentLevel = new Level(currentStage);
                    currentLevel.getPlayer().setCreativeMode(keepCreative);
                    currentLevel.getPlayer().setLives(savedLives);
                    currentLevel.getPlayer().changeState(savedState);
                    currentLevel.getPlayer().addScore(savedScore);
                } else {
                    victory = true; // Zerei o jogo todo!
                }
            }
        }

        // Mando a View desenhar tudo na tela bonitinho
        gameView.render(currentLevel, TOTAL_STAGES, isGameOver, victory);
    }
}