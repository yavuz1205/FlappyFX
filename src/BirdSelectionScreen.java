import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BirdSelectionScreen {
    private Image[] yellowBirdImages;
    private Image[] blueBirdImages;
    private Image[] redBirdImages;

    private int animationFrame = 0;
    private int animationCounter = 0;
    private final int animationSpeed = 15;

    private int hoveredBirdIndex = -1;

    private Font flappyFont;
    private Font flappyFontSmall;
    private Font flappyFontMedium;

    private static class BirdButton {
        double x, y, width, height;
        String color;
        String name;

        BirdButton(double x, double y, double width, double height, String color, String name) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.name = name;
        }

        boolean contains(double mx, double my) {
            return mx >= x && mx <= x + width && my >= y && my <= y + height;
        }
    }

    private BirdButton[] birdButtons;

    public BirdSelectionScreen() {
        loadAssets();
        setupBirdButtons();
    }

    private void loadAssets() {
        try {
            yellowBirdImages = new Image[3];
            yellowBirdImages[0] = new Image(getClass().getResourceAsStream("/assets/sprites/yellowbird-downflap.png"));
            yellowBirdImages[1] = new Image(getClass().getResourceAsStream("/assets/sprites/yellowbird-midflap.png"));
            yellowBirdImages[2] = new Image(getClass().getResourceAsStream("/assets/sprites/yellowbird-upflap.png"));

            blueBirdImages = new Image[3];
            blueBirdImages[0] = new Image(getClass().getResourceAsStream("/assets/sprites/bluebird-downflap.png"));
            blueBirdImages[1] = new Image(getClass().getResourceAsStream("/assets/sprites/bluebird-midflap.png"));
            blueBirdImages[2] = new Image(getClass().getResourceAsStream("/assets/sprites/bluebird-upflap.png"));

            redBirdImages = new Image[3];
            redBirdImages[0] = new Image(getClass().getResourceAsStream("/assets/sprites/redbird-downflap.png"));
            redBirdImages[1] = new Image(getClass().getResourceAsStream("/assets/sprites/redbird-midflap.png"));
            redBirdImages[2] = new Image(getClass().getResourceAsStream("/assets/sprites/redbird-upflap.png"));

            flappyFont = Font.loadFont(getClass().getResourceAsStream("/assets/FlappyBirdy.ttf"), 36);
            flappyFontSmall = Font.loadFont(getClass().getResourceAsStream("/assets/FlappyBirdy.ttf"), 16);
            flappyFontMedium = Font.loadFont(getClass().getResourceAsStream("/assets/FlappyBirdy.ttf"), 24);

            if (flappyFont == null || flappyFontSmall == null || flappyFontMedium == null) {
                System.err.println("Error loading font in BirdSelectionScreen, using default");
                flappyFont = Font.font("Arial", FontWeight.BOLD, 36);
                flappyFontSmall = Font.font("Arial", FontWeight.BOLD, 16);
                flappyFontMedium = Font.font("Arial", FontWeight.BOLD, 24);
            } else {
                System.out.println("Flappy Bird font loaded successfully in BirdSelectionScreen!");
            }

        } catch (Exception e) {
            System.err.println("Error loading selection screen assets: " + e.getMessage());
        }
    }

    private void setupBirdButtons() {
        double panelWidth = 540;
        double panelX = 30;
        double panelY = 210;

        double birdCardWidth = 150;
        double birdCardHeight = 180;
        double spacing = 30;

        double startX = panelX + (panelWidth - (birdCardWidth * 3 + spacing * 2)) / 2;
        double cardY = panelY + 100;

        birdButtons = new BirdButton[3];
        birdButtons[0] = new BirdButton(startX, cardY, birdCardWidth, birdCardHeight, "yellow", "Sunny");
        birdButtons[1] = new BirdButton(startX + birdCardWidth + spacing, cardY, birdCardWidth, birdCardHeight, "blue",
                "Sky");
        birdButtons[2] = new BirdButton(startX + (birdCardWidth + spacing) * 2, cardY, birdCardWidth, birdCardHeight,
                "red", "Ruby");
    }

    public void updateAnimation() {
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            animationFrame = (animationFrame + 1) % 3;
            animationCounter = 0;
        }
    }

    public void updateMousePosition(double mx, double my) {

        hoveredBirdIndex = -1;
        for (int i = 0; i < birdButtons.length; i++) {
            if (birdButtons[i].contains(mx, my)) {
                hoveredBirdIndex = i;
                break;
            }
        }
    }

    public String checkBirdClick(double mx, double my) {
        for (BirdButton button : birdButtons) {
            if (button.contains(mx, my)) {
                return button.color;
            }
        }
        return null;
    }

    public void render(GraphicsContext gc, int canvasWidth, int canvasHeight) {
        gc.setFill(Color.rgb(0, 0, 0, 0.75));
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        double panelWidth = 540;
        double panelHeight = 380;
        double panelX = (canvasWidth - panelWidth) / 2;
        double panelY = (canvasHeight - panelHeight) / 2;

        gc.setFill(Color.rgb(255, 255, 255, 0.95));
        gc.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 25, 25);

        gc.setFill(Color.rgb(30, 30, 30));

        double titleSize = Math.max(28, Math.min(40, panelWidth * 0.07));
        Font titleFont = Font.font(flappyFont.getFamily(), titleSize);
        gc.setFont(titleFont);

        String title = "CHOOSE YOUR BIRD";
        javafx.scene.text.Text titleNode = new javafx.scene.text.Text(title);
        titleNode.setFont(titleFont);
        double titleW = titleNode.getLayoutBounds().getWidth();

        gc.fillText(title, panelX + (panelWidth - titleW) / 2, panelY + 60);

        for (int i = 0; i < birdButtons.length; i++) {
            BirdButton button = birdButtons[i];
            boolean isHovered = hoveredBirdIndex == i;

            double cardX = button.x;
            double cardY = button.y;
            double cardWidth = button.width;
            double cardHeight = button.height;

            if (isHovered) {
                cardY -= 8;

                gc.setFill(Color.rgb(0, 0, 0, 0.2));
                gc.fillRoundRect(cardX + 4, cardY + 8, cardWidth, cardHeight, 15, 15);
            }

            if (isHovered) {
                gc.setFill(Color.rgb(100, 200, 255, 0.3));
            } else {
                gc.setFill(Color.rgb(245, 245, 250));
            }
            gc.fillRoundRect(cardX, cardY, cardWidth, cardHeight, 15, 15);

            if (isHovered) {
                gc.setStroke(Color.rgb(50, 150, 255));
                gc.setLineWidth(3);
            } else {
                gc.setStroke(Color.rgb(200, 200, 210));
                gc.setLineWidth(2);
            }
            gc.strokeRoundRect(cardX, cardY, cardWidth, cardHeight, 15, 15);

            Image[] birdImages = getBirdImages(button.color);
            if (birdImages != null && birdImages[animationFrame] != null) {
                double birdScale = isHovered ? 2.2 : 2.0;
                double birdWidth = 44 * birdScale;
                double birdHeight = 32 * birdScale;
                double imgX = cardX + (cardWidth - birdWidth) / 2;
                double imgY = cardY + 40;

                gc.drawImage(birdImages[animationFrame], imgX, imgY, birdWidth, birdHeight);
            }

            gc.setFill(Color.rgb(50, 50, 60));
            gc.setFont(flappyFontMedium);
            double nameWidth = button.name.length() * 13;
            gc.fillText(button.name, cardX + (cardWidth - nameWidth) / 2, cardY + cardHeight - 25);
        }

        String hint = "Click on a bird to select";

        Font hintFont = Font.font(flappyFont.getFamily(), 40);
        gc.setFont(hintFont);

        javafx.scene.text.Text hintNode = new javafx.scene.text.Text(hint);
        hintNode.setFont(hintFont);
        double hintW = hintNode.getLayoutBounds().getWidth();
        double hintX = panelX + (panelWidth - hintW) / 2;
        double hintY = panelY + panelHeight - 28;

        gc.setFill(Color.rgb(0, 0, 0, 0.15));
        gc.fillText(hint, hintX + 1, hintY + 1);

        gc.setFill(Color.rgb(90, 100, 120));
        gc.fillText(hint, hintX, hintY);
    }

    private Image[] getBirdImages(String color) {
        switch (color) {
            case "yellow":
                return yellowBirdImages;
            case "blue":
                return blueBirdImages;
            case "red":
                return redBirdImages;
            default:
                return null;
        }
    }
}
