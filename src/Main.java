import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The entry point of Flappy Bird game.
 * Launches the JavaFX application and starts the FlappyBirdGame.
 */

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        FlappyBirdGame game = new FlappyBirdGame(); // Create an instance of Flappy Bird
        game.start(primaryStage); // Start the game
    }

    public static void main(String[] args) {
        launch(args); // Launch JavaFX
    }
}
