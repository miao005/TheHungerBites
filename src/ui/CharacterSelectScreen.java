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

    // Background + button images
    private BufferedImage background;
    private BufferedImage imgSelect, imgReturn, imgArrowLeft, imgArrowRight;

    // Character sprites
    private Map<String, BufferedImage> sprites = new HashMap<>();
    private Map<String, ImageIcon> animatedSprites = new HashMap<>();

    //Font
    private Font pixelFont;

    // Selection state
    private int p1Index = 0;
    private int p2Index = 1;
    private boolean selectingP1 = true;

    // Hover state  (-1 = none, 0 = left, 1 = right, 2 = select, 3 = return)
    private int hoveredIndex = -1;

    // Hit-zones — recalculated every draw()
    private Rectangle leftArrow, rightArrow, selectBtn, returnBtn;

    public CharacterSelectScreen(GamePanel gamePanel, CharacterManager characterManager) {
        this.gamePanel = gamePanel;
        this.characterManager = characterManager;

        // Load custom font
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT,
                            getClass().getResourceAsStream("/resources/fonts/ARCADE_N.TTF"))
                    .deriveFont(Font.PLAIN, 16f);
        } catch (Exception e) {
            pixelFont = new Font("Monospaced", Font.BOLD, 16); // fallback
        }


        loadImages();
        loadSprites();
    }

    private void loadImages() {
        background   = tryLoad("/resources/character_select_bg.png");
        imgSelect    = tryLoad("/resources/buttons/btn_select.png");
        imgReturn    = tryLoad("/resources/buttons/btn_return.png");
        imgArrowLeft  = tryLoad("/resources/buttons/arrow_left.png");
        imgArrowRight = tryLoad("/resources/buttons/arrow_right.png");
    }

    private BufferedImage tryLoad(String path) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            return null; // will use placeholder
        }
    }

    private void loadSprites() {
        String[] names = {
                "Jollibee","RonaldMcDonald","BurgerKing","ColonelSanders",
                "TacoBell","Wendys","Poco","Julies"
        };
        for (String n : names) {
            // Try animated GIF sprite first
            URL gifUrl = getClass().getResource("/resources/sprites/" + n + "/" + n + "_idle.gif");
            if (gifUrl != null) {
                ImageIcon icon = new ImageIcon(gifUrl);
                icon.setImageObserver(gamePanel);
                animatedSprites.put(n, icon);
            } else {
                // Fall back to static PNG
                BufferedImage img = tryLoad("/resources/sprites/" + n + ".png");
                if (img != null) sprites.put(n, img);
            }
        }
    }

    public void reset() {
        p1Index = 0;
        p2Index = 1;
        selectingP1 = true;
        hoveredIndex = -1;
    }

    // ──────────Images (buttons, etc) ────────────────────────────────────────
    public void draw(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int currentIndex = selectingP1 ? p1Index : p2Index;
        Character ch = characterManager.createCharacter(currentIndex);
        String key = ch.getName().replaceAll("[^a-zA-Z]", "");
        ImageIcon animIcon = animatedSprites.get(key);

        // ──  Background (PNG shell — always) ────────────────────────
        if (background != null) {
            g2d.drawImage(background, 0, 0, width, height, null);
        } else {
            drawFallbackBackground(g2d, width, height);
        }

        // ──  Player label ────────────────────────────────────────────
        boolean isPvp = gamePanel.getGameMode().equals("PVP");
        String playerLabel = selectingP1 ? "PLAYER 1" : (isPvp ? "PLAYER 2" : "PLAYER 1");
        g2d.setFont(pixelFont.deriveFont((float) sf(width, 20)));
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(playerLabel,
                (width - fm.stringWidth(playerLabel)) / 2,
                (int)(height * 0.09));

        // ──  Character sprite (GIF or static PNG) ────────────────────
        // Pedestal center X = 0.1622, top = 0.7611
        // Sprite should sit on pedestal — bottom of sprite = pedestal top
        int spriteH  = (int)(height * 0.52);
        int spriteW  = spriteH;  // square aspect
        int pedestalCenterX = (int)(width * 0.1622);
        int pedestalTopY    = (int)(height * 0.761);
        int spriteX  = pedestalCenterX - spriteW / 2;
        int spriteY  = pedestalTopY - spriteH;
        if (animIcon != null) {
            g2d.drawImage(animIcon.getImage(), spriteX, spriteY, spriteW, spriteH, gamePanel);
        } else {
            BufferedImage sprite = sprites.get(key);
            if (sprite != null) {
                g2d.drawImage(sprite, spriteX, spriteY, spriteW, spriteH, null);
            } else {
                g2d.setColor(new Color(255, 200, 100, 200));
                g2d.fillOval(spriteX, spriteY, spriteW, spriteH);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Monospaced", Font.BOLD, sf(width, 10)));
                g2d.drawString("?", spriteX + spriteW / 2 - 4, spriteY + spriteH / 2 + 4);
            }
        }

        // ──  Character name — centered in pink header, white ─────────
        // Header spans x: 0.3964–0.8531, y mid: 0.2796
        g2d.setFont(pixelFont.deriveFont((float) sf(width, 20)));
        g2d.setColor(Color.WHITE);
        fm = g2d.getFontMetrics();
        String charName  = ch.getName().toUpperCase();
        int panelLeft    = (int)(width * 0.3964);
        int panelRight   = (int)(width * 0.8531);
        int panelW       = panelRight - panelLeft;
        g2d.drawString(charName,
                panelLeft + (panelW - fm.stringWidth(charName)) / 2,
                (int)(height * 0.295));

        // ──  Lore text — inside left cream cell ──────────────────────
        // Lore cell: x 0.3964–0.6151, y 0.3389–0.7444
        // Add inner padding
        int lorePad  = (int)(width * 0.012);
        int loreX    = panelLeft + lorePad;
        int loreTopY = (int)(height * 0.375);
        int loreW    = (int)(width * 0.6151) - panelLeft - lorePad * 2;
        int loreH    = (int)(height * 0.7444) - loreTopY;
        g2d.setFont(new Font("Monospaced", Font.BOLD, sf(width, 8)));
        g2d.setColor(new Color(30, 20, 60));
        drawWrappedText(g2d, ch.getBackstory(), loreX, loreTopY, loreW, loreH);

        // ──  Skills text — inside right cream cell ───────────────────
        // Skills cell: x 0.6151–0.8531, y 0.3389–0.7444
        int skillPad = (int)(width * 0.012);
        int sx       = (int)(width * 0.6151) + skillPad;
        int sy       = loreTopY;
        int sw       = panelRight - (int)(width * 0.6151) - skillPad * 2;
        int sh       = loreH;
        int gap      = (int)(sh * 0.32);

        drawSkillBlock(g2d,
                "BASIC ATTACK: " + ch.getBasicAttackName(),
                "DAMAGE: " + getBasicDmgRange(currentIndex) + "  MANA: 0",
                sx, sy, sw, width);
        drawSkillBlock(g2d,
                "SKILL: " + ch.getSkillAttackName(),
                "DAMAGE: " + getSkillDmgRange(currentIndex) + "  MANA: 30",
                sx, sy + gap, sw, width);
        drawSkillBlock(g2d,
                "ULTIMATE: " + ch.getUltimateAttackName(),
                "DAMAGE: " + getUltiDmgRange(currentIndex) + "  MANA: 50",
                sx, sy + gap * 2, sw, width);

        // ──  Calculate button bounds ────────────────────────────────
        int arrowSize  = (int)(height * 0.18);
        int arrowY     = (int)(height * 0.37);
        leftArrow  = new Rectangle((int)(width * 0.02), arrowY, arrowSize, arrowSize);
        rightArrow = new Rectangle((int)(width * 0.88), arrowY, arrowSize, arrowSize);

        returnBtn = new Rectangle(
                (int)(width * 0.03), (int)(height * 0.85),
                (int)(width * 0.15), (int)(height * 0.11));
        selectBtn = new Rectangle(
                (int)(width * 0.35), (int)(height * 0.84),
                (int)(width * 0.30), (int)(height * 0.17));

        // ──  Draw buttons (PNG or placeholder) + hover glow ─────────
        drawButton(g2d, leftArrow,  imgArrowLeft,  "LEFT", hoveredIndex == 0, false, width);
        drawButton(g2d, rightArrow, imgArrowRight, "RIGHT", hoveredIndex == 1, false, width);
        drawButton(g2d, returnBtn,  imgReturn,     "RETURN", hoveredIndex == 3, true, width);
        drawButton(g2d, selectBtn,  imgSelect,     "SELECT", hoveredIndex == 2, true, width);

        // ──  Dot indicators ─────────────────────────────────────────
        int dotCount = characterManager.getRosterSize();
        int dotSize  = Math.max(5, width / 90);
        int spacing  = dotSize + 5;
        int dotStartX = (width - dotCount * spacing) / 2;
        int dotY      = (int)(height * 0.815);
        for (int i = 0; i < dotCount; i++) {
            g2d.setColor(i == currentIndex
                    ? new Color(255, 100, 180)
                    : new Color(120, 80, 160));
            g2d.fillOval(dotStartX + i * spacing, dotY, dotSize, dotSize);
        }
        NavButtons.draw((Graphics2D) g, width, height);
    }

    // ── Button draw (mirrors MenuScreen logic) ────────────────────────
    private void drawButton(Graphics2D g2d, Rectangle bounds, BufferedImage img,
                            String fallbackLabel, boolean hovered,
                            boolean isTextButton, int screenW) {
        if (bounds == null) return;

        if (img != null) {
            // Draw the PNG scaled to bounds
            g2d.drawImage(img, bounds.x, bounds.y, bounds.width, bounds.height, null);

            // Gold glow hover (same as MenuScreen)
            if (hovered) {
                g2d.setColor(new Color(255, 215, 0, 100));
                g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                g2d.setColor(new Color(255, 215, 0, 200));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
                g2d.setStroke(new BasicStroke(1));
            }
        } else {
            // ── Placeholder ───────────────────────────────────────────
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color base  = isTextButton ? new Color(220, 80, 160) : new Color(200, 70, 150);
            Color hover = isTextButton ? new Color(255, 120, 200) : new Color(255, 100, 180);
            g2d.setColor(hovered ? hover : base);
            g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 20, 20);

            // Gold glow border on hover
            if (hovered) {
                g2d.setColor(new Color(255, 215, 0, 220));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(bounds.x - 2, bounds.y - 2,
                        bounds.width + 4, bounds.height + 4, 22, 22);
                g2d.setStroke(new BasicStroke(1));
            } else {
                g2d.setColor(new Color(255, 150, 210));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 20, 20);
                g2d.setStroke(new BasicStroke(1));
            }

            // Label
            int fontSize = Math.max(8, (int)(bounds.height * 0.45));
            g2d.setFont(new Font("Monospaced", Font.BOLD, fontSize));
            g2d.setColor(Color.WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(fallbackLabel,
                    bounds.x + (bounds.width  - fm.stringWidth(fallbackLabel)) / 2,
                    bounds.y + (bounds.height + fm.getAscent() - fm.getDescent()) / 2);
        }
    }

    // ── Skill block ───────────────────────────────────────────────────
    private void drawSkillBlock(Graphics2D g2d, String title, String details,
                                int x, int y, int maxW, int screenW) {
        int lh = g2d.getFontMetrics().getHeight();
        g2d.setFont(new Font("Monospaced", Font.BOLD, sf(screenW, 9)));
        g2d.setColor(new Color(50, 0, 70));
        drawWrappedText(g2d, title, x, y, maxW, lh * 2);

        g2d.setFont(new Font("Monospaced", Font.PLAIN, sf(screenW, 9)));
        g2d.setColor(new Color(60, 40, 80));
        String[] parts = details.split("  ");
        for (int i = 0; i < parts.length; i++)
            g2d.drawString(parts[i], x, y + lh * (i + 2));
    }

    // ── Wrapped text ──────────────────────────────────────────────────
    private void drawWrappedText(Graphics2D g2d, String text,
                                 int x, int y, int maxW, int maxH) {
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
        if (line.length() > 0 && curY - y <= maxH)
            g2d.drawString(line.toString(), x, curY);
    }

    // ── Fallback background when PNG is missing ───────────────────────
    private void drawFallbackBackground(Graphics2D g2d, int w, int h) {
        g2d.setColor(new Color(10, 10, 60));
        g2d.fillRect(0, 0, w, h);

        g2d.setColor(new Color(20, 20, 80));
        g2d.fillRoundRect((int)(w*.17),(int)(h*.10),(int)(w*.66),(int)(h*.72),12,12);
        g2d.setColor(new Color(220,80,160));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect((int)(w*.17),(int)(h*.10),(int)(w*.66),(int)(h*.72),12,12);
        g2d.setStroke(new BasicStroke(1));

        g2d.setColor(new Color(230,100,170));
        g2d.fillRect((int)(w*.17),(int)(h*.10),(int)(w*.66),(int)(h*.155));

        g2d.setColor(new Color(240,230,245));
        g2d.fillRect((int)(w*.17),(int)(h*.255),(int)(w*.33),(int)(h*.555));
        g2d.fillRect((int)(w*.50),(int)(h*.255),(int)(w*.33),(int)(h*.555));

        g2d.setColor(new Color(180,80,160));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine((int)(w*.50),(int)(h*.255),(int)(w*.50),(int)(h*.81));
        g2d.setStroke(new BasicStroke(1));

        g2d.setFont(new Font("Monospaced", Font.BOLD, sf(w, 16)));
        g2d.setColor(new Color(220,60,140));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString("LORE",
                (int)(w*.17)+((int)(w*.33)-fm.stringWidth("LORE"))/2, (int)(h*.305));
        g2d.drawString("SKILLS",
                (int)(w*.50)+((int)(w*.33)-fm.stringWidth("SKILLS"))/2, (int)(h*.305));
    }

    // ── Scale helper ──────────────────────────────────────────────────
    private int sf(int width, int base) {
        return Math.max(7, (int)(base * width / 640.0));
    }

    // ── Damage ranges ─────────────────────────────────────────────────
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

    // ── Mouse input ───────────────────────────────────────────────────
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