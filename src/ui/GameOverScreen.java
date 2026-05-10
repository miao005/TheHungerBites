package ui;

import main.Character;
import main.GamePanel;
import main.GameState;
import main.Leaderboard;

import java.awt.*;

public class GameOverScreen {
    private GamePanel gamePanel;
    private Character winner, loser;
    private boolean playerWon;
    private boolean arcadeMode;
    private int arcadeRoundsWon;
    private Leaderboard leaderboard;
    private boolean pvpMode;
    private String nameInput = "";
    private boolean nameSubmitted = false;
    private Rectangle nameSubmitBtn;

    private Rectangle btnPlayAgain, btnMenu;
    private int hoveredBtn = -1;

    private static final int   LIFT  = 3;
    private static final float SCALE = 1.05f;

    public GameOverScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void setup(Character winner, Character loser, boolean arcadeMode, boolean aiMode, int roundsWon) {
        this.winner          = winner;
        this.loser           = loser;
        this.arcadeMode      = arcadeMode;
        this.arcadeRoundsWon = roundsWon;
        this.playerWon       = winner != null && winner.isPlayer();
        this.pvpMode         = !arcadeMode && !aiMode;
        this.nameInput       = "";
        this.nameSubmitted   = false;
        this.hoveredBtn      = -1;
        this.nameSubmitBtn   = null;

        // Try to grab leaderboard from GamePanel if not already set
        if (leaderboard == null) leaderboard = gamePanel.getLeaderboard();
    }

