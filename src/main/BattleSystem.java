package main;

public class BattleSystem {

    public void executePlayerTurn(Character current, Character opponent, int skillChoice) {
        current.regenerateMana();
        switch (skillChoice) {
            case 1: current.basicAttack(opponent); break;
            case 2: current.skillAttack(opponent); break;
            case 3: current.ultimateAttack(opponent); break;
            case 4: current.rest(current); break;
        }
    }

    public boolean isBattleOver(Character p1, Character p2) {
        return !p1.isAlive() || !p2.isAlive();
    }

    public Character getWinner(Character p1, Character p2) {
        if (p1.isAlive() && !p2.isAlive()) return p1;
        if (!p1.isAlive() && p2.isAlive()) return p2;
        return null;
    }


    public int getAiSkillChoice(Character ai) {
        double roll = Math.random();
        if (roll < 0.10 && ai.getHealth() < ai.getMaxHealth() * 0.4) {
            return 4; // rest when low HP
        } else if (roll < 0.25 && ai.getCurrentMana() >= 50) {
            return 3; // ultimate
        } else if (roll < 0.60 && ai.getCurrentMana() >= 30) {
            return 2; // skill
        } else {
            return 1; // basic
        }
    }
}