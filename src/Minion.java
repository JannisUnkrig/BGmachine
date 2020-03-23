public abstract class Minion {

    private int id;
    private String name;
    private int stars;
    private int attack;
    private int health;
    private boolean taunt;
    private boolean divineShield;
    private boolean poisonous;
    private boolean windfury;
    private int replicatingMenace = 0;
    private int plantDeathrattle = 0;

    public Minion(int id, String name, int stars, int attack, int health, boolean taunt, boolean divineShield, boolean poisonous, boolean windfury) {
        this.id = id;
        this.name = name;
        this.stars = stars;
        this.attack = attack;
        this.health = health;
        this.taunt = taunt;
        this.divineShield = divineShield;
        this.poisonous = poisonous;
        this.windfury = windfury;
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

    public int getPlantDeathrattle() {
        return plantDeathrattle;
    }

    public void setPlantDeathrattle(int plantDeathrattle) {
        this.plantDeathrattle = plantDeathrattle;
    }
}
