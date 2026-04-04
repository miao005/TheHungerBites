package ui;

import main.Character;
import main.CharacterManager;
import main.GamePanel;
import main.GameState;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CharacterSelectScreen {
    private GamePanel gamePanel;
    private CharacterManager characterManager;
    private int p1Index = 0;
    private int p2Index = 1;
    private boolean selectingP1 = true;
    private Map<String, BufferedImage> sprites = new HashMap<>();
    private Rectangle leftArrow, rightArrow, selectBtn, returnBtn;

    public CharacterSelectScreen(GamePanel gamePanel, CharacterManager characterManager) {
        this.gamePanel = gamePanel;
        this.characterManager = characterManager;
        loadSprites();
    }

    private void loadSprites() {
        String[] names = {"Jollibee", "RonaldMcDonald", "BurgerKing", "ColonelSanders",
                "TacoBell", "Wendys", "Poco", "Julies"};
        for (String name : names) {
            try {
                BufferedImage img = ImageIO.read(getClass().getResourceAsStream("/resources/sprites/" + name + ".png"));
                if (img != null) sprites.put(name, img);
            } catch (Exception ignored) {}
        }
    }

    public void reset() {
        p1Index = 0;
        p2Index = 1;
        selectingP1 = true;
    }


    public void draw(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String mode = gamePanel.getGameMode();
        boolean isPvp = mode.equals("PVP");
        int currentIndex = selectingP1 ? p1Index : p2Index;
        Character currentChar = characterManager.createCharacter(currentIndex);
        String playerLabel = selectingP1 ? "PLAYER 1" : (isPvp ? "PLAYER 2" : "PLAYER 1");

        // Background
        g2d.setColor(new Color(10, 10, 60));
        g2d.fillRect(0, 0, width, height);

        // Player label
        g2d.setFont(new Font("Monospaced", Font.BOLD, scaleFont(width, 28)));
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(playerLabel, (width - fm.stringWidth(playerLabel)) / 2, (int)(height * 0.09));

        // Main card bounds
        int cardX = (int)(width * 0.12);
        int cardW = (int)(width * 0.76);
        int cardY = (int)(height * 0.11);
        int cardH = (int)(height * 0.73);

        // Card border
        g2d.setColor(new Color(20, 20, 80));
        g2d.fillRoundRect(cardX, cardY, cardW, cardH, 12, 12);
        g2d.setColor(new Color(220, 80, 160));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(cardX, cardY, cardW, cardH, 12, 12);
        g2d.setStroke(new BasicStroke(1));

        // Header
        int headerH = (int)(cardH * 0.22);
        g2d.setColor(new Color(230, 100, 170));
        g2d.fillRect(cardX, cardY, cardW, headerH);

        // Sprite
        String spriteKey = currentChar.getName().replaceAll("[^a-zA-Z]", "");
        BufferedImage sprite = sprites.get(spriteKey);
        int spriteSize = (int)(headerH * 0.85);
        int spriteX = cardX + (int)(cardW * 0.04);
        int spriteY = cardY + (headerH - spriteSize) / 2;
        if (sprite != null) {
            g2d.drawImage(sprite, spriteX, spriteY, spriteSize, spriteSize, null);
        } else {
            g2d.setColor(new Color(255, 200, 100));
            g2d.fillOval(spriteX, spriteY, spriteSize, spriteSize);
            g2d.setColor(new Color(180, 80, 40));
            g2d.setFont(new Font("Monospaced", Font.BOLD, scaleFont(width, 10)));
            g2d.drawString("?", spriteX + spriteSize/2 - 4, spriteY + spriteSize/2 + 4);
        }

        // Character name
        g2d.setFont(new Font("Monospaced", Font.BOLD, scaleFont(width, 26)));
        g2d.setColor(Color.WHITE);
        fm = g2d.getFontMetrics();
        String charName = currentChar.getName().toUpperCase();
        int nameX = spriteX + spriteSize + (int)(cardW * 0.04);
        int nameY = cardY + headerH / 2 + fm.getAscent() / 2;
        g2d.drawString(charName, nameX, nameY);

        // Two-panel body
        int bodyY = cardY + headerH + 2;
        int bodyH = cardH - headerH - 2;
        int halfW = cardW / 2;

        g2d.setColor(new Color(240, 230, 245));
        g2d.fillRect(cardX, bodyY, halfW, bodyH);
        g2d.fillRect(cardX + halfW, bodyY, halfW, bodyH);

        g2d.setColor(new Color(180, 80, 160));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(cardX + halfW, bodyY, cardX + halfW, bodyY + bodyH);
        g2d.setStroke(new BasicStroke(1));

        // LORE title
        int panelPad = (int)(cardW * 0.04);
        g2d.setFont(new Font("Monospaced", Font.BOLD, scaleFont(width, 18)));
        g2d.setColor(new Color(220, 60, 140));
        String loreTitle = "LORE";
        fm = g2d.getFontMetrics();
        g2d.drawString(loreTitle, cardX + (halfW - fm.stringWidth(loreTitle)) / 2, bodyY + (int)(bodyH * 0.1));

        // SKILLS title
        String skillsTitle = "SKILLS";
        g2d.drawString(skillsTitle, cardX + halfW + (halfW - fm.stringWidth(skillsTitle)) / 2, bodyY + (int)(bodyH * 0.1));

        // Lore text
        g2d.setFont(new Font("Monospaced", Font.PLAIN, scaleFont(width, 11)));
        g2d.setColor(new Color(30, 20, 60));
        drawWrappedText(g2d, currentChar.getBackstory(),
                cardX + panelPad, bodyY + (int)(bodyH * 0.16),
                halfW - panelPad * 2, (int)(bodyH * 0.78));

        // Skills text
        int skillsX = cardX + halfW + panelPad;
        int skillsY = bodyY + (int)(bodyH * 0.16);
        int lineH = (int)(bodyH * 0.13);
        g2d.setColor(new Color(30, 20, 60));
        g2d.setFont(new Font("Monospaced", Font.PLAIN, scaleFont(width, 11)));

        drawSkillEntry(g2d, "BASIC ATTACK: " + currentChar.getBasicAttackName(),
                "DAMAGE: " + getBasicDmgRange(currentIndex) + "\nMANA COST: 0",
                skillsX, skillsY, halfW - panelPad * 2, lineH * 3, width);

        drawSkillEntry(g2d, "SKILL: " + currentChar.getSkillAttackName(),
                "DAMAGE: " + getSkillDmgRange(currentIndex) + "\nMANA COST: 30",
                skillsX, skillsY + (int)(bodyH * 0.30), halfW - panelPad * 2, lineH * 3, width);

        drawSkillEntry(g2d, "ULTIMATE: " + currentChar.getUltimateAttackName(),
                "DAMAGE: " + getUltiDmgRange(currentIndex) + "\nMANA COST: 50",
                skillsX, skillsY + (int)(bodyH * 0.58), halfW - panelPad * 2, lineH * 3, width);

        // HP/Mana stats
        g2d.setFont(new Font("Monospaced", Font.BOLD, scaleFont(width, 10)));
        g2d.setColor(new Color(100, 0, 80));
        g2d.drawString("HP: " + currentChar.getMaxHealth() + "  |  MANA: " + currentChar.getMaxMana(),
                skillsX, bodyY + (int)(bodyH * 0.92));

        // Arrow buttons
        int arrowSize = (int)(height * 0.10);
        int arrowY = (int)(height * 0.40);
        leftArrow = new Rectangle((int)(width * 0.02), arrowY, arrowSize, arrowSize);
        rightArrow = new Rectangle((int)(width * 0.90), arrowY, arrowSize, arrowSize);
        drawArrow(g2d, leftArrow, false);
        drawArrow(g2d, rightArrow, true);

        // Bottom buttons
        int btnH = (int)(height * 0.09);
        int btnY = (int)(height * 0.87);
        returnBtn = new Rectangle((int)(width * 0.04), btnY, (int)(width * 0.18), btnH);
        selectBtn = new Rectangle((int)(width * 0.35), btnY, (int)(width * 0.30), btnH);
        drawPillButton(g2d, returnBtn, "RETURN", new Color(220, 80, 160), Color.WHITE, width);
        drawPillButton(g2d, selectBtn, "SELECT", new Color(220, 80, 160), Color.WHITE, width);

        // Dot indicators
        int dotCount = characterManager.getRosterSize();
        int dotSize = Math.max(6, width / 80);
        int dotSpacing = dotSize + 4;
        int dotsW = dotCount * dotSpacing;
        int dotStartX = (width - dotsW) / 2;
        int dotY = (int)(height * 0.84);
        for (int i = 0; i < dotCount; i++) {
            g2d.setColor(i == currentIndex ? new Color(220, 80, 160) : new Color(80, 60, 120));
            g2d.fillOval(dotStartX + i * dotSpacing, dotY, dotSize, dotSize);
        }

        // Mode hint
        g2d.setFont(new Font("Monospaced", Font.PLAIN, scaleFont(width, 10)));
        g2d.setColor(new Color(160, 140, 200));
        String hint = "Mode: " + mode + (isPvp && !selectingP1 ? "  [Selecting Player 2]" : "");
        g2d.drawString(hint, (int)(width * 0.04), (int)(height * 0.975));
    }

    private void drawSkillEntry(Graphics2D g2d, String title, String details, int x, int y, int maxW, int h, int screenW) {
        g2d.setFont(new Font("Monospaced", Font.BOLD, scaleFont(screenW, 10)));
        g2d.setColor(new Color(60, 0, 80));
        drawWrappedText(g2d, title, x, y, maxW, g2d.getFontMetrics().getHeight() * 2);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, scaleFont(screenW, 10)));
        g2d.setColor(new Color(60, 40, 80));
        String[] lines = details.split("\n");
        int lh = g2d.getFontMetrics().getHeight();
        for (int i = 0; i < lines.length; i++) {
            g2d.drawString(lines[i], x, y + lh * 2 + lh * i);
        }
    }

    private void drawArrow(Graphics2D g2d, Rectangle bounds, boolean pointRight) {
        g2d.setColor(new Color(220, 80, 160));
        int cx = bounds.x + bounds.width / 2;
        int cy = bounds.y + bounds.height / 2;
        int r = bounds.width / 2 - 4;
        int[] xs, ys;
        if (pointRight) {
            xs = new int[]{cx - r, cx + r, cx - r};
            ys = new int[]{cy - r, cy, cy + r};
        } else {
            xs = new int[]{cx + r, cx - r, cx + r};
            ys = new int[]{cy - r, cy, cy + r};
        }
        g2d.fillPolygon(xs, ys, 3);
    }

    private void drawPillButton(Graphics2D g2d, Rectangle bounds, String text, Color bg, Color fg, int screenW) {
        g2d.setColor(bg);
        g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 30, 30);
        g2d.setColor(fg);
        g2d.setFont(new Font("Monospaced", Font.BOLD, scaleFont(screenW, 16)));
        FontMetrics fm = g2d.getFontMetrics();
        int tx = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int ty = bounds.y + (bounds.height + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(text, tx, ty);
    }

    private void drawWrappedText(Graphics2D g2d, String text, int x, int y, int maxWidth, int maxHeight) {
        if (text == null) return;
        FontMetrics fm = g2d.getFontMetrics();
        int lineH = fm.getHeight();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int curY = y + lineH;
        for (String word : words) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(test) > maxWidth) {
                if (curY - y + lineH > maxHeight) break;
                g2d.drawString(line.toString(), x, curY);
                curY += lineH;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0 && curY - y <= maxHeight) {
            g2d.drawString(line.toString(), x, curY);
        }
    }

    private int scaleFont(int width, int base) {
        return Math.max(8, (int)(base * width / 640.0));
    }

    private String getBasicDmgRange(int idx) {
        int[][] ranges = {{16,22},{17,23},{22,28},{18,22},{20,26},{18,26},{15,20},{19,25}};
        return idx < ranges.length ? ranges[idx][0] + " - " + ranges[idx][1] : "?";
    }

    private String getSkillDmgRange(int idx) {
        int[][] ranges = {{24,32},{26,34},{30,40},{25,32},{30,38},{25,33},{22,28},{28,36}};
        return idx < ranges.length ? ranges[idx][0] + " - " + ranges[idx][1] : "?";
    }

    private String getUltiDmgRange(int idx) {
        int[][] ranges = {{38,48},{41,51},{45,55},{40,50},{45,58},{40,52},{35,45},{43,55}};
        return idx < ranges.length ? ranges[idx][0] + " - " + ranges[idx][1] : "?";
    }



    public void mouseClicked(int mx, int my) {
        String mode = gamePanel.getGameMode();
        boolean isPvp = mode.equals("PVP");

        if (leftArrow != null && leftArrow.contains(mx, my)) {
            if (selectingP1) {
                p1Index = (p1Index - 1 + characterManager.getRosterSize()) % characterManager.getRosterSize();
            } else {
                p2Index = (p2Index - 1 + characterManager.getRosterSize()) % characterManager.getRosterSize();
            }
            gamePanel.repaint();
        } else if (rightArrow != null && rightArrow.contains(mx, my)) {
            if (selectingP1) {
                p1Index = (p1Index + 1) % characterManager.getRosterSize();
            } else {
                p2Index = (p2Index + 1) % characterManager.getRosterSize();
            }
            gamePanel.repaint();
        } else if (selectBtn != null && selectBtn.contains(mx, my)) {
            if (isPvp) {
                if (selectingP1) {
                    selectingP1 = false;
                    gamePanel.repaint();
                } else {
                    gamePanel.startPvpBattle(p1Index, p2Index);
                }
            } else if (mode.equals("PVAI")) {
                gamePanel.startPvAiBattle(p1Index);
            } else if (mode.equals("ARCADE")) {
                gamePanel.startArcade(p1Index);
            }
        } else if (returnBtn != null && returnBtn.contains(mx, my)) {
            if (!selectingP1 && isPvp) {
                selectingP1 = true;
                gamePanel.repaint();
            } else {
                reset();
                gamePanel.setGameState(GameState.MENU);
            }
        }
    }

    public void mouseMoved(int mouseX, int mouseY) {
        gamePanel.repaint();
    }
}