package main.characterList;

import main.Character;

public class RonaldMcDonald extends Character {
    public RonaldMcDonald() {
        super("Ronald McDonald", 135, 80, 15);
        this.title = "I'm Lovin' It, and You Will Too!";
        this.backstory = "McDonald's is the undisputed king of fast food, a place where comfort food reigns supreme. Ronald brings chaos and colour to the battlefield, striking fast and striking often.";
        this.basicAttack = "Big Mac Barrage – Deals 17-23 damage";
        this.skillAttack = "McFlurry Storm – Deals 26-34 damage";
        this.ultimateAttack = "I'm Lovin' It – Deals 41-51 damage";
    }
    @Override public void basicAttack(Character target) { performAttack(target, 17, 23, 0, "Big Mac Barrage"); }
    @Override public void skillAttack(Character target) { performAttack(target, 26, 34, 30, "McFlurry Storm"); }
    @Override public void ultimateAttack(Character target) { performAttack(target, 41, 51, 50, "I'm Lovin' It"); }
    @Override public void rest(Character target) { performAttack(target, -35, -35, 0, "Rest"); }
}
