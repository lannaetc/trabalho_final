package org.example.trabalho_final;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.trabalho_final.controller.GamePlayController;
import org.example.trabalho_final.view.GameView;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(1000, 620);
        GameView gameView = new GameView(canvas);
        GamePlayController controller = new GamePlayController(gameView);

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, 1000, 620);

        scene.setOnKeyPressed(controller::handleKeyPressed);
        scene.setOnKeyReleased(controller::handleKeyReleased);

        primaryStage.setTitle("MVC Platformer // State & Factory Architecture");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}