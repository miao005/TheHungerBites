package main;

import inputs.MouseInputs;
import ui.StartScreen;
import ui.MenuScreen;
import javax.swing.JPanel;
import java.awt.Graphics;

public class GamePanel extends JPanel {
    private MouseInputs mouseInputs;
    private GameState gameState = GameState.START;

    private StartScreen startScreen;
    private MenuScreen menuScreen;

    public GamePanel(){
        mouseInputs = new MouseInputs(this);
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);

        startScreen = new StartScreen(this);
        menuScreen = new MenuScreen(this);
    }

    public GameState getGameState(){ return gameState; }
    public void setGameState(GameState gameState){
        this.gameState = gameState;
        repaint();
    }

    public StartScreen getStartScreen(){ return startScreen; }
    public MenuScreen getMenuScreen(){ return menuScreen; }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        switch(gameState){
            case START:
                startScreen.draw(g, getWidth(), getHeight());
                break;
            case MENU:
                menuScreen.draw(g, getWidth(), getHeight());
                break;
        }
    }
}