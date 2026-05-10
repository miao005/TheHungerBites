package ui;

import main.GamePanel;
import main.GameState;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class CreditsScreen {
    private GamePanel gamePanel;
    private BufferedImage bg;
    private Rectangle backBtn;

    public CreditsScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        try {
            bg = ImageIO.read(getClass().getResourceAsStream("/resources/credits.png"));
        } catch (IOException e) {
            System.out.println("credits.png not found: " + e.getMessage());
        }
    }

    public void draw(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (bg != null) {
            g2d.drawImage(bg, 0, 0, width, height, null);
        }

        // Back button at bottom centre
        int btnW = (int)(width * 0.18), btnH = (int)(height * 0.08);
        backBtn = new Rectangle((width - btnW) / 2, (int)(height * 0.88), btnW, btnH);
        g2d.setColor(new Color(40, 30, 80, 210));
        g2d.fillRoundRect(backBtn.x, backBtn.y, btnW, btnH, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(backBtn.x, backBtn.y, btnW, btnH, 10, 10);
        g2d.setStroke(new BasicStroke(1));
        g2d.setFont(new Font("Monospaced", Font.BOLD, Math.max(8, (int)(btnH * 0.40))));
        FontMetrics fm = g2d.getFontMetrics();
        String label = "BACK";
        g2d.drawString(label,
                backBtn.x + (btnW - fm.stringWidth(label)) / 2,
                backBtn.y + (btnH + fm.getAscent() - fm.getDescent()) / 2);
    }

    public void mouseClicked(int mx, int my) {
        if (backBtn != null && backBtn.contains(mx, my))
            gamePanel.setGameState(GameState.SETTINGS);
    }
}