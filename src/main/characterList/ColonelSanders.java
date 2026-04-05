package main.characterList;

import main.Character;

public class ColonelSanders extends Character {
    public ColonelSanders() {
        super("Colonel Sanders", 180, 50, 9);
        this.title = "Finger Lickin' Good Combat!";
        this.backstory = "KFC brings the heat with his secret recipe of abilities, offering a mix of high-damage attacks and powerful debuffs. The Colonel's endurance on the battlefield is legendary.";
        this.basicAttack = "Kentucky's Best Punch – Deals 18-22 damage";
        this.skillAttack = "Colonel's Spice Shot – Deals 25-32 damage";
        this.ultimateAttack = "Finger Lickin' Good – Deals 40-50 damage";
    }
    @Override public void basicAttack(Character target) { performAttack(target, 18, 22, 0, "Kentucky's Best Punch"); }
    @Override public void skillAttack(Character target) { performAttack(target, 25, 32, 30, "Colonel's Spice Shot"); }
    @Override public void ultimateAttack(Character target) { performAttack(target, 40, 50, 50, "Finger Lickin' Good"); }
    @Override public void rest(Character target) { performAttack(target, -40, -40, 0, "Rest"); }
}
