package ui;

import main.GamePanel;
import main.GameState;
import main.Leaderboard;
import java.awt.*;
import java.util.List;

public class LeaderboardScreen {
    private GamePanel gamePanel;
    private Leaderboard leaderboard;
    private Rectangle backBtn;

    public LeaderboardScreen(GamePanel gamePanel, Leaderboard leaderboard) {
        this.gamePanel   = gamePanel;
        this.leaderboard = leaderboard;
    }

    public void draw(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(20, 10, 40));
        g2d.fillRect(0, 0, width, height);

        int cx = width / 2;

        // Title
        g2d.setFont(new Font("Monospaced", Font.BOLD, sf(width, 32)));
        g2d.setColor(new Color(255, 215, 0));
        String title = "LEADERBOARD";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, cx - fm.stringWidth(title) / 2, (int)(height * 0.13));

        // Column headers
        g2d.setFont(new Font("Monospaced", Font.BOLD, sf(width, 13)));
        g2d.setColor(new Color(180, 160, 255));
        g2d.drawString("RANK", (int)(width * 0.12), (int)(height * 0.24));
        g2d.drawString("NAME", (int)(width * 0.30), (int)(height * 0.24));
        g2d.drawString("WINS", (int)(width * 0.72), (int)(height * 0.24));

        // Entries
        List<String[]> entries = leaderboard.getTopEntries();
        g2d.setFont(new Font("Monospaced", Font.PLAIN, sf(width, 13)));
        for (int i = 0; i < entries.size(); i++) {
            int rowY = (int)(height * 0.24) + (i + 1) * sf(width, 22);
            Color rowColor = i == 0 ? new Color(255, 215, 0)
                    : i == 1 ? new Color(192, 192, 192)
                    : i == 2 ? new Color(205, 127, 50)
                    : Color.WHITE;
            g2d.setColor(rowColor);
            g2d.drawString("#" + (i + 1),        (int)(width * 0.12), rowY);
            g2d.drawString(entries.get(i)[0],     (int)(width * 0.30), rowY);
            g2d.drawString(entries.get(i)[1],     (int)(width * 0.72), rowY);
        }

        if (entries.isEmpty()) {
            g2d.setColor(new Color(150, 150, 150));
            g2d.setFont(new Font("Monospaced", Font.PLAIN, sf(width, 14)));
            String empty = "No records yet. Play PVP to get on the board!";
            fm = g2d.getFontMetrics();
            g2d.drawString(empty, cx - fm.stringWidth(empty) / 2, height / 2);
        }

        // Back button
        int btnW = (int)(width * 0.28), btnH = (int)(height * 0.10);
        backBtn = new Rectangle(cx - btnW / 2, (int)(height * 0.84), btnW, btnH);
        g2d.setColor(new Color(80, 60, 140));
        g2d.fillRoundRect(backBtn.x, backBtn.y, btnW, btnH, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(backBtn.x, backBtn.y, btnW, btnH, 10, 10);
        g2d.setStroke(new BasicStroke(1));
        g2d.setFont(new Font("Monospaced", Font.BOLD, sf(width, 14)));
        fm = g2d.getFontMetrics();
        String back = "BACK";
        g2d.drawString(back, backBtn.x + (btnW - fm.stringWidth(back)) / 2,
                backBtn.y + (btnH + fm.getAscent() - fm.getDescent()) / 2);
    }

    public void mouseClicked(int mx, int my) {
        if (backBtn != null && backBtn.contains(mx, my))
            gamePanel.setGameState(gamePanel.getPreviousState());  // ← CHANGED
    }

    private int sf(int w, int base) {
        return Math.max(8, (int)(base * w / 640.0));
    }
}