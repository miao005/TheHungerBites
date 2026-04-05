package main.characterList;

import main.Character;

public class Julies extends Character {
    public Julies() {
        super("Julie's", 110, 85, 12);
        this.title = "Freshly Baked, Fiercely Battle-Ready!";
        this.backstory = "Julie spreads joy through baked goods that can heal and harm with magic frosting. Her Ensaymada is deceptively powerful, and her ultimate is the stuff of bakery legend.";
        this.basicAttack = "Ensaymada – Deals 19-25 damage";
        this.skillAttack = "Pan De Leche – Deals 28-36 damage";
        this.ultimateAttack = "Basta Julie's Fresh Yan – Deals 43-55 damage";
    }
    @Override public void basicAttack(Character target) { performAttack(target, 19, 25, 0, "Ensaymada"); }
    @Override public void skillAttack(Character target) { performAttack(target, 28, 36, 30, "Pan De Leche"); }
    @Override public void ultimateAttack(Character target) { performAttack(target, 43, 55, 50, "Basta Julie's Fresh Yan"); }
    @Override public void rest(Character target) { performAttack(target, -30, -30, 0, "Rest"); }
}
