package ui;

import main.Character;
import main.GamePanel;
import main.GameState;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * RoundCutscene — animated intro shown before each battle round.
 *
 * Timeline (total ~3.5 seconds):
 *   0.0s – 0.6s  : Background fades in
 *   0.6s – 1.4s  : P1 sprite slides in from left
 *   0.8s – 1.6s  : P2 sprite slides in from right
 *   1.4s – 2.2s  : "ROUND X" banner drops from top
 *   2.2s – 2.8s  : "FIGHT!" flashes in centre
 *   2.8s – 3.5s  : Everything fades out → transitions to BATTLE
 */
public class RoundCutscene {

    private final GamePanel gamePanel;

    // Characters
    private Character player1, player2;
    private int roundNumber;

    // Assets — placeholders used if images are missing
    private BufferedImage background;
    private BufferedImage sprite1, sprite2;
    private Font pixelFont;

    // Timing
    private long startTime = -1;
    private static final long TOTAL_MS   = 3500;
    private static final long FADE_IN    =  600;
    private static final long P1_START   =  600;
    private static final long P2_START   =  800;
    private static final long SLIDE_DUR  =  800;
    private static final long BANNER_IN  = 1400;
    private static final long FIGHT_IN   = 2200;
    private static final long FADE_OUT   = 2800;
    private boolean finished = false;

    public RoundCutscene(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        loadFont();
    }

