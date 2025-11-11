import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Represents the bird character in Flappy Bird game.
 */
public class Bird {
    private double x, y;
    private double velocity;
    private final double gravity = 0.005;
    private final double jumpStrength = -1.0;
    private final double maxVelocity = 12.0;

    private Image[] birdImages;
    private int currentFrame = 0;
    private int animationCounter = 0;
    private final int animationSpeed = 20;

    private final double width = 44;
    private final double height = 32;

    public Bird(double x, double y, String birdColor) {
        this.x = x;
        this.y = y;
        this.velocity = 0;

        // Load bird animation frames
        try {
            birdImages = new Image[3];
            birdImages[0] = new Image(
                    getClass().getResourceAsStream("/assets/sprites/" + birdColor + "bird-downflap.png"));
            birdImages[1] = new Image(
                    getClass().getResourceAsStream("/assets/sprites/" + birdColor + "bird-midflap.png"));
            birdImages[2] = new Image(
                    getClass().getResourceAsStream("/assets/sprites/" + birdColor + "bird-upflap.png"));
        } catch (Exception e) {
            System.err.println("Error loading bird images: " + e.getMessage());
        }
    }

    public void jump() {
        velocity = jumpStrength;
    }

    public void update() {
        velocity += gravity;

        // Limit maximum falling speed
        if (velocity > maxVelocity) {
            velocity = maxVelocity;
        }

        y += velocity;

        // Update animation
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            currentFrame = (currentFrame + 1) % 3;
            animationCounter = 0;
        }
    }

    public void render(GraphicsContext gc) {
        if (birdImages != null && birdImages[currentFrame] != null) {
            gc.save();

            // Rotate bird based on velocity
            double rotation = Math.min(Math.max(velocity * 2, -30), 90);
            gc.translate(x + width / 2, y + height / 2);
            gc.rotate(rotation);
            gc.drawImage(birdImages[currentFrame], -width / 2, -height / 2, width, height);

            gc.restore();
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void reset(double startY) {
        this.y = startY;
        this.velocity = 0;
    }
}
