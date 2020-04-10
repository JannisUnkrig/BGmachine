package TrainingFacility;

import java.util.LinkedList;

public class Shop {

    private LinkedList<Minion> offers = new LinkedList<>();
    private Player myPlayer;

    private int tavernTier = 1;
    private int tavernTierUpCost = 6;       //bei neuem spiel direkt -1
    private boolean frozen = false;


    public Shop(Player player) {
        myPlayer = player;
    }


    public void newTurnReset() {
        if(tavernTierUpCost > 0 && tavernTier < 6) this.tavernTierUpCost--;

        if(!frozen) {
            while (!offers.isEmpty()) {
                MinionPool.returnMinion(offers.get(0));
                offers.remove(0);
            }
        }

        int amount = 3;
        if(tavernTier >= 2) amount++;
        if(tavernTier >= 4) amount++;
        if(tavernTier == 6) amount++;

        for (int i = offers.size(); i < amount; i++) {
            offers.add(MinionPool.getRandomMinionUpTo(tavernTier));
        }


        frozen = false;
    }

    public void roll() {
        frozen = false;

        while (!offers.isEmpty()) {
            MinionPool.returnMinion(offers.get(0));
            offers.remove(0);
        }

        int amount = 3;
        if(tavernTier >= 2) amount++;
        if(tavernTier >= 4) amount++;
        if(tavernTier == 6) amount++;

        for (int i = 0; i < amount; i++) {
            offers.add(MinionPool.getRandomMinionUpTo(tavernTier));
        }
    }

    public void levelUp() {
        tavernTier++;
        switch (tavernTier) {
            case 2:
                tavernTierUpCost = 7;
                break;
            case 3:
                tavernTierUpCost = 8;
                break;
            case 4:
                tavernTierUpCost = 9;
                break;
            case 5:
                tavernTierUpCost = 10;
                break;
            default:
                tavernTierUpCost = 42;
        }
    }

    public void freeze() {
        if (frozen) {
            frozen = false;
        } else {
            frozen = true;
        }
    }

    public Minion buy(int pos) {
        Minion bought = offers.get(pos);
        offers.remove(pos);
        return bought;
    }


    @Override
    public String toString() {
        String result = "Tavern:\nTier: " + tavernTier;
        if(tavernTier < 6) result += " | Tier up cost: " + tavernTierUpCost;
        if(frozen) result += " (frozen)";
        result += "\n\n";
        boolean empty = true;
        for (int i = 0; i < offers.size(); i++) {
            result += getMinionForDisplay(i).toString() + "\n";
            empty = false;
        }
        if(empty) result += "empty :(\n";
        return result;
    }

    public Minion getMinionForDisplay(int pos) {
        Minion m = new Minion(offers.get(pos));

        if(m.getName().equals("Old Murk-Eye")) {
            int murlocCounter = -1;                 //-1 because only other murlocs count
            LinkedList<Minion> playersBoard = myPlayer.getMyBoard().getBoardMinions();
            for (Minion possiblyMurloc : playersBoard) {
                if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) {
                    murlocCounter++;
                }
            }
            for (Minion possiblyMurloc : offers) {
                if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) {
                    murlocCounter++;
                }
            }
            if (!m.isGolden()) {
                m.addAttack(murlocCounter);
            } else {
                m.addAttack(murlocCounter * 2);
            }
        }

        return m;
    }


    public LinkedList<Minion> getOffers() {
        return offers;
    }

    public int getTavernTier() {
        return tavernTier;
    }

    public int getTavernTierUpCost() {
        return tavernTierUpCost;
    }

}
