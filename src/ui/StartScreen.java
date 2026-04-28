package ui;

import main.GamePanel;
import main.GameState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class StartScreen {
    private ImageIcon backgroundGif;
    private GamePanel gamePanel;
    private Timer repaintTimer;

    public StartScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        // Load GIF
        URL gifUrl = getClass().getResource("/resources/start.gif");
        if (gifUrl != null) {
            backgroundGif = new ImageIcon(gifUrl);

            // Force repaints to update GIF frames
            repaintTimer = new Timer(50, e -> {
                gamePanel.repaint();  // Force redraw 20 times per second
            });
            repaintTimer.start();
        } else {
            // Fallback to PNG
            try {
                java.awt.image.BufferedImage pngBg = ImageIO.read(getClass().getResourceAsStream("/resources/start.png"));
                if (pngBg != null) {
                    backgroundGif = new ImageIcon(pngBg);
                }
            } catch (Exception e) {
                System.err.println("Could not load start screen image");
            }
        }
    }

    public void draw(Graphics g, int width, int height) {
        if (backgroundGif != null) {
            // Use paintIcon for proper animation
            Graphics2D g2d = (Graphics2D) g;

            // Save original transform
            java.awt.geom.AffineTransform old = g2d.getTransform();

            // Scale to fit screen
            double scaleX = (double)width / backgroundGif.getIconWidth();
            double scaleY = (double)height / backgroundGif.getIconHeight();
            g2d.scale(scaleX, scaleY);

            // Draw GIF at scaled size (this animates!)
            backgroundGif.paintIcon(null, g2d, 0, 0);

            // Restore transform
            g2d.setTransform(old);
        } else {
            g.setColor(new Color(30, 20, 50));
            g.fillRect(0, 0, width, height);
        }
    }

    public void mouseClicked() {
        System.out.println("StartScreen clicked - Moving to Menu");
        if (repaintTimer != null) {
            repaintTimer.stop();  // Stop timer when leaving start screen
        }
        gamePanel.getAudioManager().playSFX("/resources/Music/click.wav");
        gamePanel.setGameState(GameState.MENU);
    }
}