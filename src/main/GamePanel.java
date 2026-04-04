package main;

import inputs.MouseInputs;
import ui.*;

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

    // Managers
    private CharacterManager characterManager;
    private BattleSystem battleSystem;

    // Screens
    private StartScreen startScreen;
    private MenuScreen menuScreen;
    private CharacterSelectScreen characterSelectScreen;
    private BattleScreen battleScreen;
    private ArcadeRewardScreen arcadeRewardScreen;
    private GameOverScreen gameOverScreen;


    // Arcade state
    private Character arcadePlayer;
    private int[] arcadeOpponentIndices;
    private int arcadeCurrentRound = 0;
    private int arcadePlayerIndex = 0;
    private static final int ARCADE_ROUNDS = 5;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setMinimumSize(new Dimension(WIDTH, HEIGHT));

        characterManager = new CharacterManager();
        battleSystem = new BattleSystem();

        mouseInputs = new MouseInputs(this);
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);

        startScreen = new StartScreen(this);
        menuScreen = new MenuScreen(this);
        characterSelectScreen = new CharacterSelectScreen(this, characterManager);
        battleScreen = new BattleScreen(this);
        arcadeRewardScreen = new ArcadeRewardScreen(this);
        gameOverScreen = new GameOverScreen(this);
    }

    // ── Game flow methods called by screens ──────────────────────────

    public void startPvpBattle(int p1Index, int p2Index) {
        Character p1 = characterManager.createCharacter(p1Index);
        Character p2 = characterManager.createCharacter(p2Index);
        p1.setName("P1 " + p1.getName());
        p2.setName("P2 " + p2.getName());
        p1.setPlayer(true);
        p2.setPlayer(false);
        battleScreen.startBattle(p1, p2, false, false, 1, 1);
        setGameState(GameState.BATTLE);
    }

    public void startPvAiBattle(int playerIndex) {
        Character p1 = characterManager.createCharacter(playerIndex);
        Character ai = characterManager.getRandomCharacter(playerIndex);
        p1.setPlayer(true);
        ai.setPlayer(false);
        ai.setName(characterManager.getAiName(ai.getName()));
        battleScreen.startBattle(p1, ai, true, false, 1, 1);
        setGameState(GameState.BATTLE);
    }

    public void startArcade(int playerIndex) {
        arcadePlayerIndex = playerIndex;
        arcadePlayer = characterManager.createCharacter(playerIndex);
        arcadePlayer.setPlayer(true);
        arcadeOpponentIndices = characterManager.getArcadeOpponentIndices(playerIndex);
        arcadeCurrentRound = 0;
        launchNextArcadeRound();
    }

    private void launchNextArcadeRound() {
        if (arcadeCurrentRound >= arcadeOpponentIndices.length) {
            // All rounds done — player won
            goToGameOver(arcadePlayer, null, true, arcadeCurrentRound);
            return;
        }
        Character opponent = characterManager.createCharacter(arcadeOpponentIndices[arcadeCurrentRound]);
        opponent.setPlayer(false);
        opponent.setName(characterManager.getArcadeName(opponent.getName()));
        int round = arcadeCurrentRound + 1;
        battleScreen.startBattle(arcadePlayer, opponent, false, true, round, ARCADE_ROUNDS);
        setGameState(GameState.BATTLE);
    }

    /** Called by BattleScreen when player wins an arcade round and there are more rounds */
    public void onArcadeRoundWon(int roundJustWon, int totalRounds) {
        arcadeCurrentRound++;
        if (arcadeCurrentRound >= Math.min(arcadeOpponentIndices.length, ARCADE_ROUNDS)) {
            // Won all rounds
            goToGameOver(arcadePlayer, null, true, arcadeCurrentRound);
        } else {
            arcadeRewardScreen.setup(arcadePlayer, roundJustWon, totalRounds);
            setGameState(GameState.ARCADE_REWARD);
        }
    }

    /** Called by ArcadeRewardScreen after player picks reward */
    public void continueArcade() {
        launchNextArcadeRound();
    }

    public void goToGameOver(Character winner, Character loser, boolean arcadeMode, int roundsWon) {
        gameOverScreen.setup(winner, loser, arcadeMode, roundsWon);
        setGameState(GameState.GAME_OVER);
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
    public BattleScreen getBattleScreen() { return battleScreen; }
    public ArcadeRewardScreen getArcadeRewardScreen() { return arcadeRewardScreen; }
    public GameOverScreen getGameOverScreen() { return gameOverScreen; }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        switch(gameState){
            case START:
                startScreen.draw(g, w, h);
                break;
            case MENU:
                menuScreen.draw(g, w, h);
                break;
            case CHARACTER_SELECT:
                characterSelectScreen.draw(g, w, h);
                break;
            case BATTLE:
                battleScreen.draw(g, w, h);
                break;
            case ARCADE_REWARD:
                arcadeRewardScreen.draw(g, w, h);
                break;
            case GAME_OVER:
                gameOverScreen.draw(g, w, h);
                break;
            default:
                g.setColor(Color.black);
                g.fillRect(0, 0, w, h);
                break;
        }
    }
}