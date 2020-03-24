import java.util.LinkedList;

public interface IBoard {

    void playMinion(Minion minion, int pos);

    void moveMinion(int fromPos, int toPos);

    void removeMinion(int pos);

    LinkedList<Minion> getBoardMinions();

    int getBoardSize();

    Minion getBoardMinion(int pos);

    String toString();

}
