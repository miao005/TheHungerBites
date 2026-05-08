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

    private static void loadIcons() {
        if (iconsLoaded) return;
        iconsLoaded = true;
        try {
            settingsIcon    = ImageIO.read(NavButtons.class.getResourceAsStream("/resources/buttons/btn_settings.png"));
            leaderboardIcon = ImageIO.read(NavButtons.class.getResourceAsStream("/resources/buttons/btn_leaderboard.png"));
        } catch (Exception ignored) {}
    }

    public static void draw(Graphics2D g2d, int width, int height) {
        loadIcons();

        // ── Customize button size and position here ───────────────
        int btnW = (int)(width  * 0.08);   // button width  (change ratio for wider/narrower)
        int btnH = (int)(height * 0.07);   // button height (change ratio for taller/shorter)
        int pad  = (int)(width  * 0.01);   // gap from right edge and between buttons
        int top  = (int)(height * 0.01);   // gap from top edge
        // ──────────────────────────────────────────────────────────

        settingsBtn    = new Rectangle(width - pad - btnW,            top, btnW, btnH);
        leaderboardBtn = new Rectangle(width - pad - btnW * 2 - pad,  top, btnW, btnH);

        drawBtn(g2d, settingsBtn,    settingsIcon,    "S", new Color(60, 80, 160));
        drawBtn(g2d, leaderboardBtn, leaderboardIcon, "L", new Color(100, 60, 140));
    }

    private static void drawBtn(Graphics2D g2d, Rectangle r, BufferedImage icon,
                                String fallback, Color bg) {
        if (icon != null) {
            g2d.drawImage(icon, r.x, r.y, r.width, r.height, null);
        } else {
            g2d.setColor(bg);
            g2d.fillRoundRect(r.x, r.y, r.width, r.height, 6, 6);
            g2d.setColor(new Color(200, 200, 255));
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
    }

    public static boolean handleClick(int mx, int my, GamePanel gamePanel) {
        if (settingsBtn != null && settingsBtn.contains(mx, my)) {
            gamePanel.goToOverlay(GameState.SETTINGS);
            return true;
        }
        if (leaderboardBtn != null && leaderboardBtn.contains(mx, my)) {
            gamePanel.goToOverlay(GameState.LEADERBOARD);
            return true;
        }
        return false;
    }
}