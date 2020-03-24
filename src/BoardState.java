import java.util.LinkedList;

public class BoardState implements IBoard {

    LinkedList<Minion> boardMinions = new LinkedList<>();



    @Override
    public void playMinion(Minion minion, int pos) {
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
    public String toString() {
        String result = "Board:\n\n";
        for (int i = 0; i < boardMinions.size(); i++) {
            result += boardMinions.get(i).toString() + "\n";
        }
        if(result.equals("Board:\n\n")) result += "empty :(\n";
        return result;
    }

}
