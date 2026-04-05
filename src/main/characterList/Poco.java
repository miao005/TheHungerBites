package main.characterList;

import main.Character;

public class Poco extends Character {
    public Poco() {
        super("Poco", 170, 80, 9);
        this.title = "Master of Fries!";
        this.backstory = "Potato Corner may be the underdog, but his crispy fries pack a punch with endurance and flavor. Don't underestimate Poco — his Terra Storm has levelled entire battlefields.";
        this.basicAttack = "Fry Barrage – Deals 15-20 damage";
        this.skillAttack = "Giga Slam – Deals 22-28 damage";
        this.ultimateAttack = "Terra Storm – Deals 35-45 damage";
    }
    @Override public void basicAttack(Character target) { performAttack(target, 15, 20, 0, "Fry Barrage"); }
    @Override public void skillAttack(Character target) { performAttack(target, 22, 28, 30, "Giga Slam"); }
    @Override public void ultimateAttack(Character target) { performAttack(target, 35, 45, 50, "Terra Storm"); }
    @Override public void rest(Character target) { performAttack(target, -40, -40, 0, "Rest"); }
}
