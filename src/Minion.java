public class Minion {

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
    }

    public Minion(Minion toCopy) {
        this.id = toCopy.getId();
        this.name = toCopy.getName();
        this.stars = toCopy.getStars();
        this.attack = toCopy.getAttack();
        this.health = toCopy.getHealth();
        this.tribe = toCopy.getTribe();

        this.taunt = toCopy.isTaunt();
        this.divineShield = toCopy.isDivineShield();
        this.poisonous = toCopy.isPoisonous();
        this.windfury = toCopy.isWindfury();
        this.replicatingMenace = toCopy.getReplicatingMenace();
        this.annoyOModule = toCopy.getAnnoyOModule();
        this.goldenReplicatingMenace = toCopy.getGoldenReplicatingMenace();
        this.goldenAnnoyOModule = toCopy.getGoldenAnnoyOModule();
        this.plantDeathrattle = toCopy.getPlantDeathrattle();
    }

    public String toString() {
        String built = "" + name + " | " + attack + "/" + health;
        if(taunt) built += ", taunt";
        if(divineShield) built += ", divine shield";
        if(poisonous) built += ", poisonous";
        if(windfury) built += ", windfury";
        if(replicatingMenace > 0) built += ", " + replicatingMenace + " replicating menace";
        if(plantDeathrattle > 0) built += ", " + plantDeathrattle + " plant deathrattle";
        return built;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void reduceHealth(int by) {
        this.health -= by;
    }

    public Tribe getTribe() { return tribe; }

    public void setTribe(Tribe tribe) { this.tribe = tribe; }

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
