import java.util.LinkedList;

public class Player {

    private final int playerNr;
    private int health = 40;
    private int curGold = 3;
    private int curMaxGold = 3;
    private boolean discoveryRunning = false;
    private String discoveryName;
    private LinkedList<Minion> discoverOptions;

    private Shop myShop = new Shop(this);
    private Board myBoard = new Board(this);
    private LinkedList<Card> handCards = new LinkedList<>();

    private Player lastOpponent = null;
    private Player secondToLastOpponent = null;
    private int lastDamageTaken = 0;
    private int secondToLastDamageTaken = 0;
    private int defeatedAsPlace = 0;


    public Player(int playerNr) {
        this.playerNr = playerNr;
    }



    public void newTurnReset() {
        if(curMaxGold < 10) curMaxGold++;
        curGold = curMaxGold;
        myShop.newTurnReset();
        myBoard.triggerStartOfTurnEffects();
    }

    public void level() {
        if (discoveryRunning) return;
        if(curGold < myShop.getTavernTierUpCost()) return;
        curGold -= myShop.getTavernTierUpCost();
        myShop.levelUp();
    }

    public void roll() {
        if (discoveryRunning) return;
        if(curGold < 1) return;
        curGold--;
        myShop.roll();
    }

    public void freeze() {
        if (discoveryRunning) return;
        myShop.freeze();
    }

    public void buy(int pos) {
        if (discoveryRunning) return;
        if (curGold < 3 || handCards.size() >= 10 || pos < 0 || pos >= myShop.getOffers().size()) return;
        curGold -= 3;
        Minion bought = myShop.buy(pos);
        handCards.add(bought);
        checkForTriple(bought);
    }

    public void sell(int pos) {
        if (discoveryRunning) return;
        if (pos < 0 || pos >= myBoard.getBoardSize()) return;
        if (curGold < 10) curGold++;

        Minion possiblySOT = myBoard.getBoardMinion(pos);
        if (possiblySOT.getName().equals("Steward of Time")) {
            if (!possiblySOT.isGolden()) {
                for (Minion m : myShop.getOffers()) {
                    m.addAttack(1);
                    m.addHealth(1);
                }
            } else {
                for (Minion m : myShop.getOffers()) {
                    m.addAttack(2);
                    m.addHealth(2);
                }
            }
        }

        myBoard.removeMinion(pos);
    }

    public void move(int fromPos, int toPos) {
        if (discoveryRunning) return;
        if (fromPos < 0 || toPos < 0 || fromPos >= myBoard.getBoardSize() || toPos >= myBoard.getBoardSize()) return;
        myBoard.moveMinion(fromPos, toPos);
    }

    public void play(int fromPos, int toPos, int targetedPos) {
        if (discoveryRunning) return;
        if (fromPos < 0 || fromPos >= handCards.size()) return;
        Card card = handCards.get(fromPos);

        if(card instanceof Minion) {
            if (toPos < 0 || toPos > myBoard.getBoardSize() || myBoard.getBoardSize() >= 7) return;
            Minion minion = (Minion) card;

            //TODO tests for targeted battlecry
            if (minion.getName().equals("Rockpool Hunter")) {
                boolean hasMurloc = false;
                for (Minion possiblyMurloc : myBoard.getBoardMinions()) {
                    if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) hasMurloc = true;
                }
                if (hasMurloc && (targetedPos < 0 || targetedPos >= myBoard.getBoardSize() || (myBoard.getBoardMinion(targetedPos).getTribe() != Tribe.MURLOC
                    && myBoard.getBoardMinion(targetedPos).getTribe() != Tribe.ALL))) return;
            }

            handCards.remove(fromPos);
            myBoard.playMinion(minion, toPos, targetedPos);

            if(minion.isGolden()) {
                int tier;
                if (myShop.getTavernTier() < 6) {
                    tier = myShop.getTavernTier() + 1;
                } else {
                    tier = 6;
                }
                handCards.add(new Spell("Triple Reward", tier, 0));
            }
        }

