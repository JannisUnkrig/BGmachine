import java.util.LinkedList;

public class Player {

    private final int playerNr;
    private int health = 40;
    private int curGold = 3;
    private int curMaxGold = 3;

    private Shop myShop = new Shop();
    private Board myBoard = new Board();
    private LinkedList<Minion> handMinions = new LinkedList<>();

    private Player lastOpponent = null;
    private Player secondToLastOpponent = null;


    public Player(int playerNr) {
        this.playerNr = playerNr;
    }



    public void newTurnReset() {
        if(curMaxGold < 10) curMaxGold++;
        curGold = curMaxGold;
        myShop.newTurnReset();
    }

    public void level() {
        if(curGold < myShop.getTavernTierUpCost()) return;
        curGold -= myShop.getTavernTierUpCost();
        myShop.levelUp();
    }

    public void roll() {
        if(curGold < 1) return;
        curGold--;
        myShop.roll();
    }

    public void freeze() {
        myShop.freeze();
    }

    public void buy(int pos) {
        if (curGold < 3 || handMinions.size() >= 10 || pos < 0 || pos >= myShop.getOffers().size()) return;
        curGold -= 3;
        handMinions.add(myShop.buy(pos));
    }

    public void sell(int pos) {
        if (pos < 0 || pos >= myBoard.getBoardSize()) return;
        if (curGold < 10) curGold++;
        myBoard.removeMinion(pos);
    }

    public void move(int fromPos, int toPos) {
        if (fromPos < 0 || toPos < 0 || fromPos >= myBoard.getBoardSize() || toPos >= myBoard.getBoardSize()) return;
        myBoard.moveMinion(fromPos, toPos);
    }

    public void play(int fromPos, int toPos) {
        if (fromPos < 0 || toPos < 0 || fromPos >= handMinions.size() || toPos > myBoard.getBoardSize() || myBoard.getBoardSize() >= 7) return;
        Minion minion = handMinions.get(fromPos);
        handMinions.remove(fromPos);
        myBoard.playMinion(minion, toPos);
    }


    public String toString() {
        return "Player " + playerNr + ":\nHealth: " + health + " | Gold: " + curGold + "/" + curMaxGold + "\n\n\n" +
                myShop.toString() + "\n\n" + myBoard.toString() + "\n\n" + getHandMinionsAsString();
    }

    public String getHandMinionsAsString() {
        String result = "Hand:\n\n";
        for (int i = 0; i < handMinions.size(); i++) {
            result += handMinions.get(i).toString() + "\n";
        }
        if(result.equals("Hand:\n\n")) result += "empty :(\n";
        return result;
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

    public LinkedList<Minion> getHandMinions() {
        return handMinions;
    }

    public void setHandMinions(LinkedList<Minion> handMinions) {
        this.handMinions = handMinions;
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
}
