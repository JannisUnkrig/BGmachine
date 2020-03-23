import java.util.LinkedList;

public class Player {

    private int curGold = 3;
    private int curMaxGold = 3;
    private Shop myShop = new Shop(this);

    private LinkedList<Minion> handMinions = new LinkedList<>();





    public int getCurGold() {
        return curGold;
    }

    public void setCurGold(int curGold) {
        this.curGold = curGold;
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

    public LinkedList<Minion> getHandMinions() {
        return handMinions;
    }

    public void setHandMinions(LinkedList<Minion> handMinions) {
        this.handMinions = handMinions;
    }

}
