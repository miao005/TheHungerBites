package ui;

import main.BattleSystem;
import main.Character;
import main.GamePanel;
import main.MatchManager;
import main.GameState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleScreen {

    private static final float BTN_SCALE = 1.08f;

    private boolean showRoundStart  = false;
    private long    roundStartTime  = 0;
    private int     currentRound    = 1;
    private static final long ROUND_INDICATOR_MS = 2000;

    private GamePanel gamePanel;
    private BattleSystem battleSystem;

    private BufferedImage currentMap;
    private Map<Integer, BufferedImage> roundBanners = new HashMap<>();
    private Map<String, BufferedImage> mapImages  = new HashMap<>();
    private BufferedImage battleBottomBg;
    private BufferedImage btnBasicImg, btnSkillImg, btnUltimateImg, btnRestImg;
    private Map<String, BufferedImage> sprites     = new HashMap<>();
    private Map<String, BufferedImage> bossSprites = new HashMap<>();
    private Map<String, URL>           gifUrls     = new HashMap<>();
    private Map<String, ImageIcon>     idleIcons   = new HashMap<>();
    private Font pixelFont;
    private Font minecraftFont;

    private ImageIcon attackerIcon;
    private ImageIcon defenderIcon;
    private int attackerAnimX, attackerAnimY, attackerAnimW, attackerAnimH;
    private int defenderAnimX, defenderAnimY, defenderAnimW, defenderAnimH;
    private boolean attackerFlip, defenderFlip;

    private Character hitFlashTarget = null;
    private long hitFlashStartMs     = 0;
    private static final int HIT_FLASH_DURATION_MS = 600;
    private boolean animationPlaying = false;

    private Character player1, player2;
    private boolean isAiMode     = false;
    private boolean isArcadeMode = false;
    private int arcadeRound      = 1;
    private int arcadeMaxRounds  = 5;
    private boolean playerTurn   = true;
    private boolean waitingForInput = true;
    private boolean battleOver   = false;

    private List<String> battleLog = new ArrayList<>();

    private Rectangle btnBasic, btnSkill, btnUltimate, btnRest;
    private int hoveredBtn = -1;

    private String flashMessage = "";
    private long   flashTime    = 0;
    private static final long FLASH_MS = 2000;

    private static final int ANIM_DURATION_MS = 1500;

    private int hpBarH, battleAreaY, battleH, bottomY, bottomH;

    private Rectangle battleSettingsBtn, battleLeaderboardBtn;

    public BattleScreen(GamePanel gamePanel) {
        this.gamePanel    = gamePanel;
        this.battleSystem = new BattleSystem();
        loadFont();
        loadAssets();
        setupAnimationLabels();
    }

    public void triggerRoundStart(int round) {
        this.currentRound    = round;
        this.showRoundStart  = true;
        this.roundStartTime  = System.currentTimeMillis();
        this.waitingForInput = false;
        gamePanel.getAudioManager().playSFX("/resources/Music/round" + round + ".wav");
    }

    private void drawRoundStartIndicator(Graphics2D g2d, int width, int height) {
        if (!showRoundStart) return;
        long elapsed = System.currentTimeMillis() - roundStartTime;
        if (elapsed >= ROUND_INDICATOR_MS) {
            showRoundStart  = false;
            waitingForInput = true;
            gamePanel.repaint();
            return;
        }
        float alpha;
        if (elapsed < 300)                         alpha = elapsed / 300f;
        else if (elapsed > ROUND_INDICATOR_MS-400) alpha = (ROUND_INDICATOR_MS - elapsed) / 400f;
        else                                        alpha = 1f;
        alpha = Math.max(0f, Math.min(1f, alpha));

        float scale = elapsed < 400 ? 1f + (1f - elapsed / 400f) * 0.4f : 1f;

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.55f));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int cx = width / 2, cy = height / 2;
        java.awt.geom.AffineTransform old = g2d.getTransform();
        g2d.translate(cx, cy); g2d.scale(scale, scale); g2d.translate(-cx, -cy);

        Font roundFont = minecraftFont.deriveFont((float) sf(width, 32));
        g2d.setFont(roundFont);
        FontMetrics rfm = g2d.getFontMetrics();
        String roundText = "ROUND  " + currentRound;
        int rx = cx - rfm.stringWidth(roundText) / 2;
        int ry = cy - (int)(height * 0.04);
        g2d.setColor(new Color(0,0,0,180)); g2d.drawString(roundText, rx+3, ry+3);
        g2d.setColor(new Color(255,215,0)); g2d.drawString(roundText, rx, ry);

        int lineY = ry + (int)(height * 0.025);
        g2d.setColor(new Color(255,215,0,180)); g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(cx - rfm.stringWidth(roundText)/2, lineY, cx + rfm.stringWidth(roundText)/2, lineY);
        g2d.setStroke(new BasicStroke(1));

        Font fightFont = minecraftFont.deriveFont((float) sf(width, 22));
        g2d.setFont(fightFont);
        FontMetrics ffm = g2d.getFontMetrics();
        String fightText = elapsed > 600 ? "FIGHT!" : "";
        if (!fightText.isEmpty()) {
            float fa = Math.min(1f, (elapsed-600)/200f);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(alpha,fa)));
            int fx = cx - ffm.stringWidth(fightText)/2;
            int fy = ry + (int)(height*0.08);
            g2d.setColor(new Color(0,0,0,180)); g2d.drawString(fightText, fx+2, fy+2);
            g2d.setColor(new Color(255,80,80));  g2d.drawString(fightText, fx, fy);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }

        BufferedImage banner = roundBanners.get(currentRound);
        if (banner != null) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.drawImage(banner, 0, 0, width, height, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        g2d.setTransform(old);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        gamePanel.repaint();
    }

    private void loadFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/resources/fonts/ARCADECLASSIC.TTF");
            if (is != null) {
                pixelFont = new Font("Monospaced", Font.PLAIN, 12);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(pixelFont);
            }
        } catch (Exception ignored) {}
        if (pixelFont == null) pixelFont = new Font("Monospaced", Font.BOLD, 12);
        // Minecraft font
        try {
            java.io.InputStream mis = getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf");
            if (mis != null) {
                minecraftFont = Font.createFont(Font.TRUETYPE_FONT, mis).deriveFont(Font.PLAIN, 12f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(minecraftFont);
            }
        } catch (Exception ignored) {}
        if (minecraftFont == null) minecraftFont = new Font("Monospaced", Font.BOLD, 12);
    }

    private void loadAssets() {
        mapImages.put("Jollibee",       tryLoadImage("/resources/Maps/jollibee_map.png"));
        mapImages.put("RonaldMcDonald", tryLoadImage("/resources/Maps/mcdonalds_map.png"));
        mapImages.put("BurgerKing",     tryLoadImage("/resources/Maps/burgerking_map.png"));
        mapImages.put("Poco",           tryLoadImage("/resources/potatocorner_map.png"));
        mapImages.put("Julies",         tryLoadImage("/resources/julies_map.png"));

        battleBottomBg = tryLoadImage("/resources/battle_bottom_bg.png", "/resources/bottom_panel.png");
        btnBasicImg    = tryLoadImage("/resources/btn_basic.png");
        btnSkillImg    = tryLoadImage("/resources/btn_skill.png");
        btnUltimateImg = tryLoadImage("/resources/btn_ultimate.png");
        btnRestImg     = tryLoadImage("/resources/btn_rest.png");

        roundBanners.put(1, tryLoadImage("/resources/rounds/round1.png"));
        roundBanners.put(2, tryLoadImage("/resources/rounds/round2.png"));
        roundBanners.put(3, tryLoadImage("/resources/rounds/round3.png"));

        String[] charNames = {"Jollibee","RonaldMcDonald","BurgerKing","ColonelSanders",
                "TacoBell","Wendys","Poco","Julies"};
        for (String n : charNames) {
            BufferedImage img = tryLoadImage("/resources/sprites/" + n + ".png");
            if (img != null) sprites.put(n, img);
            BufferedImage bossImg = tryLoadImage("/resources/sprites/" + n + "/" + n + "_boss.png");
            if (bossImg != null) bossSprites.put(n, bossImg);
        }

        // ── FIX: added "_rest" to the suffix list so rest GIFs are preloaded ──
        String[] suffixes = {"_idle","_basic","_skill","_ultimate","_hit","_rest"};
        for (String n : charNames) {
            for (String s : suffixes) {
                URL url = getClass().getResource("/resources/sprites/" + n + "/" + n + s + ".gif");
                if (url != null) gifUrls.put(n + s, url);
            }
            URL idleUrl = gifUrls.get(n + "_idle");
            if (idleUrl != null) {
                ImageIcon icon = new ImageIcon(idleUrl);
                icon.setImageObserver(gamePanel);
                idleIcons.put(n, icon);
            }
        }
    }

    private BufferedImage tryLoadImage(String... paths) {
        for (String p : paths) {
            try {
                BufferedImage img = ImageIO.read(getClass().getResourceAsStream(p));
                if (img != null) return img;
            } catch (Exception ignored) {}
        }
        return null;
    }

    private void setupAnimationLabels() { attackerIcon = null; defenderIcon = null; }

    public void startBattle(Character p1, Character p2,
                            boolean aiMode, boolean arcadeMode, int round, int maxRounds) {
        this.player1 = p1; this.player2 = p2;
        this.isAiMode = aiMode; this.isArcadeMode = arcadeMode;
        this.arcadeRound = round; this.arcadeMaxRounds = maxRounds;
        this.playerTurn = true; this.waitingForInput = true;
        this.battleOver = false; this.animationPlaying = false;
        this.flashMessage = "";
        this.battleLog.clear();
        hideAnimations();
        selectMap(p2, arcadeMode);
        addLog("What will " + p1.getName() + " do?");
        gamePanel.repaint();
    }

    public void draw(Graphics g, int width, int height) {
        if (player1 == null || player2 == null) return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        hpBarH = (int)(height*0.18); bottomH = (int)(height*0.30);
        battleH = height - hpBarH - bottomH; battleAreaY = hpBarH; bottomY = hpBarH + battleH;

        drawBattleBackground(g2d, width, battleH);

        if (!animationPlaying) {
            drawSprites(g2d, width);
        } else {
            if (attackerIcon != null)
                drawGif(g2d, attackerIcon, attackerAnimX, attackerAnimY, attackerAnimW, attackerAnimH, attackerFlip);
            if (defenderIcon != null) {
                drawGif(g2d, defenderIcon, defenderAnimX, defenderAnimY, defenderAnimW, defenderAnimH, defenderFlip);
                if (hitFlashTarget != null && hitFlashStartMs > 0) {
                    long elapsed = System.currentTimeMillis() - hitFlashStartMs;
                    if (elapsed < HIT_FLASH_DURATION_MS) {
                        float progress = (float) elapsed / HIT_FLASH_DURATION_MS;
                        int a = (int)(180 * (1f - progress));
                        g2d.setColor(new Color(255,0,0,a));
                        g2d.fillRect(defenderAnimX, defenderAnimY, defenderAnimW, defenderAnimH);
                        g2d.setColor(new Color(255,0,0,Math.min(255,a+60)));
                        g2d.setStroke(new BasicStroke(4));
                        g2d.drawRect(defenderAnimX, defenderAnimY, defenderAnimW, defenderAnimH);
                        g2d.setStroke(new BasicStroke(1));
                    }
                }
            }
        }

        drawHpBars(g2d, width);
        drawRoundLabel(g2d, width);
        drawBottomPanel(g2d, width, height);
        drawFlash(g2d, width, height);
        drawRoundStartIndicator(g2d, width, height);
    }

    private void selectMap(Character enemy, boolean arcadeMode) {
        if (arcadeMode) {
            currentMap = mapImages.get(getSpriteKey(enemy));
            if (currentMap == null) currentMap = getRandomMap();
        } else {
            currentMap = getRandomMap();
        }
    }

    private BufferedImage getRandomMap() {
        List<BufferedImage> available = new ArrayList<>();
        for (BufferedImage img : mapImages.values()) if (img != null) available.add(img);
        if (available.isEmpty()) return null;
        return available.get((int)(Math.random() * available.size()));
    }

    private void drawBattleBackground(Graphics2D g2d, int width, int h) {
        if (currentMap != null) {
            g2d.drawImage(currentMap, 0, battleAreaY, width, h, null);
        } else {
            GradientPaint sky = new GradientPaint(0, battleAreaY, new Color(100,160,230),
                    0, battleAreaY + h*2/3, new Color(160,210,255));
            g2d.setPaint(sky); g2d.fillRect(0, battleAreaY, width, h);
            g2d.setColor(new Color(80,60,40));
            g2d.fillRect(0, battleAreaY+(int)(h*0.65), width, (int)(h*0.35));
        }
    }

    private void drawSprites(Graphics2D g2d, int width) {
        int spriteH = (int)(battleH * 0.85);
        int spriteW = spriteH;
        int groundY = battleAreaY + (int)(battleH * 1.00);
        drawIdleSprite(g2d, player1, (int)(width*0.08), groundY-spriteH, spriteW, spriteH, false);
        drawIdleSprite(g2d, player2, (int)(width*0.64), groundY-spriteH, spriteW, spriteH, true);
    }

    private void drawIdleSprite(Graphics2D g2d, Character ch, int x, int y, int w, int h, boolean flip) {
        String key = getSpriteKey(ch);
        ImageIcon idleIcon = idleIcons.get(key);
        if (idleIcon != null) {
            drawGif(g2d, idleIcon, x, y, w, h, flip);
        } else {
            BufferedImage sprite = sprites.get(key);
            if (sprite != null) {
                if (flip) g2d.drawImage(sprite, x+w, y, x, y+h, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
                else      g2d.drawImage(sprite, x, y, w, h, null);
            } else {
                g2d.setColor(flip ? new Color(200,80,80,180) : new Color(80,120,200,180));
                g2d.fillRoundRect(x+w/4, y+h/6, w/2, h*5/6, 12, 12);
                g2d.fillOval(x+w/3, y, w/3, w/3);
            }
        }
    }

    private void drawGif(Graphics2D g2d, ImageIcon icon, int x, int y, int w, int h, boolean flip) {
        if (flip) {
            java.awt.geom.AffineTransform old = g2d.getTransform();
            g2d.translate(x+w, y); g2d.scale(-1,1);
            g2d.drawImage(icon.getImage(), 0, 0, w, h, gamePanel);
            g2d.setTransform(old);
        } else {
            g2d.drawImage(icon.getImage(), x, y, w, h, gamePanel);
        }
    }

    private void drawHpBars(Graphics2D g2d, int width) {
        g2d.setColor(new Color(0,0,0,180)); g2d.fillRect(0, 0, width, hpBarH);
        g2d.setColor(new Color(180,140,0)); g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(0, hpBarH, width, hpBarH); g2d.setStroke(new BasicStroke(1));

        int portraitSize = (int)(hpBarH*0.62);
        int padY = (hpBarH - portraitSize) / 2;
        int barH = (int)(portraitSize*0.28);
        int centreZone  = (int)(width*0.18);
        int centreLeft  = width/2 - centreZone/2;
        int centreRight = width/2 + centreZone/2;
        int pad = (int)(width*0.01);

        drawPortrait(g2d, player1, pad, padY, portraitSize, false);
        drawHpBarBlock(g2d, player1, pad+portraitSize+pad,
                padY+(int)(portraitSize*0.25), padY+(int)(portraitSize*0.45), padY+(int)(portraitSize*0.72),
                centreLeft-(pad+portraitSize+pad)-pad, barH, width);

        int p2PortX = width - pad - portraitSize;
        drawPortrait(g2d, player2, p2PortX, padY, portraitSize, true);
        drawHpBarBlock(g2d, player2, centreRight+pad,
                padY+(int)(portraitSize*0.25), padY+(int)(portraitSize*0.45), padY+(int)(portraitSize*0.72),
                p2PortX-pad-centreRight-pad, barH, width);

        NavButtons.drawBattle(g2d, width, hpBarH, isArcadeMode);
        if (!isArcadeMode) drawWinPips(g2d, width, hpBarH);
        battleSettingsBtn = null; battleLeaderboardBtn = null;
    }

    private void drawWinPips(Graphics2D g2d, int width, int hpH) {
        MatchManager mm = gamePanel.getMatchManager();
        int p1Wins = mm.getP1Wins(), p2Wins = mm.getP2Wins(), needed = MatchManager.ROUNDS_TO_WIN;
        int pipR = Math.max(4, (int)(hpH*0.09)), pipDiam = pipR*2, gap = (int)(pipR*0.9);
        int cx = width/2, cy = (int)(hpH*0.50);
        for (int i = 0; i < needed; i++) {
            int px = cx - gap - pipDiam - i*(pipDiam+gap), py = cy - pipR;
            if (i < p1Wins) { g2d.setColor(new Color(80,220,80)); g2d.fillOval(px,py,pipDiam,pipDiam); }
            else { g2d.setColor(new Color(80,220,80,80)); g2d.setStroke(new BasicStroke(1.5f)); g2d.drawOval(px,py,pipDiam,pipDiam); g2d.setStroke(new BasicStroke(1)); }
        }
        for (int i = 0; i < needed; i++) {
            int px = cx + gap + i*(pipDiam+gap), py = cy - pipR;
            if (i < p2Wins) { g2d.setColor(new Color(220,80,80)); g2d.fillOval(px,py,pipDiam,pipDiam); }
            else { g2d.setColor(new Color(220,80,80,80)); g2d.setStroke(new BasicStroke(1.5f)); g2d.drawOval(px,py,pipDiam,pipDiam); g2d.setStroke(new BasicStroke(1)); }
        }
    }

    private void drawPortrait(Graphics2D g2d, Character ch, int x, int y, int size, boolean flip) {
        g2d.setColor(new Color(180,140,0)); g2d.setStroke(new BasicStroke(3));
        int[] px = {x+size/2,x+size,x+size/2,x}, py = {y,y+size/2,y+size,y+size/2};
        g2d.fillPolygon(px,py,4); g2d.setColor(new Color(255,215,0)); g2d.drawPolygon(px,py,4);
        g2d.setStroke(new BasicStroke(1));
        Shape oldClip = g2d.getClip();
        g2d.setClip(new java.awt.geom.Ellipse2D.Float(x+size*0.15f,y+size*0.15f,size*0.70f,size*0.70f));
        String key = getSpriteKey(ch);
        int ix = x+(int)(size*0.15), iy = y+(int)(size*0.15), is = (int)(size*0.70);
        BufferedImage portrait = (isArcadeMode && !ch.isPlayer() && bossSprites.containsKey(key))
                ? bossSprites.get(key) : sprites.get(key);
        if (portrait != null) {
            if (flip) g2d.drawImage(portrait, ix+is, iy, ix, iy+is, 0,0,portrait.getWidth(),portrait.getHeight(),null);
            else      g2d.drawImage(portrait, ix, iy, is, is, null);
        } else { g2d.setColor(new Color(100,80,160)); g2d.fillRect(ix,iy,is,is); }
        g2d.setClip(oldClip);
    }

    private void drawHpBarBlock(Graphics2D g2d, Character ch,
                                int x, int nameY, int hpY, int mpY, int barW, int barH, int screenW) {
        g2d.setFont(minecraftFont.deriveFont((float) sf(screenW,10))); g2d.setColor(Color.WHITE);
        g2d.drawString(ch.getName(), x, nameY);
        drawBar(g2d,x,hpY,barW,barH,ch.getHealth(),ch.getMaxHealth(),new Color(240,60,60),new Color(80,200,80),"HP");
        drawBar(g2d,x,mpY,barW,barH,ch.getCurrentMana(),ch.getMaxMana(),new Color(40,60,160),new Color(80,140,255),"MP");
    }

    private void drawBar(Graphics2D g2d, int x, int y, int w, int h,
                         int cur, int max, Color empty, Color fill, String label) {
        float ratio = max > 0 ? (float)cur/max : 0f;
        g2d.setColor(empty); g2d.fillRoundRect(x,y,w,h,4,4);
        Color fc = fill;
        if (label.equals("HP")) { if (ratio<0.25f) fc=new Color(220,60,60); else if(ratio<0.50f) fc=new Color(220,180,60); }
        g2d.setColor(fc); g2d.fillRoundRect(x,y,(int)(w*ratio),h,4,4);
        g2d.setColor(new Color(255,255,255,60)); g2d.fillRoundRect(x,y,(int)(w*ratio),h/2,4,4);
        g2d.setColor(new Color(0,0,0,120)); g2d.drawRoundRect(x,y,w,h,4,4);
        g2d.setFont(minecraftFont.deriveFont((float)Math.max(6,h-1))); g2d.setColor(Color.WHITE);
        g2d.drawString(label+" "+cur+"/"+max, x+3, y+h-1);
    }

    private void drawRoundLabel(Graphics2D g2d, int width) {
        int round = isArcadeMode ? arcadeRound : gamePanel.getMatchManager().getCurrentRound();
        String txt = "ROUND " + round;
        g2d.setFont(minecraftFont.deriveFont((float) sf(width,14)));
        FontMetrics fm = g2d.getFontMetrics();
        int rx = (width - fm.stringWidth(txt))/2, ry = (int)(hpBarH*0.25);
        g2d.setColor(new Color(0,0,0,150)); g2d.drawString(txt, rx+2, ry+2);
        g2d.setColor(new Color(255,215,0));  g2d.drawString(txt, rx, ry);
    }

    private void drawBottomPanel(Graphics2D g2d, int width, int height) {
        if (battleBottomBg != null) g2d.drawImage(battleBottomBg,0,bottomY,width,bottomH,null);
        else {
            g2d.setColor(new Color(30,25,60)); g2d.fillRect(0,bottomY,width,bottomH);
            g2d.setColor(new Color(180,140,0)); g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(0,bottomY,width,bottomY); g2d.setStroke(new BasicStroke(1));
        }
        int logW = (int)(width*0.44), btnAreaX = logW+(int)(width*0.02);
        int btnAreaW = width-btnAreaX-(int)(width*0.02), pad = (int)(bottomH*0.06);
        drawLogBox(g2d,(int)(width*0.01),bottomY+pad,logW,bottomH-pad*2,width);

        boolean canAct = !battleOver && waitingForInput && !animationPlaying
                && (playerTurn || (!isAiMode && !isArcadeMode));
        if (canAct) {
            drawAttackButtons(g2d, btnAreaX, bottomY+pad, btnAreaW, bottomH-pad*2, width);
        } else if (!battleOver) {
            g2d.setFont(minecraftFont.deriveFont((float) sf(width,10)));
            g2d.setColor(new Color(180,160,220));
            String msg = animationPlaying ? "..." :
                    (!playerTurn && (isAiMode||isArcadeMode)) ? player2.getName()+" is thinking..." : "";
            if (!msg.isEmpty()) g2d.drawString(msg, btnAreaX+8, bottomY+bottomH/2);
        }
    }

    private void drawLogBox(Graphics2D g2d, int x, int y, int w, int h, int screenW) {
        g2d.setColor(Color.WHITE); g2d.fillRoundRect(x,y,w,h,8,8);
        g2d.setColor(new Color(40,30,80)); g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(x,y,w,h,8,8); g2d.setStroke(new BasicStroke(1));
        g2d.setFont(minecraftFont.deriveFont((float) sf(screenW,10))); g2d.setColor(new Color(30,20,50));
        FontMetrics fm = g2d.getFontMetrics();
        int lineH = fm.getHeight(), pad = (int)(h*0.10), maxLines = (h-pad*2)/lineH;
        int start = Math.max(0, battleLog.size()-maxLines);
        for (int i = start; i < battleLog.size(); i++)
            g2d.drawString(battleLog.get(i), x+pad, y+pad+(i-start+1)*lineH);
    }

    private void drawAttackButtons(Graphics2D g2d, int x, int y, int w, int h, int screenW) {
        int gap  = (int)(w*0.04);
        int btnW = (w-gap)/2;
        int btnH = (h-gap)/2;

        Character current = playerTurn ? player1 : player2;
        boolean canSkill = current.getCurrentMana() >= 30;
        boolean canUlti  = current.getCurrentMana() >= 50;

        btnBasic    = new Rectangle(x,            y,            btnW, btnH);
        btnSkill    = new Rectangle(x+btnW+gap,   y,            btnW, btnH);
        btnUltimate = new Rectangle(x,            y+btnH+gap,   btnW, btnH);
        btnRest     = new Rectangle(x+btnW+gap,   y+btnH+gap,   btnW, btnH);

        drawAttackBtn(g2d, btnBasic,    btnBasicImg,    new Color(180,40,40),  new Color(220,70,70),
                current.getBasicAttackName(),    "0 MP",  hoveredBtn==0, true,     screenW);
        drawAttackBtn(g2d, btnSkill,    btnSkillImg,    new Color(40,140,60),  new Color(70,180,90),
                current.getSkillAttackName(),    "30 MP", hoveredBtn==1, canSkill, screenW);
        drawAttackBtn(g2d, btnUltimate, btnUltimateImg, new Color(160,130,20), new Color(200,170,40),
                current.getUltimateAttackName(), "50 MP", hoveredBtn==2, canUlti,  screenW);
        drawAttackBtn(g2d, btnRest,     btnRestImg,     new Color(40,80,180),  new Color(70,120,220),
                "Rest",                          "Heal",  hoveredBtn==3, true,     screenW);
    }

    private void drawAttackBtn(Graphics2D g2d, Rectangle r, BufferedImage img,
                               Color base, Color hover,
                               String name, String cost,
                               boolean isHovered, boolean canUse, int screenW) {
        java.awt.geom.AffineTransform old = g2d.getTransform();

        if (isHovered && canUse) {
            int cx = r.x + r.width  / 2;
            int cy = r.y + r.height / 2;
            g2d.translate(cx, cy);
            g2d.scale(BTN_SCALE, BTN_SCALE);
            g2d.translate(-cx, -cy);
        }

        if (img != null) {
            g2d.drawImage(img, r.x, r.y, r.width, r.height, null);
            if (!canUse) {
                g2d.setColor(new Color(0,0,0,100));
                g2d.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
            }
        } else {
            Color bg = !canUse ? new Color(60,55,70) : (isHovered ? hover : base);
            g2d.setColor(bg); g2d.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
            g2d.setColor(new Color(0,0,0,120)); g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10); g2d.setStroke(new BasicStroke(1));
            g2d.setFont(minecraftFont.deriveFont((float) sf(screenW,10)));
            g2d.setColor(canUse ? Color.WHITE : new Color(140,130,150));
            FontMetrics fm = g2d.getFontMetrics();
            String display = fm.stringWidth(name) > r.width-8 ? name.substring(0,Math.min(name.length(),11))+"…" : name;
            g2d.drawString(display, r.x+(r.width-fm.stringWidth(display))/2, r.y+(int)(r.height*0.42));
            g2d.setFont(minecraftFont.deriveFont((float) sf(screenW,8)));
            g2d.setColor(canUse ? new Color(220,220,180) : new Color(120,110,130));
            fm = g2d.getFontMetrics();
            g2d.drawString(cost, r.x+(r.width-fm.stringWidth(cost))/2, r.y+(int)(r.height*0.78));
        }

        g2d.setTransform(old);
    }

    private void drawFlash(Graphics2D g2d, int width, int height) {
        if (flashMessage.isEmpty()) return;
        long elapsed = System.currentTimeMillis() - flashTime;
        if (elapsed >= FLASH_MS) { flashMessage = ""; return; }
        float alpha = elapsed < FLASH_MS*0.6 ? 1f : 1f-(float)(elapsed-FLASH_MS*0.6)/(float)(FLASH_MS*0.4);
        alpha = Math.max(0f, alpha);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setFont(minecraftFont.deriveFont((float) sf(width,22)));
        FontMetrics fm = g2d.getFontMetrics();
        int fx = (width-fm.stringWidth(flashMessage))/2, fy = (int)(height*0.50);
        g2d.setColor(new Color(0,0,0,(int)(180*alpha))); g2d.drawString(flashMessage,fx+2,fy+2);
        g2d.setColor(new Color(255,215,0,(int)(255*alpha))); g2d.drawString(flashMessage,fx,fy);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
        gamePanel.repaint();
    }

    private void playAnimation(Character attacker, Character defender, int skillChoice) {
        String attackerKey = getSpriteKey(attacker), defenderKey = getSpriteKey(defender);

        // ── FIX: added skillChoice==4 → "_rest" ──
        String attackSuffix = skillChoice==1 ? "_basic"
                : skillChoice==2 ? "_skill"
                : skillChoice==3 ? "_ultimate"
                : skillChoice==4 ? "_rest"
                : null;

        URL attackerGif = attackSuffix!=null ? gifUrls.get(attackerKey+attackSuffix) : null;
        // For rest, show defender's idle GIF so they don't disappear
        URL defenderGif = skillChoice!=4 ? gifUrls.get(defenderKey+"_hit") : gifUrls.get(defenderKey+"_idle");

        int panelW=gamePanel.getWidth(), panelH=gamePanel.getHeight();
        int hpH=(int)(panelH*0.18), botH=(int)(panelH*0.30), batH=panelH-hpH-botH;
        int spriteH=(int)(batH*0.85), spriteW=spriteH, groundY=hpH+(int)(batH*1.00);
        boolean attackerIsP1=(attacker==player1);
        int idleX=attackerIsP1?(int)(panelW*0.08):(int)(panelW*0.62);
        int targetX=attackerIsP1?(int)(panelW*0.45):(int)(panelW*0.20);
        int defenderX=attackerIsP1?(int)(panelW*0.62):(int)(panelW*0.08);
        int spriteY=groundY-spriteH;

        SwingUtilities.invokeLater(() -> {
            animationPlaying = true;
            if (attackerGif!=null) {
                attackerIcon=new ImageIcon(attackerGif); attackerIcon.setImageObserver(gamePanel);
                attackerAnimX=idleX; attackerAnimY=spriteY; attackerAnimW=spriteW; attackerAnimH=spriteH;
                attackerFlip=!attackerIsP1;
            }
            if (defenderGif!=null) {
                defenderIcon=new ImageIcon(defenderGif); defenderIcon.setImageObserver(gamePanel);
                defenderAnimX=defenderX; defenderAnimY=spriteY; defenderAnimW=spriteW; defenderAnimH=spriteH;
                defenderFlip=attackerIsP1;
            }
            gamePanel.repaint();

            // For rest, no slide — just play in place
            if (skillChoice == 4 || attackerGif == null) return;

            int steps=30, slideMs=ANIM_DURATION_MS/2, stepDelay=Math.max(1,slideMs/steps);
            javax.swing.Timer slideForward=new javax.swing.Timer(stepDelay,null);
            final int[] step={0};
            slideForward.addActionListener(e -> {
                step[0]++; float t=(float)step[0]/steps;
                float ease=t<0.5f?2*t*t:-1+(4-2*t)*t;
                attackerAnimX=idleX+(int)((targetX-idleX)*ease); gamePanel.repaint();
                if (step[0]>=steps) {
                    ((javax.swing.Timer)e.getSource()).stop();
                    javax.swing.Timer slideBack=new javax.swing.Timer(stepDelay,null);
                    final int[] step2={0};
                    slideBack.addActionListener(e2 -> {
                        step2[0]++; float t2=(float)step2[0]/steps;
                        float ease2=t2<0.5f?2*t2*t2:-1+(4-2*t2)*t2;
                        attackerAnimX=targetX+(int)((idleX-targetX)*ease2); gamePanel.repaint();
                        if (step2[0]>=steps) ((javax.swing.Timer)e2.getSource()).stop();
                    });
                    slideBack.start();
                }
            });
            slideForward.start();
        });

        new Thread(() -> {
            try { Thread.sleep(ANIM_DURATION_MS); } catch (InterruptedException ignored) {}
            SwingUtilities.invokeLater(this::hideAnimations);
        }).start();
    }

    private void hideAnimations() {
        attackerIcon=null; defenderIcon=null; animationPlaying=false; gamePanel.repaint();
    }

    public void triggerHitFlash(Character target) {
        hitFlashTarget=target; hitFlashStartMs=System.currentTimeMillis();
        javax.swing.Timer flashTimer=new javax.swing.Timer(16,null);
        flashTimer.addActionListener(e -> {
            gamePanel.repaint();
            if (System.currentTimeMillis()-hitFlashStartMs>HIT_FLASH_DURATION_MS) {
                hitFlashTarget=null; ((javax.swing.Timer)e.getSource()).stop();
            }
        });
        flashTimer.start();
    }

    public void mouseClicked(int mx, int my) {
        if (NavButtons.handleClick(mx, my, gamePanel)) return;

        // Easter egg: clicking P1's portrait one-hits the opponent
        int portraitSize = (int)(hpBarH * 0.62);
        int padY         = (hpBarH - portraitSize) / 2;
        int pad          = (int)(gamePanel.getWidth() * 0.01);
        Rectangle p1Portrait = new Rectangle(pad, padY, portraitSize, portraitSize);
        if (p1Portrait.contains(mx, my) && !battleOver) {
            player2.takeDamage(player2.getHealth());
            addLog("✨ ???");
            afterAnimation(1);
            return;
        }

        if (!waitingForInput || battleOver || animationPlaying) return;
        Character current = playerTurn ? player1 : player2;
        int choice = -1;
        if      (btnBasic    != null && btnBasic.contains(mx,my))                                      choice=1;
        else if (btnSkill    != null && btnSkill.contains(mx,my)    && current.getCurrentMana()>=30)   choice=2;
        else if (btnUltimate != null && btnUltimate.contains(mx,my) && current.getCurrentMana()>=50)   choice=3;
        else if (btnRest     != null && btnRest.contains(mx,my))                                       choice=4;
        if (choice != -1) executeAction(choice);
    }

    public void mouseMoved(int mx, int my) {
        int prev = hoveredBtn; hoveredBtn = -1;
        if      (btnBasic    != null && btnBasic.contains(mx,my))    hoveredBtn=0;
        else if (btnSkill    != null && btnSkill.contains(mx,my))    hoveredBtn=1;
        else if (btnUltimate != null && btnUltimate.contains(mx,my)) hoveredBtn=2;
        else if (btnRest     != null && btnRest.contains(mx,my))     hoveredBtn=3;
        if (hoveredBtn != prev) gamePanel.repaint();
    }

    private void executeAction(int skillChoice) {
        waitingForInput = false;
        Character current  = playerTurn ? player1 : player2;
        Character opponent = playerTurn ? player2 : player1;
        battleSystem.executePlayerTurn(current, opponent, skillChoice);
        addLog(current.getLastActionText());
        if (current.isLastActionFailed()) {
            addLog("Not enough mana!"); waitingForInput=true; gamePanel.repaint(); return;
        }
        if (skillChoice==4) {
            playAnimation(current, opponent, skillChoice);
            new Thread(() -> {
                try { Thread.sleep(ANIM_DURATION_MS+100); } catch (InterruptedException ignored) {}
                SwingUtilities.invokeLater(() -> afterAnimation(skillChoice));
            }).start();
        } else {
            playAnimation(current, opponent, skillChoice);
            new Thread(() -> {
                try { Thread.sleep(ANIM_DURATION_MS/2); } catch (InterruptedException ignored) {}
                SwingUtilities.invokeLater(() -> triggerHitFlash(opponent));
            }).start();
            new Thread(() -> {
                try { Thread.sleep(ANIM_DURATION_MS+100); } catch (InterruptedException ignored) {}
                SwingUtilities.invokeLater(() -> afterAnimation(skillChoice));
            }).start();
        }
    }

    private void afterAnimation(int skillChoice) {
        gamePanel.repaint();
        if (battleSystem.isBattleOver(player1, player2)) {
            battleOver = true;
            Character winner = battleSystem.getWinner(player1, player2);
            flashMessage = winner!=null ? winner.getName()+" WINS!" : "DRAW!";
            flashTime    = System.currentTimeMillis();
            addLog("★ " + flashMessage);
            gamePanel.repaint();
            new Thread(() -> {
                try { Thread.sleep(2400); } catch (InterruptedException ignored) {}
                Character rw = battleSystem.getWinner(player1, player2);
                Character rl = rw==player1 ? player2 : player1;
                SwingUtilities.invokeLater(() -> gamePanel.onRoundOver(rw, rl));
            }).start();
            return;
        }
        playerTurn = !playerTurn;
        if (!playerTurn && (isAiMode||isArcadeMode)) {
            addLog(player2.getName()+" is thinking..."); gamePanel.repaint();
            new Thread(() -> {
                try { Thread.sleep(900); } catch (InterruptedException ignored) {}
                SwingUtilities.invokeLater(() -> executeAction(battleSystem.getAiSkillChoice(player2)));
            }).start();
        } else {
            waitingForInput = true;
            addLog("What will "+(playerTurn?player1:player2).getName()+" do?");
            gamePanel.repaint();
        }
    }

    private String getSpriteKey(Character ch) {
        String name = ch.getName();
        name = name.replaceAll("^(P1|P2|AI)\\s+","");
        if (name.contains(", ")) name = name.substring(name.lastIndexOf(", ")+2);
        name = name.replaceAll("^The\\s+","");
        String[] knownKeys = {"Jollibee","RonaldMcDonald","BurgerKing","ColonelSanders",
                "TacoBell","Wendys","Poco","Julies"};
        String clean = name.replaceAll("[^a-zA-Z]","");
        for (String key : knownKeys)
            if (clean.toLowerCase().startsWith(key.toLowerCase())) return key;
        return clean;
    }

    private void addLog(String text) {
        if (text==null||text.isEmpty()) return;
        if (text.length()>38) { battleLog.add(text.substring(0,37)+"-"); battleLog.add(text.substring(37)); }
        else battleLog.add(text);
        if (battleLog.size()>60) battleLog.remove(0);
    }

    private int sf(int width, int base) { return Math.max(7,(int)(base*width/640.0)); }
}