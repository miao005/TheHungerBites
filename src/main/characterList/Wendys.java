package main.characterList;

import main.Character;

public class Wendys extends Character {
    public Wendys() {
        super("Wendy's", 130, 75, 10);
        this.title = "Fresh, Never Frozen!";
        this.backstory = "Wendy's focuses on precision and the ability to outlast opponents with a balance of healing and damage. Her fresh-never-frozen philosophy extends to the battlefield — always sharp, always ready.";
        this.basicAttack = "Burger Bash – Deals 18-26 damage";
        this.skillAttack = "Spicy Nugget Storm – Deals 25-33 damage";
        this.ultimateAttack = "Quality Is Our Recipe – Deals 40-52 damage";
    }
    @Override public void basicAttack(Character target) { performAttack(target, 18, 26, 0, "Burger Bash"); }
    @Override public void skillAttack(Character target) { performAttack(target, 25, 33, 30, "Spicy Nugget Storm"); }
    @Override public void ultimateAttack(Character target) { performAttack(target, 40, 52, 50, "Quality Is Our Recipe"); }
    @Override public void rest(Character target) { performAttack(target, -35, -35, 0, "Rest"); }
}
