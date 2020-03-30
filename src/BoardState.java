import java.util.LinkedList;

public class BoardState implements IBoard {

    LinkedList<Minion> boardMinions = new LinkedList<>();



    @Override
    public void playMinion(Minion minion, int pos, int uselessTargetPos) {
        if(pos > boardMinions.size()) pos = boardMinions.size();            //TODO sloppy fix to correct play position of e.g. Tidehunter, if scout triples
        boardMinions.add(pos, minion);
    }

    @Override
    public void moveMinion(int fromPos, int toPos) {
        Minion save = boardMinions.get(fromPos);
        boardMinions.remove(fromPos);
        boardMinions.add(toPos, save);
    }

    @Override
    public void removeMinion(int pos) {
        MinionPool.returnMinion(boardMinions.get(pos));
        boardMinions.remove(pos);
    }

    public BoardState getDeepCopyOfThis() {
        BoardState copy = new BoardState();
        for(int i = 0; i < boardMinions.size(); i++) {
            copy.getBoardMinions().add(new Minion(boardMinions.get(i)));
        }
        return copy;
    }

    @Override
    public LinkedList<Minion> getBoardMinions() {
        return boardMinions;
    }

    @Override
    public int getBoardSize() {
        return boardMinions.size();
    }

    @Override
    public Minion getBoardMinion(int pos) {
        return boardMinions.get(pos);
    }

    @Override
    public LinkedList<Minion> contains(String minionName) {
        LinkedList<Minion> result = new LinkedList<>();
        for (Minion m : boardMinions) {
            if(m.getName().equals(minionName)) result.add(m);
        }

        return result;
    }
}
