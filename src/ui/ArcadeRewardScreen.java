package ui;

import main.Character;
import main.GamePanel;
import java.awt.*;

public class ArcadeRewardScreen {
    private GamePanel gamePanel;
    private Character player;
    private int roundJustWon;
    private int totalRounds;

    private Rectangle btn1, btn2, btn3, btn4;

    public ArcadeRewardScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void setup(Character player, int roundJustWon, int totalRounds) {
        this.player       = player;
        this.roundJustWon = roundJustWon;
        this.totalRounds  = totalRounds;
    }

    public void draw(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2d.setColor(new Color(10, 10, 60));
        g2d.fillRect(0, 0, width, height);

        // Title
        g2d.setFont(new Font("Monospaced", Font.BOLD, sf(width, 24)));
        g2d.setColor(new Color(255, 215, 0));
        String title = "⭐ ROUND " + roundJustWon + " COMPLETE! ⭐";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, (width - fm.stringWidth(title)) / 2, (int)(height * 0.12));

        g2d.setFont(new Font("Monospaced", Font.PLAIN, sf(width, 13)));
        g2d.setColor(Color.WHITE);
        String sub = "Choose your reward before Round " + (roundJustWon + 1) + " / " + totalRounds;
        fm = g2d.getFontMetrics();
        g2d.drawString(sub, (width - fm.stringWidth(sub)) / 2, (int)(height * 0.20));

        // Current stats
        g2d.setFont(new Font("Monospaced", Font.PLAIN, sf(width, 11)));
        g2d.setColor(new Color(140, 220, 140));
        String stats = "Current HP: " + player.getHealth() + "/" + player.getMaxHealth() +
                "   Mana: " + player.getCurrentMana() + "/" + player.getMaxMana();
        fm = g2d.getFontMetrics();
        g2d.drawString(stats, (width - fm.stringWidth(stats)) / 2, (int)(height * 0.27));

        // Reward buttons
        int btnW = (int)(width  * 0.60);
        int btnH = (int)(height * 0.11);
        int btnX = (width - btnW) / 2;
        int gap  = (int)(height * 0.02);

        btn1 = new Rectangle(btnX, (int)(height * 0.33),                   btnW, btnH);
        btn2 = new Rectangle(btnX, (int)(height * 0.33) + btnH + gap,      btnW, btnH);
        btn3 = new Rectangle(btnX, (int)(height * 0.33) + (btnH + gap) * 2, btnW, btnH);
        btn4 = new Rectangle(btnX, (int)(height * 0.33) + (btnH + gap) * 3, btnW, btnH);

        drawRewardBtn(g2d, btn1, "1. Heal 100% HP + Full Mana",  new Color(40,  160, 80),  width);
        drawRewardBtn(g2d, btn2, "2. +100% Damage Boost",       new Color(60,  80,  200), width);
        drawRewardBtn(g2d, btn3, "3. +25 Max Health",           new Color(160, 40,  160), width);
        drawRewardBtn(g2d, btn4, "4. +15 Max Mana",             new Color(180, 140, 20),  width);

        // ── Settings / Leaderboard nav buttons ───────────────────────
        NavButtons.draw(g2d, width, height);
    }

    private void drawRewardBtn(Graphics2D g2d, Rectangle r, String text, Color bg, int screenW) {
        g2d.setColor(bg);
        g2d.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        g2d.setStroke(new BasicStroke(1));
        g2d.setFont(new Font("Monospaced", Font.BOLD, sf(screenW, 14)));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, r.x + (r.width - fm.stringWidth(text)) / 2,
                r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2);
    }

    public void mouseClicked(int mx, int my) {
        // Check nav buttons first
        if (NavButtons.handleClick(mx, my, gamePanel)) return;

        int choice = -1;
        if      (btn1 != null && btn1.contains(mx, my)) choice = 1;
        else if (btn2 != null && btn2.contains(mx, my)) choice = 2;
        else if (btn3 != null && btn3.contains(mx, my)) choice = 3;
        else if (btn4 != null && btn4.contains(mx, my)) choice = 4;

        if (choice != -1) {
            gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
            applyReward(choice);
            gamePanel.continueArcade();
        }
    }

    private void applyReward(int choice) {
        switch (choice) {
            case 1:
                player.setHealth(player.getMaxHealth());
                player.setCurrentMana(player.getMaxMana());
                break;
            case 2:
                player.increaseDamage(1.0);
                break;
            case 3:
                player.setMaxHealth(player.getMaxHealth() + 25);
                player.setHealth(player.getHealth() + 25);
                break;
            case 4:
                player.setMaxMana(player.getMaxMana() + 15);
                player.setCurrentMana(player.getCurrentMana() + 15);
                break;
        }
    }

    private int sf(int w, int base) {
        return Math.max(8, (int)(base * w / 640.0));
    }
}