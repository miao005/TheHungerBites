package main;

import java.util.Random;

public abstract class Character {
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int currMana;
    protected int maxMana;
    protected int regenMana;
    protected Random random;
    protected String title;
    protected String backstory;
    protected String basicAttack;
    protected String skillAttack;
    protected String ultimateAttack;
    private double damageMultiplier = 1.0;
    protected boolean isPlayer;

    // Last action info for battle log
    private String lastActionText = "";
    private int lastDamageDealt = 0;
    private boolean lastActionFailed = false;
    private boolean lastActionWasRest = false;
    private int lastHealAmount = 0;

    public Character(String name, int maxHp, int maxMana, int regenMana) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxMana = maxMana;
        this.currMana = maxMana;
        this.regenMana = regenMana;
        this.random = new Random();
    }

    public void regenerateMana() {
        this.currMana = Math.min(this.maxMana, this.currMana + this.regenMana);
    }

    protected void performAttack(Character target, int minDamage, int maxDamage, int manaCost, String skillName) {
        lastActionWasRest = false;
        lastActionFailed = false;
        lastDamageDealt = 0;
        lastHealAmount = 0;

        if (skillName.equals("Rest")) {
            lastActionWasRest = true;
            int heal = (int) Math.floor(this.maxHp * 0.10);
            this.hp = (int) Math.min(this.maxHp, this.hp + heal);
            lastHealAmount = heal;
            lastActionText = this.name + " rests and heals +" + heal + " HP!";
            return;
        }

        if (this.currMana < manaCost) {
            lastActionFailed = true;
            lastActionText = this.name + " doesn't have enough mana for " + skillName + "!";
            return;
        }

        this.currMana -= manaCost;
        int baseDamage = minDamage + this.random.nextInt(maxDamage - minDamage + 1);
        int finalDamage = (int) (baseDamage * this.damageMultiplier);
        target.takeDamage(finalDamage);
        lastDamageDealt = finalDamage;
        lastActionText = this.name + " uses " + skillName + " → " + finalDamage + " damage to " + target.name + "!";
    }

    public abstract void basicAttack(Character target);
    public abstract void skillAttack(Character target);
    public abstract void ultimateAttack(Character target);
    public abstract void rest(Character target);

    public void takeDamage(int damage) {
        this.hp = Math.max(0, this.hp - damage);
    }

    public boolean isAlive() { return this.hp > 0; }
    public boolean isPlayer() { return isPlayer; }

    // Getters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getHealth() { return hp; }
    public int getMaxHealth() { return maxHp; }
    public int getCurrentMana() { return currMana; }
    public int getMaxMana() { return maxMana; }
    public String getTitle() { return title; }
    public String getBackstory() { return backstory; }
    public void setMaxHealth(int maxHealth) { this.maxHp = maxHealth; }
    public void setMaxMana(int maxMana) { this.maxMana = maxMana; }
    public void setHealth(int health) { this.hp = Math.max(0, Math.min(health, this.maxHp)); }
    public void setCurrentMana(int mana) { this.currMana = Math.min(mana, this.maxMana); }
    public void increaseDamage(double multiplier) { this.damageMultiplier += multiplier; }
    public void setPlayer(boolean isPlayer) { this.isPlayer = isPlayer; }
    public String getLastActionText() { return lastActionText; }
    public boolean isLastActionFailed() { return lastActionFailed; }

    // Raw attack name getters (for UI)
    public String getBasicAttackName() {
        if (basicAttack == null) return "Basic Attack";
        return basicAttack.replaceAll("\\s*–.*", "").trim();
    }
    public String getSkillAttackName() {
        if (skillAttack == null) return "Skill Attack";
        return skillAttack.replaceAll("\\s*–.*", "").trim();
    }
    public String getUltimateAttackName() {
        if (ultimateAttack == null) return "Ultimate";
        return ultimateAttack.replaceAll("\\s*–.*", "").trim();
    }
}