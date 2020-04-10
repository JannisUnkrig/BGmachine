package TrainingFacility;

public class Spell extends Card {

    private String name;
    private int discoverTier = 0;       //exclusively for triple reward and elise's recruitment map
    private int cost;

    public Spell(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    public Spell(String name, int discoverTier, int cost) {
        this.name = name;
        this.discoverTier = discoverTier;
        this.cost = cost;
    }

    public String toString() {
        String built = name;
        if (discoverTier != 0) built += " for Tier " + discoverTier + " Minion";
        built += " | Cost: " + cost;
        return built;
    }

    public String getName() {
        return name;
    }

    public int getDiscoverTier() {
        return discoverTier;
    }

    public int getCost() {
        return cost;
    }
}