    private void loadFont() {
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT,
                            getClass().getResourceAsStream("/resources/fonts/ARCADE_N.TTF"))
                    .deriveFont(Font.BOLD, 48f);
        } catch (Exception e) {
            pixelFont = new Font("Monospaced", Font.BOLD, 48);
        }
    }

    /**
     * Call this every time before entering CUTSCENE state.
     */
    public void setup(Character p1, Character p2, int round) {
        this.player1    = p1;
        this.player2    = p2;
        this.roundNumber = round;
        this.finished   = false;
        this.startTime  = -1;

        // Load background
        background = tryLoad("/resources/cutscene_bg.png");
        if (background == null) background = tryLoad("/resources/Maps/jollibee_map.png");

        // Load sprites (reuse battle sprites)
        String k1 = spriteKey(p1);
        String k2 = spriteKey(p2);
        sprite1 = tryLoad("/resources/sprites/" + k1 + ".png");
        sprite2 = tryLoad("/resources/sprites/" + k2 + ".png");
    }

    private BufferedImage tryLoad(String path) {
        try { return ImageIO.read(getClass().getResourceAsStream(path)); }
        catch (Exception e) { return null; }
    }

    /** Called from GamePanel's paintComponent when state == CUTSCENE */
    public void draw(Graphics g, int width, int height) {
        if (startTime == -1) startTime = System.currentTimeMillis();

        long elapsed = System.currentTimeMillis() - startTime;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ── 1. Background ──────────────────────────────────────────────
        float bgAlpha = clamp((float) elapsed / FADE_IN);
        drawBackground(g2d, width, height, bgAlpha);

        // ── 2. P1 sprite — slides in from left ────────────────────────
        if (elapsed >= P1_START) {
            float p1Progress = clamp((float)(elapsed - P1_START) / SLIDE_DUR);
            float p1Ease     = easeOut(p1Progress);
            int spriteH = (int)(height * 0.60);
            int spriteW = spriteH;
            int targetX = (int)(width * 0.08);
            int startX  = -spriteW;
            int p1X     = (int)(startX + (targetX - startX) * p1Ease);
            int p1Y     = (int)(height * 0.25);

            float opacity = Math.min(1f, p1Ease * 2f);
            drawSprite(g2d, sprite1, player1, p1X, p1Y, spriteW, spriteH, false, opacity);
        }

        // ── 3. P2 sprite — slides in from right ───────────────────────
        if (elapsed >= P2_START) {
            float p2Progress = clamp((float)(elapsed - P2_START) / SLIDE_DUR);
            float p2Ease     = easeOut(p2Progress);
            int spriteH = (int)(height * 0.60);
            int spriteW = spriteH;
            int targetX = (int)(width * 0.65);
            int startX  = width + spriteW;
            int p2X     = (int)(startX + (targetX - startX) * p2Ease);
            int p2Y     = (int)(height * 0.25);

            float opacity = Math.min(1f, p2Ease * 2f);
            drawSprite(g2d, sprite2, player2, p2X, p2Y, spriteW, spriteH, true, opacity);
        }

        // ── 4. VS badge in the centre ─────────────────────────────────
        if (elapsed >= P1_START + 200) {
            float vsAlpha = clamp((float)(elapsed - P1_START - 200) / 400f);
            drawVsBadge(g2d, width, height, vsAlpha);
        }

        // ── 5. ROUND X banner — drops from top ────────────────────────
        if (elapsed >= BANNER_IN) {
            float bannerProgress = clamp((float)(elapsed - BANNER_IN) / 500f);
            float bannerEase     = easeOut(bannerProgress);
            int targetY = (int)(height * 0.12);
            int startY  = -(int)(height * 0.15);
            int bannerY = (int)(startY + (targetY - startY) * bannerEase);
            drawRoundBanner(g2d, width, height, bannerY, 1f);
        }

        // ── 6. FIGHT! text — flashes in centre ────────────────────────
        if (elapsed >= FIGHT_IN) {
            float fightProgress = clamp((float)(elapsed - FIGHT_IN) / 300f);
            float fightScale    = 1f + (1f - fightProgress) * 0.6f; // zoom from 1.6x → 1.0x
            float fightAlpha    = fightProgress;
            drawFightText(g2d, width, height, fightScale, fightAlpha);
        }

        // ── 7. Global fade out ────────────────────────────────────────
        if (elapsed >= FADE_OUT) {
            float fadeProgress = clamp((float)(elapsed - FADE_OUT) / (TOTAL_MS - FADE_OUT));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeProgress));
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, height);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // ── 8. Transition when done ───────────────────────────────────
        if (elapsed >= TOTAL_MS && !finished) {
            finished = true;
            gamePanel.setGameState(GameState.BATTLE);
            return;
        }

        gamePanel.repaint(); // keep animating
    }

    // ─────────────────────────────────────────────────────────────────
    //  Draw helpers
    // ─────────────────────────────────────────────────────────────────

    private void drawBackground(Graphics2D g2d, int w, int h, float alpha) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        if (background != null) {
            g2d.drawImage(background, 0, 0, w, h, null);
        } else {
            // Placeholder: dark purple gradient
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(20, 10, 60),
                    0, h, new Color(60, 20, 100));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);

            // Pixel-style ground line
            g2d.setColor(new Color(180, 100, 255, 120));
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine(0, (int)(h * 0.82), w, (int)(h * 0.82));

            // Ground fill
            g2d.setColor(new Color(40, 20, 80, 180));
            g2d.fillRect(0, (int)(h * 0.82), w, (int)(h * 0.18));

            // Stars
            g2d.setStroke(new BasicStroke(1));
            g2d.setColor(new Color(255, 255, 200, 180));
            int[][] stars = {{80,40},{200,70},{350,30},{500,55},{650,35},{750,65},{900,45}};
            for (int[] s : stars) {
                if (s[0] < w && s[1] < h) {
                    g2d.fillOval(s[0]-2, s[1]-2, 5, 5);
                }
            }
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawSprite(Graphics2D g2d, BufferedImage sprite, Character ch,
                            int x, int y, int w, int h, boolean flip, float alpha) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Shadow
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillOval(x + w/6, y + h - 14, w*2/3, 14);

        if (sprite != null) {
            if (flip)
                g2d.drawImage(sprite, x+w, y, x, y+h,
                        0, 0, sprite.getWidth(), sprite.getHeight(), null);
            else
                g2d.drawImage(sprite, x, y, w, h, null);
        } else {
            // Placeholder character silhouette
            Color baseColor = flip ? new Color(220, 80, 80) : new Color(80, 140, 220);
            g2d.setColor(baseColor);
            g2d.fillRoundRect(x + w/4, y + h/5, w/2, h*4/5, 16, 16);  // body
            g2d.fillOval(x + w/3, y, w/3, w/3);                         // head

            // Name label on placeholder
            g2d.setFont(new Font("Monospaced", Font.BOLD, Math.max(8, w / 9)));
            g2d.setColor(Color.WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            String name = ch != null ? ch.getName() : "???";
            if (name.length() > 8) name = name.substring(0, 8);
            g2d.drawString(name, x + (w - fm.stringWidth(name)) / 2, y + h / 2);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawVsBadge(Graphics2D g2d, int w, int h, float alpha) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int cx = w / 2;
        int cy = (int)(h * 0.52);
        int r  = (int)(h * 0.09);

        // Glowing circle
        g2d.setColor(new Color(255, 50, 50, 180));
        g2d.fillOval(cx - r, cy - r, r*2, r*2);
        g2d.setColor(new Color(255, 200, 0));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawOval(cx - r, cy - r, r*2, r*2);
        g2d.setStroke(new BasicStroke(1));

        // VS text
        g2d.setFont(pixelFont.deriveFont((float)(r * 0.9)));
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString("VS", cx - fm.stringWidth("VS")/2, cy + fm.getAscent()/2 - 4);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawRoundBanner(Graphics2D g2d, int w, int h, int y, float alpha) {
        String text = "ROUND  " + roundNumber;
        int bannerH = (int)(h * 0.13);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Banner background
        g2d.setColor(new Color(10, 5, 40, 210));
        g2d.fillRect(0, y, w, bannerH);

        // Gold border lines
        g2d.setColor(new Color(255, 215, 0));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(0, y,          w, y);
        g2d.drawLine(0, y+bannerH,  w, y+bannerH);
        g2d.setStroke(new BasicStroke(1));

        // Text
        Font bannerFont = pixelFont.deriveFont((float)(bannerH * 0.58));
        g2d.setFont(bannerFont);
        FontMetrics fm = g2d.getFontMetrics();

        // Shadow
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.drawString(text, (w - fm.stringWidth(text))/2 + 3, y + bannerH - (int)(bannerH*0.22) + 3);

        // Gold text
        g2d.setColor(new Color(255, 215, 0));
        g2d.drawString(text, (w - fm.stringWidth(text))/2, y + bannerH - (int)(bannerH*0.22));

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawFightText(Graphics2D g2d, int w, int h, float scale, float alpha) {
        String text = "FIGHT!";
        Font fightFont = pixelFont.deriveFont((float)(h * 0.16));

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, alpha)));

        int cx = w / 2;
        int cy = (int)(h * 0.52);

        // Save transform
        java.awt.geom.AffineTransform old = g2d.getTransform();
        g2d.translate(cx, cy);
        g2d.scale(scale, scale);
        g2d.translate(-cx, -cy);

        g2d.setFont(fightFont);
        FontMetrics fm = g2d.getFontMetrics();
        int fx = cx - fm.stringWidth(text)/2;
        int fy = cy + fm.getAscent()/2 - 10;

        // Red glow
        for (int i = 6; i >= 1; i--) {
            g2d.setColor(new Color(255, 50, 50, 30));
            g2d.drawString(text, fx - i, fy);
            g2d.drawString(text, fx + i, fy);
            g2d.drawString(text, fx, fy - i);
            g2d.drawString(text, fx, fy + i);
        }

        // Black outline
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, fx+3, fy+3);

        // White text with red gradient
        g2d.setColor(new Color(255, 230, 80));
        g2d.drawString(text, fx, fy);

        g2d.setTransform(old);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    // ─────────────────────────────────────────────────────────────────
    //  Easing & utilities
    // ─────────────────────────────────────────────────────────────────

    private float easeOut(float t) {
        return 1f - (1f - t) * (1f - t);
    }

    private float clamp(float t) {
        return Math.max(0f, Math.min(1f, t));
    }

    private String spriteKey(Character ch) {
        if (ch == null) return "";
        return ch.getName().replaceAll("[^a-zA-Z]", "");
    }

    public boolean isFinished() { return finished; }
}