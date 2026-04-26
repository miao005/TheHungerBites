package main;

import inputs.MouseInputs;
import ui.*;

import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel {
    // Fixed resolution for pixel art - 640x360
    public static final int WIDTH  = 640;
    public static final int HEIGHT = 360;

    private MouseInputs mouseInputs;
    private GameState gameState = GameState.START;
    private String gameMode = "PVP";
    private String selectedCharacter = "";

    // Managers
    private CharacterManager characterManager;
    private BattleSystem battleSystem;

    /**
     * MatchManager owns all best-of-3 state for the CURRENT match
     * (PVP, PvAI, or one Arcade bout against a single opponent).
     */
    private MatchManager matchManager;

    // Screens
    private StartScreen startScreen;
    private MenuScreen menuScreen;
    private CharacterSelectScreen characterSelectScreen;
    private BattleScreen battleScreen;
    private ArcadeRewardScreen arcadeRewardScreen;
    private GameOverScreen gameOverScreen;
    private RoundCutscene roundCutscene;

    // ── Arcade series state ───────────────────────────────────────────
    // Arcade = multiple opponents, each fought in a best-of-3 match.
    private Character arcadePlayer;
    private int[] arcadeOpponentIndices;
    private int arcadeCurrentOpponent = 0;   // which opponent (0-based) we are on
    private int arcadePlayerIndex = 0;
    private static final int ARCADE_OPPONENTS = 5; // total opponents in arcade run

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setMinimumSize(new Dimension(WIDTH, HEIGHT));

        characterManager = new CharacterManager();
        battleSystem     = new BattleSystem();
        matchManager     = new MatchManager();

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
    }

    // ═════════════════════════════════════════════════════════════════
    //  Game-flow entry points (called by screens / menus)
    // ═════════════════════════════════════════════════════════════════

    /** Start a fresh PVP best-of-3 match. */
    public void startPvpBattle(int p1Index, int p2Index) {
        Character p1 = characterManager.createCharacter(p1Index);
        Character p2 = characterManager.createCharacter(p2Index);
        p1.setName("P1 " + p1.getName());
        p2.setName("P2 " + p2.getName());
        p1.setPlayer(true);
        p2.setPlayer(false);

        matchManager.startMatch(p1, p2, false, false);
        launchRound(); // starts round 1
    }

    /** Start a fresh PvAI best-of-3 match. */
    public void startPvAiBattle(int playerIndex) {
        Character p1 = characterManager.createCharacter(playerIndex);
        Character ai = characterManager.getRandomCharacter(playerIndex);
        p1.setPlayer(true);
        ai.setPlayer(false);
        ai.setName(characterManager.getAiName(ai.getName()));

        matchManager.startMatch(p1, ai, true, false);
        launchRound(); // starts round 1
    }

    /** Start an Arcade run (5 opponents, each a best-of-3). */
    public void startArcade(int playerIndex) {
        arcadePlayerIndex     = playerIndex;
        arcadePlayer          = characterManager.createCharacter(playerIndex);
        arcadePlayer.setPlayer(true);
        arcadeOpponentIndices = characterManager.getArcadeOpponentIndices(playerIndex);
        arcadeCurrentOpponent = 0;
        launchNextArcadeOpponent();
    }

    // ═════════════════════════════════════════════════════════════════
    //  Internal round / match progression
    // ═════════════════════════════════════════════════════════════════

    /**
     * Launches the next round of the current match.
     * Uses matchManager to determine which round number to display,
     * resets characters (HP/Mana) for rounds 2+, and fires the cutscene.
     */
    private void launchRound() {
        int roundNumber = matchManager.getNextRoundNumber(); // 1, 2, or 3

        // Reset HP/Mana for round 2 and 3 (round 1 already starts fresh)
        if (roundNumber > 1) {
            matchManager.resetCharactersForNextRound();
        }

        Character p1 = matchManager.getPlayer1();
        Character p2 = matchManager.getPlayer2();

        // Cutscene shows "ROUND X"
        roundCutscene.setup(p1, p2, roundNumber);

        // Tell BattleScreen to start
        battleScreen.startBattle(
                p1, p2,
                matchManager.isAiMode(),
                matchManager.isArcadeMode(),
                roundNumber,
                MatchManager.MAX_ROUNDS
        );
        battleScreen.triggerRoundStart(roundNumber);

        setGameState(GameState.CUTSCENE);
        setGameState(GameState.BATTLE);
    }

    /**
     * Called by BattleScreen when a single round finishes.
     * Routes to: next round, arcade reward screen, or game-over.
     *
     * @param roundWinner the Character who won this round (null = draw)
     * @param roundLoser  the other Character
     */
    public void onRoundOver(Character roundWinner, Character roundLoser) {
        boolean matchDone = matchManager.recordRoundResult(roundWinner);

        if (matchDone) {
            Character matchWinner = matchManager.getMatchWinner();
            Character matchLoser  = (matchWinner == matchManager.getPlayer1())
                    ? matchManager.getPlayer2()
                    : matchManager.getPlayer1();

            if (matchManager.isArcadeMode()) {
                if (matchWinner == matchManager.getPlayer1()) {
                    // Player won this arcade bout
                    onArcadeBoutWon();
                } else {
                    // Player lost this arcade bout → game over
                    goToGameOver(matchWinner, matchLoser, true, arcadeCurrentOpponent);
                }
            } else {
                goToGameOver(matchWinner, matchLoser, false,
                        matchManager.getCurrentRoundNumber());
            }
        } else {
            // Match not yet decided — play the next round
            launchRound();
        }
    }

    // ── Arcade helpers ────────────────────────────────────────────────

    /**
     * Begins a best-of-3 match against the next arcade opponent.
     * The player's permanent stat boosts carry over; HP/Mana are reset.
     */
    private void launchNextArcadeOpponent() {
        if (arcadeCurrentOpponent >= Math.min(arcadeOpponentIndices.length, ARCADE_OPPONENTS)) {
            goToGameOver(arcadePlayer, null, true, arcadeCurrentOpponent);
            return;
        }

        Character opponent = characterManager.createCharacter(
                arcadeOpponentIndices[arcadeCurrentOpponent]);
        opponent.setPlayer(false);
        opponent.setName(characterManager.getArcadeName(opponent.getName()));

        // Full HP/Mana reset for the player at the start of each new opponent
        arcadePlayer.setHealth(arcadePlayer.getMaxHealth());
        arcadePlayer.setCurrentMana(arcadePlayer.getMaxMana());

        matchManager.startMatch(arcadePlayer, opponent, false, true);
        launchRound();
    }

    /**
     * Called when arcadePlayer wins the best-of-3 bout vs. the current opponent.
     */
    private void onArcadeBoutWon() {
        arcadeCurrentOpponent++;
        if (arcadeCurrentOpponent >= Math.min(arcadeOpponentIndices.length, ARCADE_OPPONENTS)) {
            goToGameOver(arcadePlayer, null, true, arcadeCurrentOpponent);
        } else {
            arcadeRewardScreen.setup(arcadePlayer, arcadeCurrentOpponent, ARCADE_OPPONENTS);
            setGameState(GameState.ARCADE_REWARD);
        }
    }

    /** Called by ArcadeRewardScreen after the player picks a reward. */
    public void continueArcade() {
        launchNextArcadeOpponent();
    }

    /**
     * Legacy hook — BattleScreen may still call this for arcade wins.
     * Re-routed through onRoundOver so MatchManager stays authoritative.
     */
    public void onArcadeRoundWon(int roundJustWon, int totalRounds) {
        onRoundOver(matchManager.getPlayer1(), matchManager.getPlayer2());
    }

    // ── Game Over ─────────────────────────────────────────────────────
    public void goToGameOver(Character winner, Character loser,
                             boolean arcadeMode, int roundsWon) {
        gameOverScreen.setup(winner, loser, arcadeMode, roundsWon);
        setGameState(GameState.GAME_OVER);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Getters / Setters
    // ═════════════════════════════════════════════════════════════════
    public GameState getGameState()  { return gameState; }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        repaint();
    }

    public String getGameMode()      { return gameMode; }
    public void setGameMode(String gameMode) { this.gameMode = gameMode; }

    public String getSelectedCharacter() { return selectedCharacter; }
    public void setSelectedCharacter(String selectedCharacter) {
        this.selectedCharacter = selectedCharacter;
        System.out.println("Selected: " + selectedCharacter + " for " + gameMode);
    }

    /** Exposes MatchManager so BattleScreen can read win counts for the HUD. */
    public MatchManager getMatchManager() { return matchManager; }

    public StartScreen getStartScreen()                     { return startScreen; }
    public MenuScreen getMenuScreen()                       { return menuScreen; }
    public CharacterSelectScreen getCharacterSelectScreen() { return characterSelectScreen; }
    public BattleScreen getBattleScreen()                   { return battleScreen; }
    public ArcadeRewardScreen getArcadeRewardScreen()       { return arcadeRewardScreen; }
    public GameOverScreen getGameOverScreen()               { return gameOverScreen; }

    // ═════════════════════════════════════════════════════════════════
    //  Rendering
    // ═════════════════════════════════════════════════════════════════
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        switch (gameState) {
            case START:
                startScreen.draw(g, w, h);
                break;
            case MENU:
                menuScreen.draw(g, w, h);
                break;
            case CHARACTER_SELECT:
                characterSelectScreen.draw(g, w, h);
                break;
            case CUTSCENE:
                roundCutscene.draw(g, w, h);
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
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, w, h);
                break;
        }
    }
}