    public void setLeaderboard(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    public void draw(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(playerWon ? new Color(10,30,10) : new Color(30,10,10));
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(playerWon ? new Color(60,200,80,80) : new Color(200,60,60,80));
        g2d.setStroke(new BasicStroke(8));
        g2d.drawRect(4, 4, width-8, height-8);
        g2d.setStroke(new BasicStroke(1));

        String headline = playerWon ? "VICTORY!" : "GAME OVER";
        g2d.setFont(new Font("Monospaced", Font.BOLD, sf(width,42)));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setColor(playerWon ? new Color(255,215,0) : new Color(255,60,60));
        g2d.drawString(headline, (width-fm.stringWidth(headline))/2, (int)(height*0.22));

        if (winner != null) {
            g2d.setFont(new Font("Monospaced", Font.BOLD, sf(width,18)));
            g2d.setColor(Color.WHITE);
            String winStr = "👑 " + winner.getName() + " wins!";
            fm = g2d.getFontMetrics();
            g2d.drawString(winStr, (width-fm.stringWidth(winStr))/2, (int)(height*0.35));
        }
        if (loser != null) {
            g2d.setFont(new Font("Monospaced", Font.PLAIN, sf(width,13)));
            g2d.setColor(new Color(180,180,180));
            String loseStr = loser.getName() + " has fallen.";
            fm = g2d.getFontMetrics();
            g2d.drawString(loseStr, (width-fm.stringWidth(loseStr))/2, (int)(height*0.43));
        }
        if (arcadeMode) {
            g2d.setFont(new Font("Monospaced", Font.PLAIN, sf(width,12)));
            g2d.setColor(new Color(255,215,0));
            String arcStr = playerWon ? "ARCADE COMPLETE! All rounds defeated!" :
                    "You reached Round " + arcadeRoundsWon + ". Better luck next time!";
            fm = g2d.getFontMetrics();
            g2d.drawString(arcStr, (width-fm.stringWidth(arcStr))/2, (int)(height*0.52));
        }
        if (winner != null) {
            g2d.setFont(new Font("Monospaced", Font.PLAIN, sf(width,11)));
            g2d.setColor(new Color(140,220,140));
            String stats = "Winner HP: "+winner.getHealth()+"/"+winner.getMaxHealth()+
                    "   Mana: "+winner.getCurrentMana()+"/"+winner.getMaxMana();
            fm = g2d.getFontMetrics();
            g2d.drawString(stats, (width-fm.stringWidth(stats))/2, (int)(height*0.60));
        }

        // Name input form — only in PVP and winner exists and not yet submitted
        if (pvpMode && winner != null && !nameSubmitted) {
            int cx = width/2;
            int boxY = (int)(height*0.63);
            g2d.setFont(new Font("Monospaced", Font.PLAIN, sf(width,12)));
            g2d.setColor(Color.WHITE);
            String prompt = "Enter winner's name for leaderboard:";
            FontMetrics fm2 = g2d.getFontMetrics();
            g2d.drawString(prompt, cx-fm2.stringWidth(prompt)/2, boxY);

            int inputW=(int)(width*0.40), inputH=(int)(height*0.08), inputX=cx-inputW/2;
            g2d.setColor(new Color(40,30,80));
            g2d.fillRoundRect(inputX, boxY+8, inputW, inputH, 6, 6);
            g2d.setColor(new Color(180,160,255));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(inputX, boxY+8, inputW, inputH, 6, 6);
            g2d.setStroke(new BasicStroke(1));
            g2d.setColor(Color.WHITE);
            g2d.drawString(nameInput+"|", inputX+8, boxY+8+inputH-10);

            int sbW=(int)(width*0.18), sbH=(int)(height*0.07);
            nameSubmitBtn = new Rectangle(cx-sbW/2, boxY+inputH+14, sbW, sbH);

            // Submit button — greyed out if empty
            boolean canSubmit = !nameInput.trim().isEmpty();
            g2d.setColor(canSubmit ? new Color(60,140,60) : new Color(60,80,60));
            g2d.fillRoundRect(nameSubmitBtn.x, nameSubmitBtn.y, sbW, sbH, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(nameSubmitBtn.x, nameSubmitBtn.y, sbW, sbH, 8, 8);
            g2d.setStroke(new BasicStroke(1));
            g2d.setFont(new Font("Monospaced", Font.BOLD, sf(width,12)));
            fm2 = g2d.getFontMetrics();
            g2d.drawString("SUBMIT",
                    nameSubmitBtn.x+(sbW-fm2.stringWidth("SUBMIT"))/2,
                    nameSubmitBtn.y+(sbH+fm2.getAscent()-fm2.getDescent())/2);
        }

        // Play Again / Main Menu buttons
        int btnW=(int)(width*0.28), btnH=(int)(height*0.10);
        int btnY = (pvpMode && winner!=null && !nameSubmitted) ? (int)(height*0.87) : (int)(height*0.72);
        int gap=(int)(width*0.06), bx=(width-(btnW*2+gap))/2;

        btnPlayAgain = new Rectangle(bx,          btnY, btnW, btnH);
        btnMenu      = new Rectangle(bx+btnW+gap, btnY, btnW, btnH);

        drawBtn(g2d, btnPlayAgain, "PLAY AGAIN", new Color(60,100,200), hoveredBtn==0, width);
        drawBtn(g2d, btnMenu,      "MAIN MENU",  new Color(80,60,100),  hoveredBtn==1, width);

        NavButtons.draw(g2d, width, height);
    }

    private void drawBtn(Graphics2D g2d, Rectangle r, String text, Color bg, boolean hovered, int screenW) {
        java.awt.geom.AffineTransform old = g2d.getTransform();
        if (hovered) {
            int cx = r.x + r.width  / 2;
            int cy = r.y + r.height / 2;
            g2d.translate(cx, cy - LIFT);
            g2d.scale(SCALE, SCALE);
            g2d.translate(-cx, -cy);
        }
        g2d.setColor(bg);
        g2d.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        g2d.setStroke(new BasicStroke(1));
        g2d.setFont(new Font("Monospaced", Font.BOLD, sf(screenW,13)));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, r.x+(r.width-fm.stringWidth(text))/2,
                r.y+(r.height+fm.getAscent()-fm.getDescent())/2);
        g2d.setTransform(old);
    }

    public void mouseClicked(int mx, int my) {
        if (NavButtons.handleClick(mx, my, gamePanel)) return;

        // Submit button — check first before other buttons
        if (pvpMode && !nameSubmitted && nameSubmitBtn != null && nameSubmitBtn.contains(mx, my)) {
            String trimmed = nameInput.trim();
            if (!trimmed.isEmpty() && leaderboard != null) {
                leaderboard.recordWin(trimmed);
                nameSubmitted = true;
                gamePanel.repaint();
            }
            return;
        }

        if (btnPlayAgain != null && btnPlayAgain.contains(mx, my)) {
            gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
            gamePanel.setGameState(GameState.CHARACTER_SELECT);
            gamePanel.getCharacterSelectScreen().reset();
        } else if (btnMenu != null && btnMenu.contains(mx, my)) {
            gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
            gamePanel.setGameState(GameState.MENU);
        }
    }

    public void mouseMoved(int mx, int my) {
        int prev = hoveredBtn;
        hoveredBtn = -1;
        if      (btnPlayAgain != null && btnPlayAgain.contains(mx, my)) hoveredBtn = 0;
        else if (btnMenu      != null && btnMenu.contains(mx, my))      hoveredBtn = 1;
        if (hoveredBtn != prev) gamePanel.repaint();
    }

    public void keyTyped(char c) {
        if (!pvpMode || nameSubmitted) return;
        if (c == '\b' && nameInput.length() > 0)
            nameInput = nameInput.substring(0, nameInput.length()-1);
        else if (c != '\b' && nameInput.length() < 16 && c >= 32)
            nameInput += c;
        gamePanel.repaint();
    }

    private int sf(int w, int base) { return Math.max(8,(int)(base*w/640.0)); }
}