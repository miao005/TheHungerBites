package main;

import inputs.MouseInputs;
import ui.*;

import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel {
    public static final int WIDTH  = 640;
    public static final int HEIGHT = 360;

    private MouseInputs mouseInputs;
    private GameState gameState = GameState.START;
    private GameState previousState = GameState.MENU;  // ← NEW
    private String gameMode = "PVP";
    private String selectedCharacter = "";
    private Settings settings = new Settings();
    private Leaderboard leaderboard = new Leaderboard();
    private SettingsScreen settingsScreen;
    private LeaderboardScreen leaderboardScreen;

    private CharacterManager characterManager;
    private BattleSystem battleSystem;
    private MatchManager matchManager;
    private AudioManager audioManager;

    private StartScreen startScreen;
    private MenuScreen menuScreen;
    private CharacterSelectScreen characterSelectScreen;
    private BattleScreen battleScreen;
    private ArcadeRewardScreen arcadeRewardScreen;
    private GameOverScreen gameOverScreen;
    private RoundCutscene roundCutscene;

    private Character arcadePlayer;
    private int[] arcadeOpponentIndices;
    private int arcadeCurrentOpponent = 0;
    private int arcadePlayerIndex = 0;
    private static final int ARCADE_OPPONENTS = 5;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setMinimumSize(new Dimension(WIDTH, HEIGHT));

        characterManager = new CharacterManager();
        battleSystem     = new BattleSystem();
        matchManager     = new MatchManager();
        audioManager     = new AudioManager();

        mouseInputs = new MouseInputs(this);
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);

        startScreen           = new StartScreen(this);
        menuScreen            = new MenuScreen(this);
        characterSelectScreen = new CharacterSelectScreen(this, characterManager);
        battleScreen          = new BattleScreen(this);
        arcadeRewardScreen    = new ArcadeRewardScreen(this);
        gameOverScreen        = new GameOverScreen(this);
        roundCutscene         = new RoundCutscene(this);
        settingsScreen        = new SettingsScreen(this, settings, audioManager);
        leaderboardScreen     = new LeaderboardScreen(this, leaderboard);
        gameOverScreen.setLeaderboard(leaderboard);
        audioManager.setVolume(settings.getVolume() / 100f);

        setGameState(GameState.START);
    }

    public void startPvpBattle(int p1Index, int p2Index) {
        Character p1 = characterManager.createCharacter(p1Index);
        Character p2 = characterManager.createCharacter(p2Index);
        p1.setName("P1 " + p1.getName());
        p2.setName("P2 " + p2.getName());
        p1.setPlayer(true);
        p2.setPlayer(false);
        matchManager.startMatch(p1, p2, false, false);
        launchRound();
    }

    public void startPvAiBattle(int playerIndex) {
        Character p1 = characterManager.createCharacter(playerIndex);
        Character ai = characterManager.getRandomCharacter(playerIndex);
        p1.setPlayer(true);
        ai.setPlayer(false);
        ai.setName(characterManager.getAiName(ai.getName()));
        matchManager.startMatch(p1, ai, true, false);
        launchRound();
    }

    public void startArcade(int playerIndex) {
        arcadePlayerIndex     = playerIndex;
        arcadePlayer          = characterManager.createCharacter(playerIndex);
        arcadePlayer.setPlayer(true);
        arcadeOpponentIndices = characterManager.getArcadeOpponentIndices(playerIndex);
        arcadeCurrentOpponent = 0;
        launchNextArcadeOpponent();
    }

    private void launchRound() {
        int roundNumber = matchManager.getNextRoundNumber();
        if (roundNumber > 1) matchManager.resetCharactersForNextRound();

        Character p1 = matchManager.getPlayer1();
        Character p2 = matchManager.getPlayer2();

        roundCutscene.setup(p1, p2, roundNumber);
        battleScreen.startBattle(p1, p2, matchManager.isAiMode(), matchManager.isArcadeMode(),
                roundNumber, MatchManager.MAX_ROUNDS);
        battleScreen.triggerRoundStart(roundNumber);

        setGameState(GameState.CUTSCENE);
        setGameState(GameState.BATTLE);
    }

    public void onRoundOver(Character roundWinner, Character roundLoser) {
        boolean matchDone = matchManager.recordRoundResult(roundWinner);
        if (matchDone) {
            Character matchWinner = matchManager.getMatchWinner();
            Character matchLoser  = (matchWinner == matchManager.getPlayer1())
                    ? matchManager.getPlayer2() : matchManager.getPlayer1();
            if (matchManager.isArcadeMode()) {
                if (matchWinner == matchManager.getPlayer1()) onArcadeBoutWon();
                else goToGameOver(matchWinner, matchLoser, true, arcadeCurrentOpponent);
            } else {
                goToGameOver(matchWinner, matchLoser, false, matchManager.getCurrentRoundNumber());
            }
        } else {
            launchRound();
        }
    }

    private void launchNextArcadeOpponent() {
        if (arcadeCurrentOpponent >= Math.min(arcadeOpponentIndices.length, ARCADE_OPPONENTS)) {
            goToGameOver(arcadePlayer, null, true, arcadeCurrentOpponent);
            return;
        }
        Character opponent = characterManager.createCharacter(arcadeOpponentIndices[arcadeCurrentOpponent]);
        opponent.setPlayer(false);
        opponent.setName(characterManager.getArcadeName(opponent.getName()));
        matchManager.startMatch(arcadePlayer, opponent, false, true);
        launchRound();
    }

    private void onArcadeBoutWon() {
        arcadeCurrentOpponent++;
        if (arcadeCurrentOpponent >= Math.min(arcadeOpponentIndices.length, ARCADE_OPPONENTS)) {
            goToGameOver(arcadePlayer, null, true, arcadeCurrentOpponent);
        } else {
            arcadeRewardScreen.setup(arcadePlayer, arcadeCurrentOpponent, ARCADE_OPPONENTS);
            setGameState(GameState.ARCADE_REWARD);
        }
    }

    public void continueArcade() { launchNextArcadeOpponent(); }

    public void onArcadeRoundWon(int roundJustWon, int totalRounds) {
        onRoundOver(matchManager.getPlayer1(), matchManager.getPlayer2());
    }

    public void goToGameOver(Character winner, Character loser, boolean arcadeMode, int roundsWon) {
        gameOverScreen.setup(winner, loser, arcadeMode, matchManager.isAiMode(), roundsWon);
        setGameState(GameState.GAME_OVER);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Getters / Setters
    // ═════════════════════════════════════════════════════════════════
    public GameState getGameState()     { return gameState; }
    public GameState getPreviousState() { return previousState; }  // ← NEW

    /** Save current state then navigate to SETTINGS or LEADERBOARD. */
    public void goToOverlay(GameState overlay) {  // ← NEW
        previousState = gameState;
        setGameState(overlay);
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        switch (gameState) {
            case START:
                if (!audioManager.isPlaying())
                    audioManager.playMusic("/resources/Music/menu_music.wav");
                break;
            case CHARACTER_SELECT:
                break;
            case MENU:
                if (this.gameState == GameState.GAME_OVER || !audioManager.isPlaying()) {
                    audioManager.stopMusic();
                    audioManager.playMusic("/resources/Music/menu_music.wav");
                }
                break;
            case BATTLE:
                audioManager.stopMusic();
                audioManager.playMusic("/resources/Music/battle_music.wav");
                break;
            case GAME_OVER:
                audioManager.stopMusic();
                audioManager.playMusic("/resources/Music/gameover_music.wav");
                break;
        }
        repaint();
    }

    public String getGameMode()                  { return gameMode; }
    public void   setGameMode(String gameMode)   { this.gameMode = gameMode; }

    public String getSelectedCharacter()                         { return selectedCharacter; }
    public void   setSelectedCharacter(String selectedCharacter) {
        this.selectedCharacter = selectedCharacter;
        System.out.println("Selected: " + selectedCharacter + " for " + gameMode);
    }

    public MatchManager getMatchManager()                       { return matchManager; }
    public StartScreen getStartScreen()                         { return startScreen; }
    public MenuScreen getMenuScreen()                           { return menuScreen; }
    public CharacterSelectScreen getCharacterSelectScreen()     { return characterSelectScreen; }
    public BattleScreen getBattleScreen()                       { return battleScreen; }
    public ArcadeRewardScreen getArcadeRewardScreen()           { return arcadeRewardScreen; }
    public GameOverScreen getGameOverScreen()                   { return gameOverScreen; }
    public AudioManager getAudioManager()                       { return audioManager; }
    public SettingsScreen getSettingsScreen()                   { return settingsScreen; }
    public LeaderboardScreen getLeaderboardScreen()             { return leaderboardScreen; }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();
        switch (gameState) {
            case START:          startScreen.draw(g, w, h);           break;
            case MENU:           menuScreen.draw(g, w, h);            break;
            case CHARACTER_SELECT: characterSelectScreen.draw(g, w, h); break;
            case CUTSCENE:       roundCutscene.draw(g, w, h);         break;
            case BATTLE:         battleScreen.draw(g, w, h);          break;
            case ARCADE_REWARD:  arcadeRewardScreen.draw(g, w, h);    break;
            case GAME_OVER:      gameOverScreen.draw(g, w, h);        break;
            case SETTINGS:       settingsScreen.draw(g, w, h);        break;
            case LEADERBOARD:    leaderboardScreen.draw(g, w, h);     break;
            default: g.setColor(Color.BLACK); g.fillRect(0, 0, w, h); break;
        }
    }
}