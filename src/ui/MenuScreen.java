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
    private int hoveredIndex = -1;

    // ----- END OF CUSTOMIZATION SECTION -----

    public MenuScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        try {
            // Load background image
            background = ImageIO.read(getClass().getResourceAsStream("/resources/menu(1).png"));

            // Load button images
            btnPVP = ImageIO.read(getClass().getResourceAsStream("/resources/buttons/btn_pvp.png"));
            btnPVAI = ImageIO.read(getClass().getResourceAsStream("/resources/buttons/btn_pvai.png"));
            btnArcade = ImageIO.read(getClass().getResourceAsStream("/resources/buttons/btn_arcade.png"));

            System.out.println("✓ MenuScreen images loaded!");

        } catch (IOException e) {
            System.out.println("⚠ Error loading images: " + e.getMessage());
        }
    }

    public void draw(Graphics g, int width, int height) {
        // Draw background
        if (background != null) {
            g.drawImage(background, 0, 0, width, height, null);
        } else {
            g.setColor(new Color(20, 20, 40));
            g.fillRect(0, 0, width, height);
        }

        // Calculate button size and position based on current screen size
        int buttonWidth  = (int)(width * 0.19);   // ~19% of screen width
        int buttonHeight = (int)(height * 0.083); // ~8% of screen height
        int buttonX      = (width - buttonWidth) / 2; // always centered

        int pvpY    = (int)(height * 0.47);
        int pvaiY   = (int)(height * 0.58);
        int arcadeY = (int)(height * 0.69);

        // Update bounds so click detection stays accurate
        pvpBounds    = new Rectangle(buttonX, pvpY,    buttonWidth, buttonHeight);
        pvAiBounds   = new Rectangle(buttonX, pvaiY,   buttonWidth, buttonHeight);
        arcadeBounds = new Rectangle(buttonX, arcadeY, buttonWidth, buttonHeight);

        // Draw buttons
        drawButton(g, pvpBounds,    btnPVP,    "PVP",    hoveredIndex == 0);
        drawButton(g, pvAiBounds,   btnPVAI,   "P V AI", hoveredIndex == 1);
        drawButton(g, arcadeBounds, btnArcade, "ARCADE", hoveredIndex == 2);
    }

    private void drawButton(Graphics g, Rectangle bounds, BufferedImage buttonImage, String text, boolean isHovered) {
        if (bounds == null) return;

        Graphics2D g2d = (Graphics2D) g;

        if (buttonImage != null) {
            // Draw button image (scaled to your custom size)
            g.drawImage(buttonImage, bounds.x, bounds.y, bounds.width, bounds.height, null);

            // Hover effect
            if (isHovered) {
                g2d.setColor(new Color(255, 215, 0, 100));
                g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

                // Glow effect
                g2d.setColor(new Color(255, 215, 0, 180));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6);
            }
        } else {
            // Fallback rectangle button
            if (isHovered) {
                g2d.setColor(new Color(100, 85, 255));
            } else {
                g2d.setColor(new Color(60, 50, 180));
            }
            g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            g2d.setColor(new Color(180, 160, 255));
            g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

            g2d.setColor(Color.white);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 20));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
            int textY = bounds.y + (bounds.height + fm.getAscent() - fm.getDescent()) / 2;
            g2d.drawString(text, textX, textY);
        }
    }

    public void mouseClicked(int mouseX, int mouseY) {
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
        }
    }

    public void mouseMoved(int mouseX, int mouseY) {
        int newHovered = -1;

        if (pvpBounds != null && pvpBounds.contains(mouseX, mouseY)) {
            newHovered = 0;
        } else if (pvAiBounds != null && pvAiBounds.contains(mouseX, mouseY)) {
            newHovered = 1;
        } else if (arcadeBounds != null && arcadeBounds.contains(mouseX, mouseY)) {
            newHovered = 2;
        }

        if (newHovered != hoveredIndex) {
            hoveredIndex = newHovered;
            gamePanel.repaint();
        }
    }
}