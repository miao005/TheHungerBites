package main;

import inputs.MouseInputs;
import ui.StartScreen;
import ui.MenuScreen;
import ui.CharacterSelectScreen;
import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel {
    // Fixed resolution for pixel art - 640x360
    public static final int WIDTH = 640;
    public static final int HEIGHT = 360;

    private MouseInputs mouseInputs;
    private GameState gameState = GameState.START;
    private String gameMode = "PVP";
    private String selectedCharacter = "";

    // Screens
    private StartScreen startScreen;
    private MenuScreen menuScreen;
    private CharacterSelectScreen characterSelectScreen;

    public GamePanel(){
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        this.setMaximumSize(new Dimension(WIDTH, HEIGHT));

        mouseInputs = new MouseInputs(this);
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);

        // Initialize screens
        startScreen = new StartScreen(this);
        menuScreen = new MenuScreen(this);
        characterSelectScreen = new CharacterSelectScreen(this);
    }

    // Getters and Setters
    public GameState getGameState() { return gameState; }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        repaint();
    }

    public String getGameMode() { return gameMode; }
    public void setGameMode(String gameMode) { this.gameMode = gameMode; }

    public String getSelectedCharacter() { return selectedCharacter; }
    public void setSelectedCharacter(String selectedCharacter) {
        this.selectedCharacter = selectedCharacter;
        System.out.println("Selected: " + selectedCharacter + " for " + gameMode);
    }

    public StartScreen getStartScreen() { return startScreen; }
    public MenuScreen getMenuScreen() { return menuScreen; }
    public CharacterSelectScreen getCharacterSelectScreen() { return characterSelectScreen; }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        switch(gameState){
            case START:
                startScreen.draw(g, WIDTH, HEIGHT);
                break;
            case MENU:
                menuScreen.draw(g, WIDTH, HEIGHT);
                break;
            case CHARACTER_SELECT:
                characterSelectScreen.draw(g, WIDTH, HEIGHT);
                break;
            default:
                // Fallback
                g.setColor(Color.black);
                g.fillRect(0, 0, WIDTH, HEIGHT);
                break;
        }
    }
}