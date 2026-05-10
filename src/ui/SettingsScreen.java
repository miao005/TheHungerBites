package ui;

import main.AudioManager;
import main.GamePanel;
import main.GameState;
import main.Settings;

import java.awt.*;
import java.io.InputStream;

public class SettingsScreen {
    private GamePanel gamePanel;
    private Settings settings;
    private AudioManager audioManager;
    private Rectangle volDownBtn, volUpBtn, creditsBtn, mainMenuBtn, menuBtn, exitBtn;
    private int hoveredBtn = -1;

    private static final int   LIFT  = 3;
    private static final float SCALE = 1.05f;

    private Font minecraftFont;

    public SettingsScreen(GamePanel gamePanel, Settings settings, AudioManager audioManager) {
        this.gamePanel    = gamePanel;
        this.settings     = settings;
        this.audioManager = audioManager;
        loadFont();
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

    public void draw(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(20,10,40)); g2d.fillRect(0,0,width,height);

        int cx = width/2;
        g2d.setFont(mc(sf(width,36))); g2d.setColor(new Color(255,215,0));
        String title = "SETTINGS";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, cx-fm.stringWidth(title)/2, (int)(height*0.15));

        int volY = (int)(height*0.35);
        g2d.setFont(mc(sf(width,18))); g2d.setColor(Color.WHITE);
        String volLabel = "Music Volume: " + settings.getVolume() + "%";
        fm = g2d.getFontMetrics();
        g2d.drawString(volLabel, cx-fm.stringWidth(volLabel)/2, volY);

        int btnW=sf(width,50), btnH=sf(width,30), btnGap=sf(width,20), btnY=volY+sf(width,12);
        volDownBtn = new Rectangle(cx-btnGap/2-btnW, btnY, btnW, btnH);
        volUpBtn   = new Rectangle(cx+btnGap/2,      btnY, btnW, btnH);
        drawBtn(g2d, volDownBtn, "-",       new Color(80,60,140),  hoveredBtn==0, width);
        drawBtn(g2d, volUpBtn,   "+",       new Color(80,60,140),  hoveredBtn==1, width);

        int cBtnW=(int)(width*0.30), cBtnH=(int)(height*0.10);
        creditsBtn = new Rectangle(cx-cBtnW/2, (int)(height*0.52), cBtnW, cBtnH);
        drawBtn(g2d, creditsBtn, "CREDITS", new Color(120,80,40),  hoveredBtn==2, width);

        int navW=(int)(width*0.26), navH=(int)(height*0.10);
        int navY=(int)(height*0.78), navGap=(int)(width*0.02);
        int totalNav=navW*3+navGap*2, navStartX=cx-totalNav/2;
        mainMenuBtn = new Rectangle(navStartX,                 navY, navW, navH);
        menuBtn     = new Rectangle(navStartX+navW+navGap,     navY, navW, navH);
        exitBtn     = new Rectangle(navStartX+(navW+navGap)*2, navY, navW, navH);
        drawBtn(g2d, mainMenuBtn, "BACK",      new Color(60,100,60),  hoveredBtn==3, width);
        drawBtn(g2d, menuBtn,     "MAIN MENU", new Color(60,60,140),  hoveredBtn==4, width);
        drawBtn(g2d, exitBtn,     "EXIT GAME", new Color(140,40,40),  hoveredBtn==5, width);

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
        g2d.setColor(bg); g2d.fillRoundRect(r.x,r.y,r.width,r.height,8,8);
        g2d.setColor(Color.WHITE); g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(r.x,r.y,r.width,r.height,8,8); g2d.setStroke(new BasicStroke(1));
        g2d.setFont(mc(sf(screenW,13)));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, r.x+(r.width-fm.stringWidth(text))/2,
                r.y+(r.height+fm.getAscent()-fm.getDescent())/2);
        g2d.setTransform(old);
    }

    public void mouseClicked(int mx, int my) {
        if (NavButtons.handleClick(mx, my, gamePanel)) return;
        int vol = settings.getVolume();
        if (volDownBtn!=null && volDownBtn.contains(mx,my)) {
            settings.setVolume(Math.max(0,vol-10));
            audioManager.setVolume(settings.getVolume()/100f);
            settings.save(); gamePanel.repaint();
        } else if (volUpBtn!=null && volUpBtn.contains(mx,my)) {
            settings.setVolume(Math.min(100,vol+10));
            audioManager.setVolume(settings.getVolume()/100f);
            settings.save(); gamePanel.repaint();
        } else if (creditsBtn!=null && creditsBtn.contains(mx,my)) {
            gamePanel.setGameState(GameState.CREDITS);
        } else if (mainMenuBtn!=null && mainMenuBtn.contains(mx,my)) {
            settings.save(); gamePanel.setGameState(gamePanel.getPreviousState());
        } else if (menuBtn!=null && menuBtn.contains(mx,my)) {
            settings.save(); gamePanel.setGameState(GameState.MENU);
        } else if (exitBtn!=null && exitBtn.contains(mx,my)) {
            settings.save(); System.exit(0);
        }
    }

    public void mouseMoved(int mx, int my) {
        int prev = hoveredBtn; hoveredBtn = -1;
        if      (volDownBtn  != null && volDownBtn.contains(mx,my))  hoveredBtn=0;
        else if (volUpBtn    != null && volUpBtn.contains(mx,my))    hoveredBtn=1;
        else if (creditsBtn  != null && creditsBtn.contains(mx,my))  hoveredBtn=2;
        else if (mainMenuBtn != null && mainMenuBtn.contains(mx,my)) hoveredBtn=3;
        else if (menuBtn     != null && menuBtn.contains(mx,my))     hoveredBtn=4;
        else if (exitBtn     != null && exitBtn.contains(mx,my))     hoveredBtn=5;
        if (hoveredBtn != prev) gamePanel.repaint();
    }

    private int sf(int w, int base) { return Math.max(8,(int)(base*w/640.0)); }
}