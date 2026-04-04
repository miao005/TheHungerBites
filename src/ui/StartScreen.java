package ui;

import main.GamePanel;
import main.GameState;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class StartScreen {
    private BufferedImage background;
    private GamePanel gamePanel;

    public StartScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        try {
            background = ImageIO.read(getClass().getResourceAsStream("/resources/start.png"));
        } catch (IOException e) {
            // Background is optional
        }
    }

    public void draw(Graphics g, int width, int height) {
        // Draw background
        if (background != null) {
            g.drawImage(background, 0, 0, width, height, null);
        } else {
            // Fallback
            g.setColor(new Color(30, 20, 50));
            g.fillRect(0, 0, width, height);
        }

    }

    public void mouseClicked() {
        System.out.println("StartScreen clicked - Moving to Menu");
        gamePanel.setGameState(GameState.MENU);
    }
}