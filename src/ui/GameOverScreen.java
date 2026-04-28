package ui;

import main.Character;
import main.GamePanel;
import main.GameState;

import java.awt.*;

public class GameOverScreen {
    private GamePanel gamePanel;
    private Character winner, loser;
    private boolean playerWon;
    private boolean arcadeMode;
    private int arcadeRoundsWon;

    private Rectangle btnPlayAgain, btnMenu;

    public GameOverScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void setup(Character winner, Character loser, boolean arcadeMode, int roundsWon) {
        this.winner = winner;
        this.loser = loser;
        this.arcadeMode = arcadeMode;
        this.arcadeRoundsWon = roundsWon;
        this.playerWon = winner != null && winner.isPlayer();
    }

    public void draw(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        Color bgColor = playerWon ? new Color(10, 30, 10) : new Color(30, 10, 10);
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, width, height);

        // Glow border
        g2d.setColor(playerWon ? new Color(60, 200, 80, 80) : new Color(200, 60, 60, 80));
        g2d.setStroke(new BasicStroke(8));
        g2d.drawRect(4, 4, width - 8, height - 8);
        g2d.setStroke(new BasicStroke(1));

        // Game Over / Victory title
        String headline = playerWon ? "VICTORY!" : "GAME OVER";
        g2d.setFont(new Font("Monospaced", Font.BOLD, sf(width, 42)));
        FontMetrics fm = g2d.getFontMetrics();
        Color headColor = playerWon ? new Color(255, 215, 0) : new Color(255, 60, 60);
        g2d.setColor(headColor);
        g2d.drawString(headline, (width - fm.stringWidth(headline)) / 2, (int)(height * 0.22));

        // Winner name
        if (winner != null) {
            g2d.setFont(new Font("Monospaced", Font.BOLD, sf(width, 18)));
            g2d.setColor(Color.WHITE);
            String winStr = "👑 " + winner.getName() + " wins!";
            fm = g2d.getFontMetrics();
            g2d.drawString(winStr, (width - fm.stringWidth(winStr)) / 2, (int)(height * 0.35));
        }

        // Loser
        if (loser != null) {
            g2d.setFont(new Font("Monospaced", Font.PLAIN, sf(width, 13)));
            g2d.setColor(new Color(180, 180, 180));
            String loseStr = loser.getName() + " has fallen.";
            fm = g2d.getFontMetrics();
            g2d.drawString(loseStr, (width - fm.stringWidth(loseStr)) / 2, (int)(height * 0.43));
        }

        // Arcade specific
        if (arcadeMode) {
            g2d.setFont(new Font("Monospaced", Font.PLAIN, sf(width, 12)));
            g2d.setColor(new Color(255, 215, 0));
            String arcStr = playerWon ? "ARCADE COMPLETE! All rounds defeated!" :
                    "You reached Round " + arcadeRoundsWon + ". Better luck next time!";
            fm = g2d.getFontMetrics();
            g2d.drawString(arcStr, (width - fm.stringWidth(arcStr)) / 2, (int)(height * 0.52));
        }

        // Stats
        if (winner != null) {
            g2d.setFont(new Font("Monospaced", Font.PLAIN, sf(width, 11)));
            g2d.setColor(new Color(140, 220, 140));
            String stats = "Winner HP: " + winner.getHealth() + "/" + winner.getMaxHealth() +
                    "   Mana: " + winner.getCurrentMana() + "/" + winner.getMaxMana();
            fm = g2d.getFontMetrics();
            g2d.drawString(stats, (width - fm.stringWidth(stats)) / 2, (int)(height * 0.60));
        }

        // Buttons
        int btnW = (int)(width * 0.28);
        int btnH = (int)(height * 0.10);
        int btnY = (int)(height * 0.72);
        int gap = (int)(width * 0.06);
        int totalBtns = (btnW * 2 + gap);
        int bx = (width - totalBtns) / 2;

        btnPlayAgain = new Rectangle(bx, btnY, btnW, btnH);
        btnMenu = new Rectangle(bx + btnW + gap, btnY, btnW, btnH);

        drawBtn(g2d, btnPlayAgain, "PLAY AGAIN", new Color(60, 100, 200), width);
        drawBtn(g2d, btnMenu, "MAIN MENU", new Color(80, 60, 100), width);
    }

    private void drawBtn(Graphics2D g2d, Rectangle r, String text, Color bg, int screenW) {
        g2d.setColor(bg);
        g2d.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        g2d.setStroke(new BasicStroke(1));
        g2d.setFont(new Font("Monospaced", Font.BOLD, sf(screenW, 13)));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, r.x + (r.width - fm.stringWidth(text)) / 2,
                r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2);
    }

    public void mouseClicked(int mx, int my) {
        if (btnPlayAgain != null && btnPlayAgain.contains(mx, my)) {
            gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
            gamePanel.setGameState(GameState.CHARACTER_SELECT);
            gamePanel.getCharacterSelectScreen().reset();
        } else if (btnMenu != null && btnMenu.contains(mx, my)) {
            gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
            gamePanel.setGameState(GameState.MENU);
        }
    }

    private int sf(int w, int base) {
        return Math.max(8, (int)(base * w / 640.0));
    }
}