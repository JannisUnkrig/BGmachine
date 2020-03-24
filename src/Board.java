import java.util.LinkedList;

public class Board implements IBoard {

    private BoardState myBoardState = new BoardState();



    @Override
    public void playMinion(Minion minion, int pos) {
        //TODO battlecries, board dependent effects(old murkeye, etc.)
        myBoardState.playMinion(minion, pos);
    }

    @Override
    public void moveMinion(int fromPos, int toPos) {
        //TODO board dependent effects(dire wolf alpha, etc.)
        myBoardState.moveMinion(fromPos, toPos);
    }

    @Override
    public void removeMinion(int pos) {
        //TODO except triples
        myBoardState.removeMinion(pos);
    }

    @Override
    public LinkedList<Minion> getBoardMinions() {
        return myBoardState.getBoardMinions();
    }

    @Override
    public int getBoardSize() {
        return myBoardState.getBoardSize();
    }

    @Override
    public Minion getBoardMinion(int pos) {
        return myBoardState.getBoardMinion(pos);
    }

    @Override
    public String toString() {
        return myBoardState.toString();
    }

    public BoardState getMyBoardState() {
        return myBoardState;
    }

}
