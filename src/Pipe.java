import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Represents a pipe obstacle in Flappy Bird game.
 */
public class Pipe {
    private double x;
    private double gapY; // Y position of the gap center
    private final double gapHeight = 150; // bigger gap for easier gameplay
    private final double width = 52;
    private final double pipeHeight = 400; // taller pipes
    private final double speed = 0.8; // slower horizontal speed

    private Image pipeTopImage;
    private Image pipeBottomImage;

    private boolean passed = false;

    public Pipe(double x, double gapY, String pipeColor) {
        this.x = x;
        this.gapY = gapY;

        try {
            pipeTopImage = new Image(getClass().getResourceAsStream("/assets/sprites/pipe-" + pipeColor + ".png"));
            pipeBottomImage = new Image(getClass().getResourceAsStream("/assets/sprites/pipe-" + pipeColor + ".png"));
        } catch (Exception e) {
            System.err.println("Error loading pipe images: " + e.getMessage());
        }
    }

    public void update() {
        x -= speed;
    }

    public void render(GraphicsContext gc, double groundY) {
        if (pipeTopImage != null && pipeBottomImage != null) {
            // Draw top pipe (upside down)
            // double topPipeY = gapY - gapHeight / 2 - pipeHeight;
            gc.save();
            gc.translate(x + width / 2, gapY - gapHeight / 2);
            gc.scale(1, -1);
            gc.drawImage(pipeTopImage, -width / 2, 0, width, pipeHeight);
            gc.restore();

            // Draw bottom pipe
            double bottomPipeY = gapY + gapHeight / 2;
            gc.drawImage(pipeBottomImage, x, bottomPipeY, width, pipeHeight);
        }
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public boolean collidesWith(Bird bird) {
        double birdLeft = bird.getX();
        double birdRight = bird.getX() + bird.getWidth();
        double birdTop = bird.getY();
        double birdBottom = bird.getY() + bird.getHeight();

        double pipeLeft = x;
        double pipeRight = x + width;

        // Check if bird is horizontally aligned with pipe
        if (birdRight > pipeLeft && birdLeft < pipeRight) {
            // Check if bird hits top or bottom pipe
            double topPipeBottom = gapY - gapHeight / 2;
            double bottomPipeTop = gapY + gapHeight / 2;

            if (birdTop < topPipeBottom || birdBottom > bottomPipeTop) {
                return true;
            }
        }

        return false;
    }

    public boolean hasPassed(Bird bird) {
        if (!passed && bird.getX() > x + width) {
            passed = true;
            return true;
        }
        return false;
    }

    public double getX() {
        return x;
    }
}
