package ui;

import main.GamePanel;
import main.GameState;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NavButtons {
    private static Rectangle settingsBtn;
    private static Rectangle leaderboardBtn;

    private static BufferedImage settingsIcon;
    private static BufferedImage leaderboardIcon;
    private static boolean iconsLoaded = false;

    // Hover tracking
    private static boolean hoveredSettings    = false;
    private static boolean hoveredLeaderboard = false;

    private static final int   LIFT  = 2;
    private static final float SCALE = 1.08f;

    private static void loadIcons() {
        if (iconsLoaded) return;
        iconsLoaded = true;
        try {
            settingsIcon    = ImageIO.read(NavButtons.class.getResourceAsStream("/resources/buttons/btn_settings.png"));
            leaderboardIcon = ImageIO.read(NavButtons.class.getResourceAsStream("/resources/buttons/btn_leaderboard.png"));
        } catch (Exception ignored) {}
    }

    // ── Standard draw (top-right corner) ──────────────────────────────
    public static void draw(Graphics2D g2d, int width, int height) {
        loadIcons();
        int btnW = (int)(width  * 0.08);
        int btnH = (int)(height * 0.07);
        int pad  = (int)(width  * 0.01);
        int top  = (int)(height * 0.01);

        settingsBtn    = new Rectangle(width - pad - btnW,           top, btnW, btnH);
        leaderboardBtn = new Rectangle(width - pad - btnW * 2 - pad, top, btnW, btnH);

        drawBtn(g2d, settingsBtn,    settingsIcon,    "S", new Color(60,80,160),   hoveredSettings);
        drawBtn(g2d, leaderboardBtn, leaderboardIcon, "L", new Color(100,60,140),  hoveredLeaderboard);
    }

    // ── Battle draw ────────────────────────────────────────────────────
    public static void drawBattle(Graphics2D g2d, int width, int hpBarH, boolean isArcadeMode) {
        loadIcons();
        int sBtnH = (int)(hpBarH * 0.28);
        int sBtnW = sBtnH;
        int lBtnH = (int)(hpBarH * 0.28);
        int lBtnW = (int)(sBtnH * 2);
        int gap   = (int)(width * 0.015);
        int totalW = sBtnW + lBtnW + gap;
        int startX = (width - totalW) / 2;
        int cy = isArcadeMode
                ? (hpBarH - Math.max(sBtnH, lBtnH)) / 2
                : hpBarH / 2 + (int)(hpBarH * 0.2);

        settingsBtn    = new Rectangle(startX,               cy, sBtnW, sBtnH);
        leaderboardBtn = new Rectangle(startX + sBtnW + gap, cy, lBtnW, lBtnH);

        drawBtn(g2d, leaderboardBtn, leaderboardIcon, "L", new Color(100,60,140), hoveredLeaderboard);
        drawBtn(g2d, settingsBtn,    settingsIcon,    "S", new Color(60,80,160),  hoveredSettings);
    }

    private static void drawBtn(Graphics2D g2d, Rectangle r, BufferedImage icon,
                                String fallback, Color bg, boolean hovered) {
        java.awt.geom.AffineTransform old = g2d.getTransform();
        if (hovered) {
            int cx = r.x + r.width  / 2;
            int cy = r.y + r.height / 2;
            g2d.translate(cx, cy - LIFT);
            g2d.scale(SCALE, SCALE);
            g2d.translate(-cx, -cy);
        }
        if (icon != null) {
            g2d.drawImage(icon, r.x, r.y, r.width, r.height, null);
        } else {
            g2d.setColor(bg);
            g2d.fillRoundRect(r.x, r.y, r.width, r.height, 6, 6);
            g2d.setColor(new Color(200,200,255));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(r.x, r.y, r.width, r.height, 6, 6);
            g2d.setStroke(new BasicStroke(1));
            g2d.setFont(new Font("Monospaced", Font.BOLD, Math.max(8, r.height / 2)));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.setColor(Color.WHITE);
            g2d.drawString(fallback,
                    r.x + (r.width  - fm.stringWidth(fallback)) / 2,
                    r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2);
        }
        g2d.setTransform(old);
    }

    /** Call from mouseMoved in each screen to update hover state. Returns true if repaint needed. */
    public static boolean handleHover(int mx, int my) {
        boolean prevS = hoveredSettings, prevL = hoveredLeaderboard;
        hoveredSettings    = settingsBtn    != null && settingsBtn.contains(mx, my);
        hoveredLeaderboard = leaderboardBtn != null && leaderboardBtn.contains(mx, my);
        return hoveredSettings != prevS || hoveredLeaderboard != prevL;
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

    /** Reset hover state when switching screens */
    public static void resetHover() {
        hoveredSettings    = false;
        hoveredLeaderboard = false;
    }
}