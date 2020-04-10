package TrainingFacility;

import java.util.LinkedList;

public interface IBoard {

    void playMinion(Minion minion, int pos, int targetedPos);

    void moveMinion(int fromPos, int toPos);

    int removeMinion(int pos);

    LinkedList<Minion> getBoardMinions();

    int getBoardSize();

    Minion getBoardMinion(int pos);

    LinkedList<Minion> contains(String minionName);

}
