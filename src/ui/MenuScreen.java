package ui;

import main.GamePanel;
import main.GameState;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MenuScreen {
    private BufferedImage background;
    private BufferedImage btnPVP, btnPVAI, btnArcade;
    private GamePanel gamePanel;

    private Rectangle pvpBounds;
    private Rectangle pvAiBounds;
    private Rectangle arcadeBounds;
    private Rectangle settingsBounds, leaderboardBounds;
    private int hoveredIndex = -1;

    public MenuScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        try {
            background = ImageIO.read(getClass().getResourceAsStream("/resources/menu(1).png"));
            btnPVP     = ImageIO.read(getClass().getResourceAsStream("/resources/buttons/btn_pvp.png"));
            btnPVAI    = ImageIO.read(getClass().getResourceAsStream("/resources/buttons/btn_pvai.png"));
            btnArcade  = ImageIO.read(getClass().getResourceAsStream("/resources/buttons/btn_arcade.png"));
            System.out.println("✓ MenuScreen images loaded!");
        } catch (IOException e) {
            System.out.println("⚠ Error loading images: " + e.getMessage());
        }
    }

    public void draw(Graphics g, int width, int height) {
        if (background != null) {
            g.drawImage(background, 0, 0, width, height, null);
        } else {
            g.setColor(new Color(20, 20, 40));
            g.fillRect(0, 0, width, height);
        }

        int buttonWidth  = (int)(width  * 0.19);
        int buttonHeight = (int)(height * 0.083);
        int buttonX      = (width - buttonWidth) / 2;

        int pvpY    = (int)(height * 0.47);
        int pvaiY   = (int)(height * 0.58);
        int arcadeY = (int)(height * 0.69);

        pvpBounds    = new Rectangle(buttonX, pvpY,    buttonWidth, buttonHeight);
        pvAiBounds   = new Rectangle(buttonX, pvaiY,   buttonWidth, buttonHeight);
        arcadeBounds = new Rectangle(buttonX, arcadeY, buttonWidth, buttonHeight);

        drawButton(g, pvpBounds,    btnPVP,    "PVP",    hoveredIndex == 0);
        drawButton(g, pvAiBounds,   btnPVAI,   "P V AI", hoveredIndex == 1);
        drawButton(g, arcadeBounds, btnArcade, "ARCADE", hoveredIndex == 2);
        NavButtons.draw((Graphics2D) g, width, height);
    }

    private void drawButton(Graphics g, Rectangle bounds, BufferedImage buttonImage,
                            String text, boolean isHovered) {
        if (bounds == null) return;
        Graphics2D g2d = (Graphics2D) g;
        if (buttonImage != null) {
            g.drawImage(buttonImage, bounds.x, bounds.y, bounds.width, bounds.height, null);
            if (isHovered) {
                g2d.setColor(new Color(255, 215, 0, 100));
                g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                g2d.setColor(new Color(255, 215, 0, 180));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6);
            }
        } else {
            g2d.setColor(isHovered ? new Color(100, 85, 255) : new Color(60, 50, 180));
            g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            g2d.setColor(new Color(180, 160, 255));
            g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            g2d.setColor(Color.white);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 20));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(text,
                    bounds.x + (bounds.width  - fm.stringWidth(text)) / 2,
                    bounds.y + (bounds.height + fm.getAscent() - fm.getDescent()) / 2);
        }
    }

    public void mouseClicked(int mouseX, int mouseY) {
        if (NavButtons.handleClick(mouseX, mouseY, gamePanel)) return;
        if (pvpBounds == null) return;

        System.out.println("Click at: (" + mouseX + ", " + mouseY + ")");

        if (pvpBounds.contains(mouseX, mouseY)) {
            gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
            gamePanel.setGameMode("PVP");
            gamePanel.getCharacterSelectScreen().reset();
            gamePanel.setGameState(GameState.CHARACTER_SELECT);
            System.out.println("Selected: PVP Mode");
        } else if (pvAiBounds.contains(mouseX, mouseY)) {
            gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
            gamePanel.setGameMode("PVAI");
            gamePanel.getCharacterSelectScreen().reset();
            gamePanel.setGameState(GameState.CHARACTER_SELECT);
            System.out.println("Selected: P V AI Mode");
        } else if (arcadeBounds.contains(mouseX, mouseY)) {
            gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
            gamePanel.setGameMode("ARCADE");
            gamePanel.getCharacterSelectScreen().reset();
            gamePanel.setGameState(GameState.CHARACTER_SELECT);
            System.out.println("Selected: ARCADE Mode");
        } else if (settingsBounds != null && settingsBounds.contains(mouseX, mouseY)) {
            gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
            gamePanel.goToOverlay(GameState.SETTINGS);      // ← CHANGED
        } else if (leaderboardBounds != null && leaderboardBounds.contains(mouseX, mouseY)) {
            gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
            gamePanel.goToOverlay(GameState.LEADERBOARD);   // ← CHANGED
        }
    }

    public void mouseMoved(int mouseX, int mouseY) {
        int newHovered = -1;
        if      (pvpBounds   != null && pvpBounds.contains(mouseX, mouseY))   newHovered = 0;
        else if (pvAiBounds  != null && pvAiBounds.contains(mouseX, mouseY))  newHovered = 1;
        else if (arcadeBounds != null && arcadeBounds.contains(mouseX, mouseY)) newHovered = 2;
        if (newHovered != hoveredIndex) {
            hoveredIndex = newHovered;
            gamePanel.repaint();
        }
    }
}