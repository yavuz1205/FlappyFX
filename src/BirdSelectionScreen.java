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
    private final int animationSpeed = 8;
    
    private double mouseX = -1;
    private double mouseY = -1;
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
            
            flappyFont = Font.loadFont(getClass().getResourceAsStream("/assets/FlappybirdyRegular-KaBW.ttf"), 36);
            flappyFontSmall = Font.loadFont(getClass().getResourceAsStream("/assets/FlappybirdyRegular-KaBW.ttf"), 16);
            flappyFontMedium = Font.loadFont(getClass().getResourceAsStream("/assets/FlappybirdyRegular-KaBW.ttf"), 24);
            
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
        double panelWidth = 500;
        double panelHeight = 380;
        double panelX = 50;
        double panelY = 210;
        
        double birdCardWidth = 140;
        double birdCardHeight = 180;
        double spacing = 40;
        
        double startX = panelX + (panelWidth - (birdCardWidth * 3 + spacing * 2)) / 2;
        double cardY = panelY + 100;
        
        birdButtons = new BirdButton[3];
        birdButtons[0] = new BirdButton(startX, cardY, birdCardWidth, birdCardHeight, "yellow", "Sunny");
        birdButtons[1] = new BirdButton(startX + birdCardWidth + spacing, cardY, birdCardWidth, birdCardHeight, "blue", "Sky");
        birdButtons[2] = new BirdButton(startX + (birdCardWidth + spacing) * 2, cardY, birdCardWidth, birdCardHeight, "red", "Ruby");
    }
    
    public void updateAnimation() {
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            animationFrame = (animationFrame + 1) % 3;
            animationCounter = 0;
        }
    }
    
    public void updateMousePosition(double mx, double my) {
        this.mouseX = mx;
        this.mouseY = my;
        
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
        
        double panelWidth = 500;
        double panelHeight = 380;
        double panelX = (canvasWidth - panelWidth) / 2;
        double panelY = (canvasHeight - panelHeight) / 2;
        
        gc.setFill(Color.rgb(255, 255, 255, 0.95));
        gc.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 25, 25);
        
        gc.setFill(Color.rgb(30, 30, 30));
        gc.setFont(flappyFont);
        String title = "CHOOSE YOUR BIRD";
        double titleWidth = 280;
        gc.fillText(title, panelX + (panelWidth - titleWidth) / 2, panelY + 55);
        
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
        
        gc.setFill(Color.rgb(120, 120, 130));
        gc.setFont(flappyFontSmall);
        String hint = "CLICK ON A BIRD TO SELECT";
        double hintWidth = 220;
        gc.fillText(hint, panelX + (panelWidth - hintWidth) / 2, panelY + panelHeight - 25);
    }
    
    private Image[] getBirdImages(String color) {
        switch (color) {
            case "yellow": return yellowBirdImages;
            case "blue": return blueBirdImages;
            case "red": return redBirdImages;
            default: return null;
        }
    }
}

