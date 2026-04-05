package main.characterList;

import main.Character;

public class TacoBell extends Character {
    public TacoBell() {
        super("Taco Bell", 100, 90, 12);
        this.title = "Live Mas, Battle Harder!";
        this.backstory = "Taco Bell brings the heat with his spicy, unpredictable nature. Quick bursts of damage keep his enemies on their toes. Nobody sees the Bellstorm coming until it's too late.";
        this.basicAttack = "Taco Toss – Deals 20-26 damage";
        this.skillAttack = "Bellstorm – Deals 30-38 damage";
        this.ultimateAttack = "Live Mas – Deals 45-58 damage";
    }
    @Override public void basicAttack(Character target) { performAttack(target, 20, 26, 0, "Taco Toss"); }
    @Override public void skillAttack(Character target) { performAttack(target, 30, 38, 30, "Bellstorm"); }
    @Override public void ultimateAttack(Character target) { performAttack(target, 45, 58, 50, "Live Mas"); }
    @Override public void rest(Character target) { performAttack(target, -25, -25, 0, "Rest"); }
}
