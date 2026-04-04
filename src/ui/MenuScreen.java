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

    // ----- ADJUST THESE VALUES TO CUSTOMIZE YOUR BUTTONS -----

    // Button dimensions (width and height in pixels)
    private int buttonWidth = 120;      // Change this to adjust button width
    private int buttonHeight = 30;      // Change this to adjust button height

    // Button positions (X and Y coordinates)
    // You can set each button individually if they're not aligned
    private int pvpX = 260;              // PVP button X position
    private int pvpY = 170;              // PVP button Y position

    private int pvaiX = 260;             // P V AI button X position
    private int pvaiY = 210;             // P V AI button Y position

    private int arcadeX = 260;           // Arcade button X position
    private int arcadeY = 250;           // Arcade button Y position

    // OR use this centered calculation (uncomment to use)
    // private int centerX = (GamePanel.WIDTH - buttonWidth) / 2;

    // Gap between buttons (if using automatic positioning)
    private int buttonGap = 10;          // Space between buttons

    // ----- END OF CUSTOMIZATION SECTION -----

    public MenuScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        try {
            // Load background image
            background = ImageIO.read(getClass().getResourceAsStream("/resources/menu(1).png"));
            if (background == null) {
                background = ImageIO.read(getClass().getResourceAsStream("/resources/menu.png"));
            }

            // Load button images
            btnPVP = ImageIO.read(getClass().getResourceAsStream("/resources/btn_pvp.png"));
            btnPVAI = ImageIO.read(getClass().getResourceAsStream("/resources/btn_pvai.png"));
            btnArcade = ImageIO.read(getClass().getResourceAsStream("/resources/btn_arcade.png"));

            System.out.println("✓ MenuScreen images loaded!");

        } catch (IOException e) {
            System.out.println("⚠ Error loading images: " + e.getMessage());
        }

        // Initialize button bounds with your custom values
        pvpBounds = new Rectangle(pvpX, pvpY, buttonWidth, buttonHeight);
        pvAiBounds = new Rectangle(pvaiX, pvaiY, buttonWidth, buttonHeight);
        arcadeBounds = new Rectangle(arcadeX, arcadeY, buttonWidth, buttonHeight);

        // Optional: Print button positions for debugging
        System.out.println("Button positions:");
        System.out.println("  PVP: (" + pvpX + ", " + pvpY + ") size: " + buttonWidth + "x" + buttonHeight);
        System.out.println("  P V AI: (" + pvaiX + ", " + pvaiY + ") size: " + buttonWidth + "x" + buttonHeight);
        System.out.println("  Arcade: (" + arcadeX + ", " + arcadeY + ") size: " + buttonWidth + "x" + buttonHeight);
    }

    public void draw(Graphics g, int width, int height) {
        // Draw background
        if (background != null) {
            g.drawImage(background, 0, 0, width, height, null);
        } else {
            g.setColor(new Color(20, 20, 40));
            g.fillRect(0, 0, width, height);
        }

        // Draw buttons
        drawButton(g, pvpBounds, btnPVP, "PVP", hoveredIndex == 0);
        drawButton(g, pvAiBounds, btnPVAI, "P V AI", hoveredIndex == 1);
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
            gamePanel.setGameMode("PVP");
            gamePanel.setGameState(GameState.CHARACTER_SELECT);
            System.out.println("Selected: PVP Mode");
        } else if (pvAiBounds.contains(mouseX, mouseY)) {
            gamePanel.setGameMode("PVAI");
            gamePanel.setGameState(GameState.CHARACTER_SELECT);
            System.out.println("Selected: P V AI Mode");
        } else if (arcadeBounds.contains(mouseX, mouseY)) {
            gamePanel.setGameMode("ARCADE");
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