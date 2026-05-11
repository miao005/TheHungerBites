package ui;

import main.Character;
import main.GamePanel;
import main.GameState;
import main.Leaderboard;

import javax.swing.ImageIcon;
import java.awt.*;
import java.io.InputStream;
import java.net.URL;

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

    private ImageIcon victoryGif;
    private ImageIcon gameoverGif;
    private Font minecraftFont;

    public GameOverScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        loadFont();
        loadGifs();
    }

    private void loadFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf");
            if (is != null) {
                minecraftFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 14f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(minecraftFont);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (minecraftFont == null) minecraftFont = new Font("Monospaced", Font.BOLD, 14);
    }

    private Font mc(int size) {
        return minecraftFont.deriveFont(Font.PLAIN, (float) size);
    }

    private void loadGifs() {
        URL victoryUrl = getClass().getResource("/resources/victory.gif");
        if (victoryUrl != null) {
            victoryGif = new ImageIcon(victoryUrl);
            victoryGif.setImageObserver(gamePanel);
        }
        URL gameoverUrl = getClass().getResource("/resources/gameover.gif");
        if (gameoverUrl != null) {
            gameoverGif = new ImageIcon(gameoverUrl);
            gameoverGif.setImageObserver(gamePanel);
        }
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

        if (leaderboard == null) leaderboard = gamePanel.getLeaderboard();
    }

    public void setLeaderboard(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    public void draw(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background GIF
        ImageIcon bgGif = playerWon ? victoryGif : gameoverGif;
        if (bgGif != null) {
            g2d.drawImage(bgGif.getImage(), 0, 0, width, height, gamePanel);
        } else {
            g2d.setColor(playerWon ? new Color(10,30,10) : new Color(30,10,10));
            g2d.fillRect(0, 0, width, height);
        }

        FontMetrics fm;

        if (winner != null) {
            g2d.setFont(mc(sf(width,18)));
            g2d.setColor(Color.WHITE);
            String winStr = " " + winner.getName() + " wins!";
            fm = g2d.getFontMetrics();
            g2d.drawString(winStr, (width-fm.stringWidth(winStr))/2, (int)(height*0.35));
        }
        if (loser != null) {
            g2d.setFont(mc(sf(width,13)));
            g2d.setColor(new Color(180,180,180));
            String loseStr = loser.getName() + " has fallen.";
            fm = g2d.getFontMetrics();
            g2d.drawString(loseStr, (width-fm.stringWidth(loseStr))/2, (int)(height*0.43));
        }
        if (arcadeMode) {
            g2d.setFont(mc(sf(width,12)));
            g2d.setColor(new Color(255,215,0));
            String arcStr = playerWon
                    ? "ARCADE COMPLETE! All rounds defeated!"
                    : "You reached Round " + arcadeRoundsWon + ". Better luck next time!";
            fm = g2d.getFontMetrics();
            g2d.drawString(arcStr, (width-fm.stringWidth(arcStr))/2, (int)(height*0.52));
        }
        if (winner != null) {
            g2d.setFont(mc(sf(width,11)));
            g2d.setColor(new Color(140,220,140));
            String stats = "Winner HP: "+winner.getHealth()+"/"+winner.getMaxHealth()
                    +"   Mana: "+winner.getCurrentMana()+"/"+winner.getMaxMana();
            fm = g2d.getFontMetrics();
            g2d.drawString(stats, (width-fm.stringWidth(stats))/2, (int)(height*0.60));
        }

        // Name input form — shown for PVP wins AND completed Arcade runs
        boolean showNameEntry = (pvpMode || (arcadeMode && playerWon)) && winner != null && !nameSubmitted;
        if (showNameEntry) {
            int cx = width/2;
            int boxY = (int)(height*0.63);
            g2d.setFont(mc(sf(width,12)));
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
            g2d.setFont(mc(sf(width,12)));
            g2d.drawString(nameInput+"|", inputX+8, boxY+8+inputH-10);

            int sbW=(int)(width*0.18), sbH=(int)(height*0.07);
            nameSubmitBtn = new Rectangle(cx-sbW/2, boxY+inputH+14, sbW, sbH);

            boolean canSubmit = !nameInput.trim().isEmpty();
            g2d.setColor(canSubmit ? new Color(60,140,60) : new Color(60,80,60));
            g2d.fillRoundRect(nameSubmitBtn.x, nameSubmitBtn.y, sbW, sbH, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(nameSubmitBtn.x, nameSubmitBtn.y, sbW, sbH, 8, 8);
            g2d.setStroke(new BasicStroke(1));
            g2d.setFont(mc(sf(width,12)));
            fm2 = g2d.getFontMetrics();
            g2d.drawString("SUBMIT",
                    nameSubmitBtn.x+(sbW-fm2.stringWidth("SUBMIT"))/2,
                    nameSubmitBtn.y+(sbH+fm2.getAscent()-fm2.getDescent())/2);
        }

        int btnW=(int)(width*0.28), btnH=(int)(height*0.10);
        boolean nameEntryActive = (pvpMode || (arcadeMode && playerWon)) && winner != null && !nameSubmitted;
        int btnY = nameEntryActive ? (int)(height*0.87) : (int)(height*0.72);
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
        g2d.setFont(mc(sf(screenW,13)));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, r.x+(r.width-fm.stringWidth(text))/2,
                r.y+(r.height+fm.getAscent()-fm.getDescent())/2);
        g2d.setTransform(old);
    }

    public void mouseClicked(int mx, int my) {
        if (NavButtons.handleClick(mx, my, gamePanel)) return;

        boolean canSubmitName = (pvpMode || (arcadeMode && playerWon)) && !nameSubmitted;
        if (canSubmitName && nameSubmitBtn != null && nameSubmitBtn.contains(mx, my)) {
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
        if ((!pvpMode && !(arcadeMode && playerWon)) || nameSubmitted) return;
        if (c == '\b' && nameInput.length() > 0)
            nameInput = nameInput.substring(0, nameInput.length()-1);
        else if (c != '\b' && nameInput.length() < 16 && c >= 32)
            nameInput += c;
        gamePanel.repaint();
    }

    private int sf(int w, int base) { return Math.max(8,(int)(base*w/640.0)); }
}