package main;

import main.characterList.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CharacterManager {
    private List<Character> roster;

    public CharacterManager() {
        roster = new ArrayList<>();
        roster.add(new Jollibee());
        roster.add(new RonaldMcDonald());
        roster.add(new BurgerKing());
        roster.add(new ColonelSanders());
        roster.add(new TacoBell());
        roster.add(new Wendys());
        roster.add(new Poco());
        roster.add(new Julies());
    }

    public List<Character> getRoster() {
        return roster;
    }

    public int getRosterSize() {
        return roster.size();
    }

    /** Creates a fresh instance of the character at the given roster index */
    public Character createCharacter(int index) {
        switch (index) {
            case 0: return new Jollibee();
            case 1: return new RonaldMcDonald();
            case 2: return new BurgerKing();
            case 3: return new ColonelSanders();
            case 4: return new TacoBell();
            case 5: return new Wendys();
            case 6: return new Poco();
            case 7: return new Julies();
            default: return new Jollibee();
        }
    }

    public Character getRandomCharacter(int excludeIndex) {
        Random rand = new Random();
        int idx;
        do {
            idx = rand.nextInt(roster.size());
        } while (idx == excludeIndex);
        return createCharacter(idx);
    }

    /** Returns a shuffled array of indices excluding the player's index, for Arcade mode */
    public int[] getArcadeOpponentIndices(int playerIndex) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < roster.size(); i++) {
            if (i != playerIndex) indices.add(i);
        }
        // Shuffle
        for (int i = indices.size() - 1; i > 0; i--) {
            int j = (int)(Math.random() * (i + 1));
            int tmp = indices.get(i);
            indices.set(i, indices.get(j));
            indices.set(j, tmp);
        }
        // Return first 5
        int[] result = new int[Math.min(5, indices.size())];
        for (int i = 0; i < result.length; i++) result[i] = indices.get(i);
        return result;
    }

    /** Arcade flavour names */
    public String getArcadeName(String originalName) {
        switch (originalName.toLowerCase()) {
            case "jollibee": return "Geoffred's pick, Jollibee";
            case "ronald mcdonald": return "Jandyll's pick, Ronald McDonald";
            case "burger king": return "Kimjie's pick, The Burger King";
            case "julie's": return "Jennifer's pick, Julie's the Baker";
            case "poco": return "Keeia's favourite, Poco the Potato";
            case "colonel sanders": return "The Colonel's Revenge";
            case "taco bell": return "Taco Bell, Spice Master";
            case "wendy's": return "Wendy's, the Frost Queen";
            default: return originalName;
        }
    }

    /** AI flavour names */
    public String getAiName(String originalName) {
        return "AI " + originalName;
    }
}