package ui;

import main.GamePanel;
import main.GameState;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MenuScreen {
    private BufferedImage background;
    private GamePanel gamePanel;

    public MenuScreen(GamePanel gamePanel){
        this.gamePanel = gamePanel;
        try {
            background = ImageIO.read(getClass().getResourceAsStream("/resources/menu.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g, int width, int height){
        if(background != null)
            g.drawImage(background, 0, 0, width, height, null);
    }

    public void mouseClicked(){
        gamePanel.setGameState(GameState.CHARACTER_SELECT);
    }
}