        if(card instanceof Spell) {
            Spell spell = (Spell) card;
            if (spell.getCost() > curGold) return;
            //TODO play spell
            if (spell.getName().equals("Triple Reward")) {
                discoveryRunning = true;
                discoveryName = "Triple Reward";
                LinkedList<Minion> discoverOptions = new LinkedList<>();
                int discoverTier = spell.getDiscoverTier();
                for (int i = 0; i < 3; i++) {
                    discoverOptions.add(MinionPool.getRandomMinionOf(discoverTier));
                }
                this.discoverOptions = discoverOptions;
            }

            handCards.remove(spell);
        }
    }

    public void choose(int whichOne) {
        if (!discoveryRunning) return;
        if (whichOne < 0 || whichOne >= discoverOptions.size()) return;

        if (discoveryName.equals("Triple Reward")) {
            handCards.add(discoverOptions.get(whichOne));
            discoverOptions.remove(whichOne);
            for (Minion m : discoverOptions) {
                MinionPool.returnMinion(m);
                discoverOptions.remove(m);
            }
        }

        discoveryRunning = false;
    }


    public void checkForTriple(Minion minion) {
        if (minion.isGolden()) return;

        String name = minion.getName();
        LinkedList<Minion> tripleProgress = new LinkedList<>();

        LinkedList<Minion> inHand = handContainsMinion(name);
        for (Minion handMinion : inHand) {
            if (!handMinion.isGolden()) tripleProgress.add(handMinion);
        }

        LinkedList<Minion> onBoard = myBoard.contains(name);
        for (Minion boardMinion : onBoard) {
            if (!boardMinion.isGolden()) tripleProgress.add(boardMinion);
        }

        if(tripleProgress.size() >= 3) {
            Minion goldenMinion = MinionPool.generateMinion(name);
            goldenMinion.setGolden(true);

            for(Minion triplePart : tripleProgress) {
                if (inHand.contains(triplePart)) handCards.remove(triplePart);
                else if (onBoard.contains(triplePart)) myBoard.getBoardMinions().remove(triplePart);
                triplePart.addAttack(-1 * goldenMinion.getAttack());
                triplePart.addHealth(-1 * goldenMinion.getHealth());
            }
            goldenMinion.setAttack(goldenMinion.getAttack() * 2);
            goldenMinion.setHealth(goldenMinion.getHealth() * 2);
            for(Minion triplePart : tripleProgress) {
                goldenMinion.addAttack(triplePart.getAttack());
                goldenMinion.addHealth(triplePart.getHealth());
            }
            if (handCards.size() < 10) handCards.add(goldenMinion);
        }
    }

    //only for minions
    public LinkedList<Minion> handContainsMinion(String minionName) {
        LinkedList<Minion> result = new LinkedList<>();
        for (Card c : handCards) {
            if(c instanceof Minion) {
                Minion m = (Minion) c;
                if(m.getName().equals(minionName)) result.add(m);
            }
        }

        return result;
    }


    public String toString() {
        return "Player " + playerNr + ":\nHealth: " + health + " | Gold: " + curGold + "/" + curMaxGold + "\n\n\n" +
                myShop.toString() + "\n\n" + myBoard.toString() + "\n\n" + getHandCardsAsString() + getDiscoveryAsString();
    }

    public String getHandCardsAsString() {
        String result = "Hand:\n\n";
        for (int i = 0; i < handCards.size(); i++) {
            result += handCards.get(i).toString() + "\n";
        }
        if(result.equals("Hand:\n\n")) result += "empty :(\n";
        return result;
    }

    public String getDiscoveryAsString() {
        if (!discoveryRunning) return "";
        String built = "\n\nDiscovery from: " + discoveryName + "\n";
        for (Minion m : discoverOptions) {
            built += "\n" + m.toString();
        }
        return built;
    }

    public int getPlayerNr() {
        return playerNr;
    }

    public int getHealth() {
        return health;
    }

    public void reduceHealth(int by) {
        this.health -= by;
    }

    public int getCurGold() {
        return curGold;
    }

    public void setCurGold(int curGold) {
        this.curGold = curGold;
    }

    public void reduceCurGold(int by) {
        this.curGold -= by;
    }

    public void increaseCurGold(int by) {
        this.curGold += by;
    }

    public int getCurMaxGold() {
        return curMaxGold;
    }

    public void setCurMaxGold(int curMaxGold) {
        this.curMaxGold = curMaxGold;
    }

    public Shop getMyShop() {
        return myShop;
    }

    public void setMyShop(Shop myShop) {
        this.myShop = myShop;
    }

    public Board getMyBoard() {
        return myBoard;
    }

    public void setMyBoard(Board myBoard) {
        this.myBoard = myBoard;
    }

    public LinkedList<Card> getHandCards() {
        return handCards;
    }

    public Player getLastOpponent() {
        return lastOpponent;
    }

    public void setLastOpponent(Player lastOpponent) {
        this.lastOpponent = lastOpponent;
    }

    public Player getSecondToLastOpponent() {
        return secondToLastOpponent;
    }

    public void setSecondToLastOpponent(Player secondToLastOpponent) {
        this.secondToLastOpponent = secondToLastOpponent;
    }

    public int getLastDamageTaken() {
        return lastDamageTaken;
    }

    public void setLastDamageTaken(int lastDamageTaken) {
        this.lastDamageTaken = lastDamageTaken;
    }

    public int getSecondToLastDamageTaken() {
        return secondToLastDamageTaken;
    }

    public void setSecondToLastDamageTaken(int secondToLastDamageTaken) {
        this.secondToLastDamageTaken = secondToLastDamageTaken;
    }

    public int getDefeatedAsPlace() {
        return defeatedAsPlace;
    }

    public void setDefeatedAsPlace(int defeatedAsPlace) {
        this.defeatedAsPlace = defeatedAsPlace;
    }
}
