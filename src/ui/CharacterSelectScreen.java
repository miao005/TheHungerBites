package ui;

import main.Character;
import main.CharacterManager;
import main.GamePanel;
import main.GameState;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CharacterSelectScreen {
    private GamePanel gamePanel;
    private CharacterManager characterManager;

    private BufferedImage background;
    private BufferedImage imgSelect, imgReturn, imgArrowLeft, imgArrowRight;

    private Map<String, BufferedImage> sprites = new HashMap<>();
    private Map<String, ImageIcon> animatedSprites = new HashMap<>();

    private Font pixelFont;

    private int p1Index = 0;
    private int p2Index = 1;
    private boolean selectingP1 = true;

    // -1=none, 0=left, 1=right, 2=select, 3=return
    private int hoveredIndex = -1;

    private Rectangle leftArrow, rightArrow, selectBtn, returnBtn;

    private static final int   LIFT  = 3;
    private static final float SCALE = 1.05f;

    public CharacterSelectScreen(GamePanel gamePanel, CharacterManager characterManager) {
        this.gamePanel = gamePanel;
        this.characterManager = characterManager;
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT,
                            getClass().getResourceAsStream("/resources/fonts/ARCADE_N.TTF"))
                    .deriveFont(Font.PLAIN, 16f);
        } catch (Exception e) {
            pixelFont = new Font("Monospaced", Font.BOLD, 16);
        }
        loadImages();
        loadSprites();
    }

    private void loadImages() {
        background    = tryLoad("/resources/character_select_bg.png");
        imgSelect     = tryLoad("/resources/buttons/btn_select.png");
        imgReturn     = tryLoad("/resources/buttons/btn_return.png");
        imgArrowLeft  = tryLoad("/resources/buttons/arrow_left.png");
        imgArrowRight = tryLoad("/resources/buttons/arrow_right.png");
    }

    private BufferedImage tryLoad(String path) {
        try { return ImageIO.read(getClass().getResourceAsStream(path)); }
        catch (Exception e) { return null; }
    }

    private void loadSprites() {
        String[] names = {"Jollibee","RonaldMcDonald","BurgerKing","ColonelSanders",
                "TacoBell","Wendys","Poco","Julies"};
        for (String n : names) {
            URL gifUrl = getClass().getResource("/resources/sprites/" + n + "/" + n + "_idle.gif");
            if (gifUrl != null) {
                ImageIcon icon = new ImageIcon(gifUrl);
                icon.setImageObserver(gamePanel);
                animatedSprites.put(n, icon);
            } else {
                BufferedImage img = tryLoad("/resources/sprites/" + n + ".png");
                if (img != null) sprites.put(n, img);
            }
        }
    }

    public void reset() {
        p1Index = 0; p2Index = 1; selectingP1 = true; hoveredIndex = -1;
    }

    public void draw(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int currentIndex = selectingP1 ? p1Index : p2Index;
        Character ch = characterManager.createCharacter(currentIndex);
        String key = ch.getName().replaceAll("[^a-zA-Z]", "");
        ImageIcon animIcon = animatedSprites.get(key);

        if (background != null) g2d.drawImage(background, 0, 0, width, height, null);
        else drawFallbackBackground(g2d, width, height);

        boolean isPvp = gamePanel.getGameMode().equals("PVP");
        String playerLabel = selectingP1 ? "PLAYER 1" : (isPvp ? "PLAYER 2" : "PLAYER 1");
        g2d.setFont(pixelFont.deriveFont((float) sf(width, 20)));
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(playerLabel, (width - fm.stringWidth(playerLabel)) / 2, (int)(height * 0.09));

        int spriteH = (int)(height * 0.52);
        int spriteW = spriteH;
        int pedestalCenterX = (int)(width * 0.21);
        int pedestalTopY    = (int)(height * 0.8);
        int spriteX = pedestalCenterX - spriteW / 2;
        int spriteY = pedestalTopY - spriteH;
        if (animIcon != null) {
            g2d.drawImage(animIcon.getImage(), spriteX, spriteY, spriteW, spriteH, gamePanel);
        } else {
            BufferedImage sprite = sprites.get(key);
            if (sprite != null) g2d.drawImage(sprite, spriteX, spriteY, spriteW, spriteH, null);
            else {
                g2d.setColor(new Color(255, 200, 100, 200));
                g2d.fillOval(spriteX, spriteY, spriteW, spriteH);
            }
        }

        g2d.setFont(pixelFont.deriveFont((float) sf(width, 15)));
        g2d.setColor(Color.WHITE);
        fm = g2d.getFontMetrics();
        String charName = ch.getName().toUpperCase();
        int panelLeft  = (int)(width * 0.3964);
        int panelRight = (int)(width * 0.8531);
        int panelW     = panelRight - panelLeft;
        g2d.drawString(charName, panelLeft + (panelW - fm.stringWidth(charName)) / 2, (int)(height * 0.295));

        int lorePad  = (int)(width * 0.012);
        int loreX    = panelLeft + lorePad;
        int loreTopY = (int)(height * 0.4);
        int loreW    = (int)(width * 0.6151) - panelLeft - lorePad * 2;
        int loreH    = (int)(height * 0.7444) - loreTopY;
        g2d.setFont(new Font("Monospaced", Font.BOLD, sf(width, 7)));
        g2d.setColor(new Color(30, 20, 60));
        drawWrappedText(g2d, ch.getBackstory(), loreX, loreTopY, loreW, loreH);

        int skillPad = (int)(width * 0.012);
        int sx  = (int)(width * 0.6151) + skillPad;
        int sy  = loreTopY;
        int sw  = panelRight - (int)(width * 0.6151) - skillPad * 2;
        int sh  = loreH;
        int gap = (int)(sh * 0.32);
        drawSkillBlock(g2d, "BASIC ATTACK: " + ch.getBasicAttackName(),
                "DAMAGE: " + getBasicDmgRange(currentIndex) + "  MANA: 0", sx, sy, sw, width);
        drawSkillBlock(g2d, "SKILL: " + ch.getSkillAttackName(),
                "DAMAGE: " + getSkillDmgRange(currentIndex) + "  MANA: 30", sx, sy + gap, sw, width);
        drawSkillBlock(g2d, "ULTIMATE: " + ch.getUltimateAttackName(),
                "DAMAGE: " + getUltiDmgRange(currentIndex) + "  MANA: 50", sx, sy + gap * 2, sw, width);

        int arrowSize = (int)(height * 0.18);
        int arrowY    = (int)(height * 0.37);
        leftArrow  = new Rectangle((int)(width * 0.02), arrowY, arrowSize, arrowSize);
        rightArrow = new Rectangle((int)(width * 0.88), arrowY, arrowSize, arrowSize);
        returnBtn  = new Rectangle((int)(width * 0.03), (int)(height * 0.05),
                (int)(width * 0.17), (int)(height * 0.11));
        selectBtn  = new Rectangle((int)(width * 0.35), (int)(height * 0.86),
                (int)(width * 0.30), (int)(height * 0.13));

        // Arrows: scale only (no upward lift)
        drawScaleOnly(g2d, leftArrow,  imgArrowLeft,  "LEFT",   hoveredIndex == 0, width);
        drawScaleOnly(g2d, rightArrow, imgArrowRight, "RIGHT",  hoveredIndex == 1, width);
        // Regular buttons: lift + scale
        drawLiftScale(g2d, returnBtn, imgReturn, "RETURN", hoveredIndex == 3, true,  width);
        drawLiftScale(g2d, selectBtn, imgSelect, "SELECT", hoveredIndex == 2, true,  width);

        int dotCount  = characterManager.getRosterSize();
        int dotSize   = Math.max(5, width / 90);
        int spacing   = dotSize + 5;
        int dotStartX = (width - dotCount * spacing) / 2;
        int dotY      = (int)(height * 0.815);
        for (int i = 0; i < dotCount; i++) {
            g2d.setColor(i == currentIndex ? new Color(255, 100, 180) : new Color(120, 80, 160));
            g2d.fillOval(dotStartX + i * spacing, dotY, dotSize, dotSize);
        }
        NavButtons.draw(g2d, width, height);
    }

    /** Lift + scale — for Select and Return buttons */
    private void drawLiftScale(Graphics2D g2d, Rectangle r, BufferedImage img,
                               String fallback, boolean hovered, boolean isText, int screenW) {
        if (r == null) return;
        java.awt.geom.AffineTransform old = g2d.getTransform();
        if (hovered) {
            int cx = r.x + r.width / 2;
            int cy = r.y + r.height / 2;
            g2d.translate(cx, cy - LIFT);
            g2d.scale(SCALE, SCALE);
            g2d.translate(-cx, -cy);
        }
        drawContent(g2d, r, img, fallback, hovered, isText, screenW);
        g2d.setTransform(old);
    }

    /** Scale only — for left/right arrows */
    private void drawScaleOnly(Graphics2D g2d, Rectangle r, BufferedImage img,
                               String fallback, boolean hovered, int screenW) {
        if (r == null) return;
        java.awt.geom.AffineTransform old = g2d.getTransform();
        if (hovered) {
            int cx = r.x + r.width / 2;
            int cy = r.y + r.height / 2;
            g2d.translate(cx, cy);
            g2d.scale(SCALE, SCALE);
            g2d.translate(-cx, -cy);
        }
        drawContent(g2d, r, img, fallback, hovered, false, screenW);
        g2d.setTransform(old);
    }

    private void drawContent(Graphics2D g2d, Rectangle r, BufferedImage img,
                             String fallback, boolean hovered, boolean isText, int screenW) {
        if (img != null) {
            g2d.drawImage(img, r.x, r.y, r.width, r.height, null);
        } else {
            Color base  = isText ? new Color(220, 80, 160) : new Color(200, 70, 150);
            Color hover = isText ? new Color(255, 120, 200) : new Color(255, 100, 180);
            g2d.setColor(hovered ? hover : base);
            g2d.fillRoundRect(r.x, r.y, r.width, r.height, 20, 20);
            g2d.setColor(new Color(255, 150, 210));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(r.x, r.y, r.width, r.height, 20, 20);
            g2d.setStroke(new BasicStroke(1));
            int fontSize = Math.max(8, (int)(r.height * 0.45));
            g2d.setFont(new Font("Monospaced", Font.BOLD, fontSize));
            g2d.setColor(Color.WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(fallback,
                    r.x + (r.width  - fm.stringWidth(fallback)) / 2,
                    r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2);
        }
    }

    private void drawSkillBlock(Graphics2D g2d, String title, String details,
                                int x, int y, int maxW, int screenW) {
        int lh = g2d.getFontMetrics().getHeight();
        g2d.setFont(new Font("Monospaced", Font.BOLD, sf(screenW, 6)));
        g2d.setColor(new Color(50, 0, 70));
        drawWrappedText(g2d, title, x, y, maxW, lh * 2);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, sf(screenW, 6)));
        g2d.setColor(new Color(60, 40, 80));
        String[] parts = details.split("  ");
        for (int i = 0; i < parts.length; i++)
            g2d.drawString(parts[i], x, y + lh * (i + 2));
    }

    private void drawWrappedText(Graphics2D g2d, String text, int x, int y, int maxW, int maxH) {
        if (text == null || text.isEmpty()) return;
        FontMetrics fm = g2d.getFontMetrics();
        int lh = fm.getHeight();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int curY = y + lh;
        for (String word : words) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(test) > maxW) {
                if (curY - y > maxH) break;
                g2d.drawString(line.toString(), x, curY);
                curY += lh;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0 && curY - y <= maxH) g2d.drawString(line.toString(), x, curY);
    }

    private void drawFallbackBackground(Graphics2D g2d, int w, int h) {
        g2d.setColor(new Color(10, 10, 60)); g2d.fillRect(0, 0, w, h);
        g2d.setColor(new Color(20, 20, 80));
        g2d.fillRoundRect((int)(w*.17),(int)(h*.10),(int)(w*.66),(int)(h*.72),12,12);
        g2d.setColor(new Color(220,80,160)); g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect((int)(w*.17),(int)(h*.10),(int)(w*.66),(int)(h*.72),12,12);
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(new Color(230,100,170));
        g2d.fillRect((int)(w*.17),(int)(h*.10),(int)(w*.66),(int)(h*.155));
        g2d.setColor(new Color(240,230,245));
        g2d.fillRect((int)(w*.17),(int)(h*.255),(int)(w*.33),(int)(h*.555));
        g2d.fillRect((int)(w*.50),(int)(h*.255),(int)(w*.33),(int)(h*.555));
    }

    private int sf(int width, int base) { return Math.max(7, (int)(base * width / 640.0)); }

    private String getBasicDmgRange(int i) {
        int[][] r = {{16,22},{17,23},{22,28},{18,22},{20,26},{18,26},{15,20},{19,25}};
        return i < r.length ? r[i][0]+" - "+r[i][1] : "?";
    }
    private String getSkillDmgRange(int i) {
        int[][] r = {{24,32},{26,34},{30,40},{25,32},{30,38},{25,33},{22,28},{28,36}};
        return i < r.length ? r[i][0]+" - "+r[i][1] : "?";
    }
    private String getUltiDmgRange(int i) {
        int[][] r = {{38,48},{41,51},{45,55},{40,50},{45,58},{40,52},{35,45},{43,55}};
        return i < r.length ? r[i][0]+" - "+r[i][1] : "?";
    }

    public void mouseClicked(int mx, int my) {
        if (NavButtons.handleClick(mx, my, gamePanel)) return;
        boolean isPvp = gamePanel.getGameMode().equals("PVP");

        if (leftArrow != null && leftArrow.contains(mx, my)) {
            gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
            if (selectingP1) p1Index = (p1Index - 1 + characterManager.getRosterSize()) % characterManager.getRosterSize();
            else             p2Index = (p2Index - 1 + characterManager.getRosterSize()) % characterManager.getRosterSize();
            gamePanel.repaint();
        } else if (rightArrow != null && rightArrow.contains(mx, my)) {
            gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
            if (selectingP1) p1Index = (p1Index + 1) % characterManager.getRosterSize();
            else             p2Index = (p2Index + 1) % characterManager.getRosterSize();
            gamePanel.repaint();
        } else if (selectBtn != null && selectBtn.contains(mx, my)) {
            gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
            if (isPvp) {
                if (selectingP1) { selectingP1 = false; gamePanel.repaint(); }
                else             { gamePanel.startPvpBattle(p1Index, p2Index); }
            } else if (gamePanel.getGameMode().equals("PVAI")) {
                gamePanel.startPvAiBattle(p1Index);
            } else if (gamePanel.getGameMode().equals("ARCADE")) {
                gamePanel.startArcade(p1Index);
            }
        } else if (returnBtn != null && returnBtn.contains(mx, my)) {
            gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
            if (!selectingP1 && isPvp) { selectingP1 = true; gamePanel.repaint(); }
            else                       { reset(); gamePanel.setGameState(GameState.MENU); }
        }
    }

    public void mouseMoved(int mx, int my) {
        int prev = hoveredIndex;
        hoveredIndex = -1;
        if      (leftArrow  != null && leftArrow.contains(mx, my))  hoveredIndex = 0;
        else if (rightArrow != null && rightArrow.contains(mx, my)) hoveredIndex = 1;
        else if (selectBtn  != null && selectBtn.contains(mx, my))  hoveredIndex = 2;
        else if (returnBtn  != null && returnBtn.contains(mx, my))  hoveredIndex = 3;
        if (hoveredIndex != prev) gamePanel.repaint();
    }
}