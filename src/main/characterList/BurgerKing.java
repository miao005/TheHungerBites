package main.characterList;

import main.Character;

public class BurgerKing extends Character {
    public BurgerKing() {
        super("Burger King", 140, 70, 10);
        this.title = "The King of Burgers Rules the Battlefield!";
        this.backstory = "Burger King is bold and confident, dominating the battlefield with brute force and control. He rules with an iron fist and a flame-grilled fury that no opponent can ignore.";
        this.basicAttack = "Jr Whopper Slap – Deals 22-28 damage";
        this.skillAttack = "Royal Whopper Slam – Deals 30-40 damage";
        this.ultimateAttack = "Flame-Grilled Fury – Deals 45-55 damage";
    }
    @Override public void basicAttack(Character target) { performAttack(target, 22, 28, 0, "Jr Whopper Slap"); }
    @Override public void skillAttack(Character target) { performAttack(target, 30, 40, 30, "Royal Whopper Slam"); }
    @Override public void ultimateAttack(Character target) { performAttack(target, 45, 55, 50, "Flame-Grilled Fury"); }
    @Override public void rest(Character target) { performAttack(target, -35, -35, 0, "Rest"); }
}