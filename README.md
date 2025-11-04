# Flappy Bird - JavaFX Edition

Classic Flappy Bird game implemented in JavaFX with smooth animations and sound effects.

## 🎮 How to Play

- **SPACE** or **Click Mouse**: Make the bird jump
- **R**: Restart game (when game over)

Navigate through the pipes without hitting them or the ground. Each pipe you pass increases your score!

## 🎯 Game Features

- Smooth bird animations with different color variations (yellow, red, blue)
- Dynamic pipe generation with random heights
- Day and night background variations
- Sound effects for jumping, scoring, and collisions
- High score tracking
- Animated ground scrolling

## 🏃 Running the Game

Make sure you have JavaFX installed and configured. Then run:

```bash
java Main.java
```

## 📁 Project Structure

The project's source code is located in the `src` directory.

- `src/`: Main directory for all source code.
  - `Bird.java`: Bird character with physics and animation
  - `Pipe.java`: Pipe obstacles with collision detection
  - `FlappyBirdGame.java`: Main game logic and rendering
  - `Main.java`: Application entry point
  - `assets/`: Contains all game assets
    - `sprites/`: Images for bird, pipes, background, ground, and UI
    - `audio/`: Sound effects (WAV and OGG formats)

## 🎨 Assets

All sprites and sounds are included in the `assets` folder:

- Bird animations (3 colors × 3 frames each)
- Pipe graphics (green and red)
- Backgrounds (day and night)
- Number sprites for score display
- Sound effects for all game events

Enjoy the game! 🐦
