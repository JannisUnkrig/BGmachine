import java.util.LinkedList;

public class Shop {

    private LinkedList<Minion> offers = new LinkedList<>();

    private Player myPlayer;
    private int tavernTier = 1;
    private int tavernTierUpCost = 5;
    private boolean frozen = false;

    public Shop(Player player) {
        this.myPlayer = player;
    }

    public void roll() {
        while (!offers.isEmpty()) {
            MinionPool.returnMinion(offers.get(0));
            offers.remove(0);
        }

        int amount = 3;
        if(tavernTier >= 2) amount++;
        if(tavernTier >= 4) amount++;
        if(tavernTier == 6) amount++;

        for (int i = 0; i < amount; i++) {
            offers.add(MinionPool.getRandomMinion(tavernTier));
        }
    }

    public void levelUp() {
        if(myPlayer.getCurGold() >= tavernTierUpCost) {
            myPlayer.setCurGold(myPlayer.getCurGold() - tavernTierUpCost);
            tavernTier++;
        }
    }

    public void freeze() {
        if (frozen) {
            frozen = false;
        } else {
            frozen = true;
        }
    }

    public void buy(int pos) {
        if (myPlayer.getCurGold() >= 3) {
            myPlayer.setCurGold(myPlayer.getCurGold() - 3);
            myPlayer.getHandMinions().add(offers.get(pos));
            offers.remove(pos);
        }
    }

}
