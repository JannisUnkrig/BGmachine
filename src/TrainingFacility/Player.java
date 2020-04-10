package TrainingFacility;

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
    private int defeatedAsPlace = 1;


    public Player(int playerNr) {
        this.playerNr = playerNr;
    }



    public void newTurnReset() {
        if(curMaxGold < 10) curMaxGold++;
        curGold = curMaxGold;
        myShop.newTurnReset();
        myBoard.triggerStartOfTurnEffects();
    }

    public boolean level() {
        Game.appendToRightTextArea("\nPlayer " + playerNr + " pressed \"level\"");
        if (discoveryRunning) return false;
        if(curGold < myShop.getTavernTierUpCost()) return false;
        Game.appendToRightTextArea(" (successful)");

        curGold -= myShop.getTavernTierUpCost();
        myShop.levelUp();
        return true;
    }

    public boolean roll() {
        Game.appendToRightTextArea("\nPlayer " + playerNr + " pressed \"roll\"");
        if (discoveryRunning) return false;
        if(curGold < 1) return false;
        Game.appendToRightTextArea(" (successful)");

        curGold--;
        myShop.roll();
        return true;
    }

    public boolean freeze() {
        Game.appendToRightTextArea("\nPlayer " + playerNr + " pressed \"freeze\"");
        if (discoveryRunning) return false;
        Game.appendToRightTextArea(" (successful)");

        myShop.freeze();
        return true;
    }

    public boolean buy(int pos) {
        Game.appendToRightTextArea("\nPlayer " + playerNr + " tried buying pos " + pos);
        if (discoveryRunning) return false;
        if (curGold < 3 || handCards.size() >= 10 || pos < 0 || pos >= myShop.getOffers().size()) return false;
        Game.appendToRightTextArea(" (successful)");

        curGold -= 3;
        Minion bought = myShop.buy(pos);
        handCards.add(bought);
        checkForTriple(bought);
        return true;
    }

    public boolean sell(int pos) {
        Game.appendToRightTextArea("\nPlayer " + playerNr + " tried selling pos " + pos);
        if (discoveryRunning) return false;
        if (pos < 0 || pos >= myBoard.getBoardSize()) return false;
        Game.appendToRightTextArea(" (successful)");

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
        return true;
    }

    public boolean move(int fromPos, int toPos) {
        Game.appendToRightTextArea("\nPlayer " + playerNr + " tried moving pos " + fromPos + " to pos " + toPos);
        if (discoveryRunning) return false;
        if (fromPos < 0 || toPos < 0 || fromPos >= myBoard.getBoardSize() || toPos >= myBoard.getBoardSize()) return false;
        Game.appendToRightTextArea(" (successful)");

        myBoard.moveMinion(fromPos, toPos);
        return true;
    }

    public boolean play(int fromPos, int toPos, int targetedPos) {
        Game.appendToRightTextArea("\nPlayer " + playerNr + " tried playing pos " + fromPos + " to pos " + toPos + " targeting pos " + targetedPos);
        if (discoveryRunning) return false;
        if (fromPos < 0 || fromPos >= handCards.size()) return false;
        Card card = handCards.get(fromPos);

        if(card instanceof Minion) {
            if (toPos < 0 || toPos > myBoard.getBoardSize() || myBoard.getBoardSize() >= 7) return false;
            Game.appendToRightTextArea(" (successful)");
            Minion minion = (Minion) card;

            //tests for targeted battlecries
            if (minion.getName().equals("Rockpool Hunter") || minion.getName().equals("Toxfin")) {
                boolean hasMurloc = false;
                for (Minion possiblyMurloc : myBoard.getBoardMinions()) {
                    if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) hasMurloc = true;
                }
                if (hasMurloc && (targetedPos < 0 || targetedPos >= myBoard.getBoardSize() || (myBoard.getBoardMinion(targetedPos).getTribe() != Tribe.MURLOC
                    && myBoard.getBoardMinion(targetedPos).getTribe() != Tribe.ALL))) return false;
            }

            if (minion.getName().equals("Nathrezim Overseer")) {
                boolean hasDemon = false;
                for (Minion possiblyDemon : myBoard.getBoardMinions()) {
                    if (possiblyDemon.getTribe() == Tribe.DEMON || possiblyDemon.getTribe() == Tribe.ALL) hasDemon = true;
                }
                if (hasDemon && (targetedPos < 0 || targetedPos >= myBoard.getBoardSize() || (myBoard.getBoardMinion(targetedPos).getTribe() != Tribe.DEMON
                        && myBoard.getBoardMinion(targetedPos).getTribe() != Tribe.ALL))) return false;
            }

            if (minion.getName().equals("Houndmaster") || minion.getName().equals("Virmen Sensei")) {
                boolean hasBeast = false;
                for (Minion possiblyBeast : myBoard.getBoardMinions()) {
                    if (possiblyBeast.getTribe() == Tribe.BEAST || possiblyBeast.getTribe() == Tribe.ALL) hasBeast = true;
                }
                if (hasBeast && (targetedPos < 0 || targetedPos >= myBoard.getBoardSize() || (myBoard.getBoardMinion(targetedPos).getTribe() != Tribe.BEAST
                        && myBoard.getBoardMinion(targetedPos).getTribe() != Tribe.ALL))) return false;
            }

            if (minion.getName().equals("Screwjank Clunker")) {
                boolean hasMech = false;
                for (Minion possiblyMech : myBoard.getBoardMinions()) {
                    if (possiblyMech.getTribe() == Tribe.MECH || possiblyMech.getTribe() == Tribe.ALL) hasMech = true;
                }
                if (hasMech && (targetedPos < 0 || targetedPos >= myBoard.getBoardSize() || (myBoard.getBoardMinion(targetedPos).getTribe() != Tribe.MECH
                        && myBoard.getBoardMinion(targetedPos).getTribe() != Tribe.ALL))) return false;
            }

            if (minion.getName().equals("Twilight Emissary")) {
                boolean hasDragon = false;
                for (Minion possiblyDragon : myBoard.getBoardMinions()) {
                    if (possiblyDragon.getTribe() == Tribe.DRAGON || possiblyDragon.getTribe() == Tribe.ALL) hasDragon = true;
                }
                if (hasDragon && (targetedPos < 0 || targetedPos >= myBoard.getBoardSize() || (myBoard.getBoardMinion(targetedPos).getTribe() != Tribe.DRAGON
                        && myBoard.getBoardMinion(targetedPos).getTribe() != Tribe.ALL))) return false;
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
            if (spell.getCost() > curGold) return false;
            Game.appendToRightTextArea(" (successful)");

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
        return true;
    }

    public boolean choose(int whichOne) {
        Game.appendToRightTextArea("\nPlayer " + playerNr + " tried choosing pos " + whichOne);
        if (!discoveryRunning) return false;
        if (whichOne < 0 || whichOne >= discoverOptions.size()) return false;
        Game.appendToRightTextArea(" (successful)");

        if (discoveryName.equals("Triple Reward")) {
            handCards.add(discoverOptions.get(whichOne));
            discoverOptions.remove(whichOne);
            for (Minion m : discoverOptions) {
                MinionPool.returnMinion(m);
                discoverOptions.remove(m);
            }
        }

        discoveryRunning = false;
        return true;
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
                else if (onBoard.contains(triplePart)) myBoard.removeMinion(myBoard.getBoardMinions().indexOf(triplePart));
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

    public void setHealth(int to) {
        this.health = to;
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
