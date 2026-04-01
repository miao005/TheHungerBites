package ui;

import main.GamePanel;
import main.GameState;
import java.awt.*;

public class CharacterSelectScreen {
    private GamePanel gamePanel;
    private Rectangle backButton;

    public CharacterSelectScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        // Simple back button
        backButton = new Rectangle(20, GamePanel.HEIGHT - 50, 80, 30);
    }

    public void draw(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;

        // Background
        g2d.setColor(new Color(20, 20, 40));
        g2d.fillRect(0, 0, width, height);

        // Pixel grid
        g2d.setColor(new Color(40, 40, 60));
        for (int i = 0; i < width; i += 32) {
            g2d.drawLine(i, 0, i, height);
        }
        for (int i = 0; i < height; i += 32) {
            g2d.drawLine(0, i, width, i);
        }

        // Title
        g2d.setFont(new Font("Monospaced", Font.BOLD, 24));
        g2d.setColor(new Color(255, 215, 0));
        String title = "CHARACTER SELECTION";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(title)) / 2;
        g2d.drawString(title, x, 100);

        // Info text
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 14));
        g2d.setColor(Color.white);
        String info = "Mode: " + gamePanel.getGameMode();
        x = (width - g2d.getFontMetrics().stringWidth(info)) / 2;
        g2d.drawString(info, x, 150);

        // Placeholder character grid
        g2d.setColor(new Color(60, 50, 180, 150));
        g2d.fillRect(120, 180, 100, 80);
        g2d.fillRect(240, 180, 100, 80);
        g2d.fillRect(360, 180, 100, 80);
        g2d.fillRect(120, 270, 100, 80);
        g2d.fillRect(240, 270, 100, 80);
        g2d.fillRect(360, 270, 100, 80);

        g2d.setColor(Color.white);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2d.drawString("Characters Coming Soon", 230, 380);

        // Back button
        drawBackButton(g2d);

        // Decorative border
        g2d.setColor(new Color(100, 80, 150));
        g2d.drawRect(20, 20, width - 40, height - 40);
    }

    private void drawBackButton(Graphics2D g2d) {
        g2d.setColor(new Color(80, 60, 60));
        g2d.fillRect(backButton.x, backButton.y, backButton.width, backButton.height);
        g2d.setColor(new Color(200, 100, 100));
        g2d.drawRect(backButton.x, backButton.y, backButton.width, backButton.height);

        g2d.setColor(Color.white);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
        String text = "BACK";
        FontMetrics fm = g2d.getFontMetrics();
        int textX = backButton.x + (backButton.width - fm.stringWidth(text)) / 2;
        int textY = backButton.y + (backButton.height + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(text, textX, textY);
    }

    public void mouseClicked(int mouseX, int mouseY) {
        // Back button
        if (backButton.contains(mouseX, mouseY)) {
            System.out.println("Returning to Menu");
            gamePanel.setGameState(GameState.MENU);
        }
    }

    public void mouseMoved(int mouseX, int mouseY) {
        gamePanel.repaint();
    }
}