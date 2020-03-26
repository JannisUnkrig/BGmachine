import java.util.LinkedList;
import java.util.Random;

public class Board implements IBoard {

    private BoardState myBoardState = new BoardState();


    public Board() {}

    public Board(BoardState bs) {
        this.myBoardState = bs;
    }

    //build up phase
    @Override
    public void playMinion(Minion minion, int pos) {
        //TODO battlecries, board dependent effects(old murkeye, etc.)
        if(minion.getName().equals("Alleycat") && getBoardSize() <= 5) {
            summonMinion(MinionPool.generateMinion("Tabbycat"), pos);
        }
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


    //both phases
    public void summonMinion(Minion minion, int pos) {
        myBoardState.playMinion(minion, pos);
    }


    //battle phase
    public boolean attack(int iAmBoardNr, Minion attacker, Board opponentsBoard) {
        if(attacker.getAttack() <= 0) return false;

        int howOften = 1;
        if(attacker.isWindfury()) howOften = 2;
        if(attacker.getName().equals("Zapp Slywick") && attacker.isGolden()) howOften = 4;

        for (int i = 0; i < howOften && (getBoardMinions().contains(attacker)); i++) {

            LinkedList<Integer> indexesOfOpposingTauntMinions = new LinkedList<>();
            for (int j = 0; j < opponentsBoard.getBoardSize(); j++) {
                if(opponentsBoard.getBoardMinion(j).isTaunt()) indexesOfOpposingTauntMinions.add(j);
            }

            int targetIndex;
            if (indexesOfOpposingTauntMinions.isEmpty()) {
                targetIndex = new Random().nextInt(opponentsBoard.getBoardSize());
            } else {
                targetIndex = indexesOfOpposingTauntMinions.get(new Random().nextInt(indexesOfOpposingTauntMinions.size()));
            }

            Minion target = opponentsBoard.getBoardMinion(targetIndex);

            Game.appendToLeftTextArea(attacker.toString() + "  -->  " + target.toString() + "\n");

            if (!attacker.isDivineShield()) {
                attacker.reduceHealth(target.getAttack());
                if (attacker.getHealth() <= 0 || (target.isPoisonous() && target.getAttack() > 0)) {
                    killMinion(iAmBoardNr, attacker);
                }
            } else {
                if(target.getAttack() > 0) attacker.setDivineShield(false);
            }

            if (!target.isDivineShield()) {
                target.reduceHealth(attacker.getAttack());
                if (target.getHealth() <= 0 || (attacker.isPoisonous() && attacker.getAttack() > 0)) {
                    opponentsBoard.killMinion(iAmBoardNr, target);
                }
            } else {
                if(attacker.getAttack() > 0) target.setDivineShield(false);
            }
        }
        return true;
    }

    public void killMinion(int iAmBoardNr, Minion minion) {
        //TODO deathrattles, board dependent stuff
        Game.appendToLeftTextArea(minion.toString() + " died\n");
        if(iAmBoardNr == 1) Battler.p1GoToNextAttackerInLine();
        if(iAmBoardNr == 2) Battler.p2GoToNextAttackerInLine();
        int savePos = getBoardMinions().indexOf(minion);
        getBoardMinions().remove(minion);

        if (minion.getName().equals("Harvest Golem")) {
            Minion damagedGolem = MinionPool.generateMinion("Damaged Golem");
            summonMinion(damagedGolem, savePos);
            if(iAmBoardNr == 1) Battler.setP1nextAttacker(damagedGolem);
            if(iAmBoardNr == 2) Battler.setP2nextAttacker(damagedGolem);
            Game.appendToLeftTextArea("Damaged Golem summoned\n");
        }
        if (minion.getName().equals("Bronze Warden")) {
            Minion bwr = MinionPool.generateMinion("Bronze Warden Reborn");
            myBoardState.playMinion(bwr, savePos);
            if(iAmBoardNr == 1) Battler.setP1nextAttacker(bwr);
            if(iAmBoardNr == 2) Battler.setP2nextAttacker(bwr);
            Game.appendToLeftTextArea("Bronze Warden resurrected\n");
        }
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


    public Board getDeepCopyOfThis() {
        return new Board(myBoardState.getDeepCopyOfThis());
    }
}
