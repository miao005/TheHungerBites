package main;

public class MatchManager {

    // ── Constants ─────────────────────────────────────────────────────
    public static final int ROUNDS_TO_WIN = 2;   // first to 2 wins takes the match
    public static final int MAX_ROUNDS    = 3;   // best-of-3

    // ── Match participants ─────────────────────────────────────────────
    private Character player1;
    private Character player2;

    // ── Scores ────────────────────────────────────────────────────────
    private int p1Wins = 0;
    private int p2Wins = 0;
    private int currentRound = 0; // 0-based; incremented at start of each round

    // ── Mode flags (passed through to BattleScreen) ───────────────────
    private boolean aiMode;
    private boolean arcadeMode;

    // ── Snapshot HP/Mana for resets ───────────────────────────────────
    // We reset to MAX each round so characters start fresh,
    // but keep any permanent stat changes (damage multiplier, maxHp boosts).
    public MatchManager() {}

    /**
     * Initialises a fresh match between two characters.
     * Call once per opponent pairing (not once per round).
     */
    public void startMatch(Character p1, Character p2,
                           boolean aiMode, boolean arcadeMode) {
        this.player1      = p1;
        this.player2      = p2;
        this.aiMode       = aiMode;
        this.arcadeMode   = arcadeMode;
        this.p1Wins       = 0;
        this.p2Wins       = 0;
        this.currentRound = 0;
    }

    /**
     * Call after a round ends with the round's winner (null = draw).
     * Increments win counter and round number.
     *
     * @return true if the match is now decided, false if another round is needed.
     */
    public boolean recordRoundResult(Character roundWinner) {
        if (roundWinner == player1) {
            p1Wins++;
        } else if (roundWinner == player2) {
            p2Wins++;
        }
        // draw counts for neither — just burns a round

        currentRound++;

        return isMatchOver();
    }

    /**
     * Returns true when either player has reached ROUNDS_TO_WIN wins
     * OR there are no more rounds left to play.
     */
    public boolean isMatchOver() {
        if (arcadeMode) {
            return currentRound >= 1;
        }
        return p1Wins >= ROUNDS_TO_WIN
                || p2Wins >= ROUNDS_TO_WIN
                || currentRound >= MAX_ROUNDS;
    }

    /**
     * Returns the match winner, or null if it is a draw.
     * Only meaningful once {@link #isMatchOver()} returns true.
     */
    public Character getMatchWinner() {
        if (p1Wins > p2Wins) return player1;
        if (p2Wins > p1Wins) return player2;
        return null; // draw
    }

    /**
     * The round number that is ABOUT TO START (1-based).
     * Increment currentRound via recordRoundResult first.
     */
    public int getNextRoundNumber() {
        return currentRound + 1;
    }

    /**
     * Current round number that is IN PROGRESS (1-based).
     * Valid after the first call to advanceRound().
     */
    public int getCurrentRoundNumber() {
        return currentRound; // currentRound is incremented after each round ends
    }

    /**
     * Returns the current round as a 1-based display number.
     * Used by BattleScreen to show "ROUND X" in the HP bar for PVP/PvAI.
     */
    public int getCurrentRound() {
        return Math.max(1, currentRound);
    }

    /**
     * Resets both characters to full HP and full Mana so they start the
     * next round fresh. Permanent stat boosts (maxHp, damageMultiplier) are kept.
     */
    public void resetCharactersForNextRound() {
        player1.setHealth(player1.getMaxHealth());
        player1.setCurrentMana(player1.getMaxMana());
        player2.setHealth(player2.getMaxHealth());
        player2.setCurrentMana(player2.getMaxMana());
    }

    // ── Getters ───────────────────────────────────────────────────────
    public Character getPlayer1()   { return player1; }
    public Character getPlayer2()   { return player2; }
    public int getP1Wins()          { return p1Wins; }
    public int getP2Wins()          { return p2Wins; }
    public boolean isAiMode()       { return aiMode; }
    public boolean isArcadeMode()   { return arcadeMode; }
}