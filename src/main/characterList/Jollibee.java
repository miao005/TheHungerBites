package main.characterList;

import main.Character;

public class Jollibee extends Character {
    public Jollibee() {
        super("Jollibee", 150, 100, 13);
        this.title = "The Bee of Victory!";
        this.backstory = "Jollibee is the lively and spirited mascot who prides himself on showing that fast food can win hearts. His combination of speed, attack power, and charm allows him to deliver swift, decisive blows, while his honey-sweet skills leave customers in awe.";
        this.basicAttack = "Chicken Joy Slap – Deals 16-22 damage";
        this.skillAttack = "Spaghetti ni Sir Khai – Deals 24-32 damage";
        this.ultimateAttack = "Bida Ang Saya! – Deals 38-48 damage";
    }
    @Override public void basicAttack(Character target) { performAttack(target, 16, 22, 0, "Chicken Joy Slap"); }
    @Override public void skillAttack(Character target) { performAttack(target, 24, 32, 30, "Spaghetti ni Sir Khai"); }
    @Override public void ultimateAttack(Character target) { performAttack(target, 38, 48, 50, "Bida Ang Saya!"); }
    @Override public void rest(Character target) { performAttack(target, -40, -40, 0, "Rest"); }
}
