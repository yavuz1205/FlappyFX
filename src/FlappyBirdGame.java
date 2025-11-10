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
    
    private Font flappyFont;
    private Font flappyFontSmall;
    private Font flappyFontMedium;

    private int score = 0;
    private int highScore = 0;
    private boolean gameStarted = false;
    private boolean gameOver = false;

    private double groundX = 0;
    private final double groundSpeed = .2;

    private int frameCount = 0;
    private final int pipeSpawnInterval = 1000;
    private String birdColor;
    
    private BirdSelectionScreen selectionScreen;
    private boolean showingBirdSelection = false;
    
    private static class ChangeBirdButton {
        double x, y, width, height;
        
        ChangeBirdButton(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        boolean contains(double mx, double my) {
            return mx >= x && mx <= x + width && my >= y && my <= y + height;
        }
    }
    
    private ChangeBirdButton changeBirdButton;

    public FlappyBirdGame(String birdColor) {
        this.birdColor = birdColor;
        random = new Random();
        pipes = new ArrayList<>();
        loadAssets();
        selectionScreen = new BirdSelectionScreen();
        
        double buttonWidth = 160;
        double buttonHeight = 55;
        changeBirdButton = new ChangeBirdButton(WIDTH - buttonWidth - 15, HEIGHT - buttonHeight - 15, buttonWidth, buttonHeight);
    }

    private void loadAssets() {
        try {
            String bgType = random.nextBoolean() ? "day" : "night";
            backgroundImage = new Image(
                    getClass().getResourceAsStream("/assets/sprites/background-" + bgType + ".png"));

            groundImage = new Image(getClass().getResourceAsStream("/assets/sprites/base.png"));

            gameOverImage = new Image(getClass().getResourceAsStream("/assets/sprites/gameover.png"));
            messageImage = new Image(getClass().getResourceAsStream("/assets/sprites/message.png"));

            numberImages = new Image[10];
            for (int i = 0; i < 10; i++) {
                numberImages[i] = new Image(getClass().getResourceAsStream("/assets/sprites/" + i + ".png"));
            }
            
            flappyFont = Font.loadFont(getClass().getResourceAsStream("/assets/FlappybirdyRegular-KaBW.ttf"), 36);
            flappyFontSmall = Font.loadFont(getClass().getResourceAsStream("/assets/FlappybirdyRegular-KaBW.ttf"), 20);
            flappyFontMedium = Font.loadFont(getClass().getResourceAsStream("/assets/FlappybirdyRegular-KaBW.ttf"), 24);
            
            if (flappyFont == null || flappyFontSmall == null || flappyFontMedium == null) {
                System.err.println("Error loading font, using default");
                flappyFont = Font.font("Arial", FontWeight.BOLD, 36);
                flappyFontSmall = Font.font("Arial", FontWeight.BOLD, 18);
                flappyFontMedium = Font.font("Arial", FontWeight.BOLD, 24);
            } else {
                System.out.println("Flappy Bird font loaded successfully!");
            }

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

        bird = new Bird(WIDTH / 4, HEIGHT / 2, birdColor);

        // Input handling
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                handleJump();
            } else if (e.getCode() == KeyCode.R && gameOver) {
                resetGame();
            }
        });

        scene.setOnMouseClicked(e -> handleMouseClick(e.getX(), e.getY()));
        
        scene.setOnMouseMoved(e -> {
            if (showingBirdSelection) {
                selectionScreen.updateMousePosition(e.getX(), e.getY());
            }
        });

        primaryStage.setTitle("Flappy Bird");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        startGameLoop();
    }

    private void handleMouseClick(double x, double y) {
        if (showingBirdSelection) {
            String selectedColor = selectionScreen.checkBirdClick(x, y);
            if (selectedColor != null) {
                birdColor = selectedColor;
                bird = new Bird(WIDTH / 4, bird.getY(), birdColor);
                bird.reset(bird.getY());
                showingBirdSelection = false;
            }
        } else if (!gameStarted && changeBirdButton.contains(x, y)) {
            showingBirdSelection = true;
        } else {
            handleJump();
        }
    }
    
    private void handleJump() {
        if (showingBirdSelection) return;
        
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
        if (showingBirdSelection) {
            selectionScreen.updateAnimation();
            return;
        }
        
        if (gameOver) {
            return;
        }

        if (!gameStarted) {
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

        if (frameCount % pipeSpawnInterval == 0) {
            double minGapY = 180;
            double maxGapY = HEIGHT - GROUND_HEIGHT - 180;
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

        // Check ground and ceiling collision (grace period after start)
        if (bird.getY() + bird.getHeight() >= HEIGHT - GROUND_HEIGHT || bird.getY() <= -5) {
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
        
        if (!gameStarted && !showingBirdSelection) {
            drawChangeBirdButton();
        }
        
        if (showingBirdSelection) {
            selectionScreen.render(gc, WIDTH, HEIGHT);
        }
    }
    
    private void drawChangeBirdButton() {
        gc.setFill(Color.rgb(255, 200, 50));
        gc.fillRoundRect(changeBirdButton.x, changeBirdButton.y, changeBirdButton.width, changeBirdButton.height, 10, 10);
        
        gc.setFill(Color.rgb(255, 150, 0));
        gc.fillRoundRect(changeBirdButton.x + 3, changeBirdButton.y + 3, changeBirdButton.width - 6, changeBirdButton.height - 6, 8, 8);
        
        gc.setStroke(Color.rgb(150, 80, 0));
        gc.setLineWidth(3);
        gc.strokeRoundRect(changeBirdButton.x, changeBirdButton.y, changeBirdButton.width, changeBirdButton.height, 10, 10);
        
        gc.setFill(Color.WHITE);
        gc.setFont(flappyFontSmall);
        
        javafx.scene.text.Text text = new javafx.scene.text.Text("CHANGE BIRD");
        text.setFont(flappyFontSmall);
        double textWidth = text.getLayoutBounds().getWidth();
        double textX = changeBirdButton.x + (changeBirdButton.width - textWidth) / 2;
        double textY = changeBirdButton.y + changeBirdButton.height / 2 + 6;
        
        gc.fillText("CHANGE BIRD", textX, textY);
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
