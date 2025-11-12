# Flappy Bird - JavaFX Edition

Classic Flappy Bird game implemented in JavaFX.

<img width="597" height="832" alt="image" src="https://github.com/user-attachments/assets/a7bd2a57-c99b-4286-ada5-edad3c36362d" />

## How to Play

- **SPACE** or **Click Mouse**: Make the bird jump
- **R**: Restart game (when game over)

Navigate through the pipes without hitting them or the ground. Each pipe you pass increases your score!

## Game Features

- Smooth bird animations with different color variations (yellow, red, blue)
- Dynamic pipe generation with random heights
- Day and night background variations
- Sound effects for jumping, scoring, and collisions
- High score tracking
- Animated ground scrolling

## Installation and Setup

### Prerequisites

- Java Development Kit 11 or higher
- Maven 3.6 or higher (Optional but recommended)

**Note:** The project can be run without Maven, but you will need to manually download JavaFX SDK and configure the module path. Using Maven is recommended for easier setup.

### Installing Maven

**Windows:**

1. Download Maven from [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
2. Extract the archive to a directory
3. Add Maven bin directory to PATH environment variable
4. Verify installation: `mvn -version`

**macOS:**

```bash
brew install maven
```

**Linux (Debian/Ubuntu):**

```bash
sudo apt update
sudo apt install maven
```

**Linux (Fedora):**

```bash
sudo dnf install maven
```

For more installation options, visit: [https://maven.apache.org/install.html](https://maven.apache.org/install.html)

### Setup Instructions

1. **Clone the repository:**

   ```bash
   git clone https://github.com/yavuz1205/FlappyFX.git
   cd FlappyFX
   ```

2. **Build the project:**

   ```bash
   mvn clean compile
   ```

3. **Run the game:**
   ```bash
   mvn javafx:run
   ```

## Assets

All sprites and sounds are included in the `assets` folder:

- Bird animations (3 colors × 3 frames each)
- Pipe graphics (green and red)
- Backgrounds (day and night)
- Number sprites for score display
- Sound effects for all game events

## Attributions and Licenses

**Font**

- _Flappy Bird Regular_ by FontofFame (2015)  
  Licensed under FontStruct Non-Commercial License — commercial and non-commercial use allowed.  
  Source: [https://online-fonts.com/fonts/flappy-bird](https://online-fonts.com/fonts/flappy-bird)

**Assets**

- _Flappy Bird Assets_ (sprites and sounds) by Samuel Custodio (2019)  
  Licensed under the MIT License.  
  Source: [https://github.com/samuelcust/flappy-bird-assets](https://github.com/samuelcust/flappy-bird-assets)

Parts of this project (sprites and audio) are licensed under the MIT License © 2019 Samuel Custodio.  
See `/assets/LICENSE` for details.

---

Enjoy the game :)
