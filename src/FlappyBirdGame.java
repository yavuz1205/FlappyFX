import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.media.AudioClip;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main game class for Flappy Bird.
 */
public class FlappyBirdGame {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;
    private static final double GROUND_HEIGHT = 112;

    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    private Bird bird;
    private List<Pipe> pipes;
    private Random random;

    private Image backgroundImage;
    private Image groundImage;
    private Image gameOverImage;
    private Image messageImage;
    private Image[] numberImages;

    private AudioClip jumpSound;
    private AudioClip pointSound;
    private AudioClip hitSound;
    private AudioClip dieSound;
    private AudioClip swooshSound;

    private int score = 0;
    private int highScore = 0;
    private boolean gameStarted = false;
    private boolean gameOver = false;

    private double groundX = 0;
    private final double groundSpeed = 2;

    private int frameCount = 0;
    private final int pipeSpawnInterval = 90; // frames

    public FlappyBirdGame() {
        random = new Random();
        pipes = new ArrayList<>();
        loadAssets();
    }

    private void loadAssets() {
        try {
            // Load background (day or night randomly)
            String bgType = random.nextBoolean() ? "day" : "night";
            backgroundImage = new Image(
                    getClass().getResourceAsStream("/assets/sprites/background-" + bgType + ".png"));

            // Load ground
            groundImage = new Image(getClass().getResourceAsStream("/assets/sprites/base.png"));

            // Load game over and message
            gameOverImage = new Image(getClass().getResourceAsStream("/assets/sprites/gameover.png"));
            messageImage = new Image(getClass().getResourceAsStream("/assets/sprites/message.png"));

            // Load number images for score
            numberImages = new Image[10];
            for (int i = 0; i < 10; i++) {
                numberImages[i] = new Image(getClass().getResourceAsStream("/assets/sprites/" + i + ".png"));
            }

            // Load sounds
            jumpSound = new AudioClip(getClass().getResource("/assets/audio/wing.wav").toString());
            pointSound = new AudioClip(getClass().getResource("/assets/audio/point.wav").toString());
            hitSound = new AudioClip(getClass().getResource("/assets/audio/hit.wav").toString());
            dieSound = new AudioClip(getClass().getResource("/assets/audio/die.wav").toString());
            swooshSound = new AudioClip(getClass().getResource("/assets/audio/swoosh.wav").toString());

        } catch (Exception e) {
            System.err.println("Error loading assets: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void start(Stage primaryStage) {
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // Bird starts at center
        String[] birdColors = { "yellow", "blue", "red" };
        String birdColor = birdColors[random.nextInt(birdColors.length)];
        bird = new Bird(WIDTH / 4, HEIGHT / 2, birdColor);

        // Input handling
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                handleJump();
            } else if (e.getCode() == KeyCode.R && gameOver) {
                resetGame();
            }
        });

        scene.setOnMouseClicked(e -> handleJump());

        primaryStage.setTitle("Flappy Bird");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        startGameLoop();
    }

    private void handleJump() {
        if (!gameStarted) {
            gameStarted = true;
            if (swooshSound != null) {
                swooshSound.play();
            }
        }

        if (!gameOver) {
            bird.jump();
            if (jumpSound != null) {
                jumpSound.play();
            }
        }
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
        gameLoop.start();
    }

    private void update() {
        if (!gameStarted || gameOver) {
            return;
        }

        frameCount++;

        // Update bird
        bird.update();

        // Update ground
        groundX -= groundSpeed;
        if (groundX <= -groundImage.getWidth()) {
            groundX = 0;
        }

        // Spawn pipes
        if (frameCount % pipeSpawnInterval == 0) {
            double minGapY = 150;
            double maxGapY = HEIGHT - GROUND_HEIGHT - 150;
            double gapY = minGapY + random.nextDouble() * (maxGapY - minGapY);

            String pipeColor = random.nextBoolean() ? "green" : "red";
            pipes.add(new Pipe(WIDTH, gapY, pipeColor));
        }

        // Update pipes
        List<Pipe> pipesToRemove = new ArrayList<>();
        for (Pipe pipe : pipes) {
            pipe.update();

            // Check collision
            if (pipe.collidesWith(bird)) {
                endGame();
            }

            // Check if passed
            if (pipe.hasPassed(bird)) {
                score++;
                if (pointSound != null) {
                    pointSound.play();
                }
            }

            // Remove off-screen pipes
            if (pipe.isOffScreen()) {
                pipesToRemove.add(pipe);
            }
        }
        pipes.removeAll(pipesToRemove);

        // Check ground and ceiling collision
        if (bird.getY() + bird.getHeight() >= HEIGHT - GROUND_HEIGHT || bird.getY() <= 0) {
            endGame();
        }
    }

    private void render() {
        // Draw background
        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT);
        } else {
            gc.setFill(Color.SKYBLUE);
            gc.fillRect(0, 0, WIDTH, HEIGHT);
        }

        // Draw pipes
        for (Pipe pipe : pipes) {
            pipe.render(gc, HEIGHT - GROUND_HEIGHT);
        }

        // Draw ground
        if (groundImage != null) {
            gc.drawImage(groundImage, groundX, HEIGHT - GROUND_HEIGHT);
            gc.drawImage(groundImage, groundX + groundImage.getWidth(), HEIGHT - GROUND_HEIGHT);
        } else {
            gc.setFill(Color.BROWN);
            gc.fillRect(0, HEIGHT - GROUND_HEIGHT, WIDTH, GROUND_HEIGHT);
        }

        // Draw bird
        bird.render(gc);

        // Draw score
        drawScore();

        // Draw UI overlays
        if (!gameStarted) {
            if (messageImage != null) {
                gc.drawImage(messageImage, (WIDTH - 184) / 2, HEIGHT / 3);
            } else {
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                gc.fillText("Press SPACE or Click to Start", 30, HEIGHT / 2);
            }
        }

        if (gameOver) {
            if (gameOverImage != null) {
                gc.drawImage(gameOverImage, (WIDTH - 192) / 2, HEIGHT / 3);
            }

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.fillText("Score: " + score, WIDTH / 2 - 30, HEIGHT / 2 + 50);
            gc.fillText("High Score: " + highScore, WIDTH / 2 - 50, HEIGHT / 2 + 70);
            gc.fillText("Press R to Restart", WIDTH / 2 - 60, HEIGHT / 2 + 100);
        }
    }

    private void drawScore() {
        if (!gameOver && gameStarted) {
            String scoreStr = String.valueOf(score);
            int digitWidth = 24;
            int totalWidth = scoreStr.length() * digitWidth;
            int startX = (WIDTH - totalWidth) / 2;

            if (numberImages[0] != null) {
                for (int i = 0; i < scoreStr.length(); i++) {
                    int digit = Character.getNumericValue(scoreStr.charAt(i));
                    gc.drawImage(numberImages[digit], startX + i * digitWidth, 50);
                }
            } else {
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 40));
                gc.fillText(scoreStr, WIDTH / 2 - 20, 60);
            }
        }
    }

    private void endGame() {
        if (!gameOver) {
            gameOver = true;
            if (hitSound != null) {
                hitSound.play();
            }
            if (dieSound != null) {
                dieSound.play();
            }

            if (score > highScore) {
                highScore = score;
            }
        }
    }

    private void resetGame() {
        gameOver = false;
        gameStarted = false;
        score = 0;
        frameCount = 0;
        pipes.clear();
        bird.reset(HEIGHT / 2);

        if (swooshSound != null) {
            swooshSound.play();
        }
    }
}
