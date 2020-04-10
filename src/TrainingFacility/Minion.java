package TrainingFacility;

public class Minion extends Card {

    private int id;
    private String name;
    private int stars;
    private int attack;
    private int health;
    private Tribe tribe;
    private boolean golden;

    private boolean taunt;
    private boolean divineShield;
    private boolean poisonous;
    private boolean windfury;
    private boolean reborn = false;
    private int replicatingMenace = 0;
    private int annoyOModule = 0;
    private int goldenReplicatingMenace = 0;
    private int goldenAnnoyOModule = 0;
    private int plantDeathrattle = 0;

    public Minion(int id, String name, int stars, int attack, int health, Tribe tribe, boolean golden, boolean taunt, boolean divineShield, boolean poisonous, boolean windfury) {
        this.id = id;
        this.name = name;
        this.stars = stars;
        this.attack = attack;
        this.health = health;
        this.tribe = tribe;
        this.golden = golden;

        this.taunt = taunt;
        this.divineShield = divineShield;
        this.poisonous = poisonous;
        this.windfury = windfury;
        if (name.equals("Bronze Warden")) this.reborn = true;
    }

    public Minion(Minion toCopy) {
        this.id = toCopy.getId();
        this.name = toCopy.getName();
        this.stars = toCopy.getStars();
        this.attack = toCopy.getAttack();
        this.health = toCopy.getHealth();
        this.tribe = toCopy.getTribe();
        this.golden = toCopy.isGolden();

        this.taunt = toCopy.isTaunt();
        this.divineShield = toCopy.isDivineShield();
        this.poisonous = toCopy.isPoisonous();
        this.windfury = toCopy.isWindfury();
        this.reborn = toCopy.isReborn();
        this.replicatingMenace = toCopy.getReplicatingMenace();
        this.annoyOModule = toCopy.getAnnoyOModule();
        this.goldenReplicatingMenace = toCopy.getGoldenReplicatingMenace();
        this.goldenAnnoyOModule = toCopy.getGoldenAnnoyOModule();
        this.plantDeathrattle = toCopy.getPlantDeathrattle();
    }

    public Minion() {
        this.name = "dummy";
    }

    public String toString() {
        String built = "";
        if(golden) built += "golden ";
        built += name + " | " + attack + "/" + health;
        if(taunt) built += ", taunt";
        if(divineShield) built += ", divine shield";
        if(poisonous) built += ", poisonous";
        if(windfury) built += ", windfury";
        if(reborn) built += ", reborn";
        if(replicatingMenace > 0) built += ", " + replicatingMenace + " replicating menace";
        if(plantDeathrattle > 0) built += ", " + plantDeathrattle + " plant deathrattle";
        return built;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStars() {
        return stars;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void addAttack(int howMuch) {
        this.attack += howMuch;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    //dont modify on board without notifying aura board
    public void addHealth(int howMuch) {
        this.health += howMuch;
    }

    public void reduceHealth(int by) {
        this.health -= by;
    }

    public Tribe getTribe() { return tribe; }

    public boolean isGolden() {
        return golden;
    }

    public void setGolden(boolean golden) {
        this.golden = golden;
    }

    public boolean isTaunt() {
        return taunt;
    }

    public void setTaunt(boolean taunt) {
        this.taunt = taunt;
    }

    public boolean isDivineShield() {
        return divineShield;
    }

    public void setDivineShield(boolean divineShield) {
        this.divineShield = divineShield;
    }

    public boolean isPoisonous() {
        return poisonous;
    }

    public void setPoisonous(boolean poisonous) {
        this.poisonous = poisonous;
    }

    public boolean isWindfury() {
        return windfury;
    }

    public void setWindfury(boolean windfury) {
        this.windfury = windfury;
    }

    public boolean isReborn() {
        return reborn;
    }

    public void setReborn(boolean reborn) {
        this.reborn = reborn;
    }

    public int getReplicatingMenace() {
        return replicatingMenace;
    }

    public void setReplicatingMenace(int replicatingMenace) {
        this.replicatingMenace = replicatingMenace;
    }

    public int getAnnoyOModule() {
        return annoyOModule;
    }

    public void setAnnoyOModule(int annoyOModule) {
        this.annoyOModule = annoyOModule;
    }

    public int getGoldenReplicatingMenace() {
        return goldenReplicatingMenace;
    }

    public void setGoldenReplicatingMenace(int goldenReplicatingMenace) {
        this.goldenReplicatingMenace = goldenReplicatingMenace;
    }

    public int getGoldenAnnoyOModule() {
        return goldenAnnoyOModule;
    }

    public void setGoldenAnnoyOModule(int goldenAnnoyOModule) {
        this.goldenAnnoyOModule = goldenAnnoyOModule;
    }

    public int getPlantDeathrattle() {
        return plantDeathrattle;
    }

    public void setPlantDeathrattle(int plantDeathrattle) {
        this.plantDeathrattle = plantDeathrattle;
    }
}
