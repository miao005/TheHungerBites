package ui;

import main.GamePanel;
import main.GameState;
import java.awt.*;

public class NavButtons {
    private static Rectangle settingsBtn;
    private static Rectangle leaderboardBtn;

    public static void draw(Graphics2D g2d, int width, int height) {
        int size = Math.max(18, (int)(height * 0.07));
        int pad  = (int)(width * 0.01);
        int top  = (int)(height * 0.01);

        settingsBtn    = new Rectangle(width - pad - size,           top, size, size);
        leaderboardBtn = new Rectangle(width - pad - size * 2 - pad, top, size, size);

        drawSquareBtn(g2d, settingsBtn,    "S", new Color(60, 80, 160));
        drawSquareBtn(g2d, leaderboardBtn, "L", new Color(100, 60, 140));
    }

    private static void drawSquareBtn(Graphics2D g2d, Rectangle r, String label, Color bg) {
        g2d.setColor(bg);
        g2d.fillRoundRect(r.x, r.y, r.width, r.height, 6, 6);
        g2d.setColor(new Color(200, 200, 255));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(r.x, r.y, r.width, r.height, 6, 6);
        g2d.setStroke(new BasicStroke(1));
        g2d.setFont(new Font("Monospaced", Font.BOLD, Math.max(8, r.height / 2)));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setColor(Color.WHITE);
        g2d.drawString(label,
                r.x + (r.width  - fm.stringWidth(label)) / 2,
                r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2);
    }

    public static boolean handleClick(int mx, int my, GamePanel gamePanel) {
        if (settingsBtn != null && settingsBtn.contains(mx, my)) {
            gamePanel.setGameState(GameState.SETTINGS);
            return true;
        }
        if (leaderboardBtn != null && leaderboardBtn.contains(mx, my)) {
            gamePanel.setGameState(GameState.LEADERBOARD);
            return true;
        }
        return false;
    }
}