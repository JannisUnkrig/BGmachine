import java.util.Random;

public class Battler {

    private static Minion p1nextAttacker;
    private static Minion p2nextAttacker;

    private static Board p1b;
    private static Board p2b;

    public static void battle(Player p1, Player p2) {

        //wer zuerst?
        if(p1.getMyBoard().getBoardSize() < p2.getMyBoard().getBoardSize()) {
            Player p3 = p1;
            p1 = p2;
            p2 = p3;
        } else if(p1.getMyBoard().getBoardSize() == p2.getMyBoard().getBoardSize() && (new Random().nextInt(2) == 1)) {
            Player p3 = p1;
            p1 = p2;
            p2 = p3;
        }

        Game.appendToLeftTextArea("\n\n\nPlayer " + p1.getPlayerNr() + " vs. Player " + p2.getPlayerNr() + "\n\n");

        p1b = p1.getMyBoard().getDeepCopyOfThis();
        p2b = p2.getMyBoard().getDeepCopyOfThis();

        if (p1b.getBoardSize() > 0 && p2b.getBoardSize() > 0) {
            p1nextAttacker = p1b.getBoardMinion(0);
            p2nextAttacker = p2b.getBoardMinion(0);
        }

        //attacking
        while (p1b.getBoardSize() > 0 && p2b.getBoardSize() > 0 && !twiceOnlyEggsLeft()) {
            boolean sucessfullAttack1 = false;                              //necessary for 0 attack handling
            for (int i = 0; !sucessfullAttack1 && i < 8; i++) {
                Minion activeAttacker = p1nextAttacker;
                p1GoToNextAttackerInLine();
                sucessfullAttack1 = p1b.attack(1, activeAttacker, p2b);
            }

            if (p1b.getBoardSize() <= 0 || p2b.getBoardSize() <= 0) break;

            boolean sucessfullAttack2 = false;
            for (int i = 0; !sucessfullAttack2 && i < 8; i++) {
                Minion activeAttacker = p2nextAttacker;
                p2GoToNextAttackerInLine();
                sucessfullAttack2 = p2b.attack(2, activeAttacker, p1b);
            }
        }

        //dealing damage to loser
        if(p1b.getBoardSize() > 0 && p2b.getBoardSize() > 0) {
            Game.appendToLeftTextArea("Egg draw!");

        } else if(p1b.getBoardSize() > 0) {
            int damage = p1.getMyShop().getTavernTier();
            for (int i = 0; i < p1b.getBoardSize(); i++) {
                damage += p1b.getBoardMinion(i).getStars();
            }
            p2.reduceHealth(damage);
            Game.appendToLeftTextArea("Player " + p1.getPlayerNr() + " won!\nPlayer " + p2.getPlayerNr() + " will take " + damage + " damage");

        } else if(p2b.getBoardSize() > 0) {
            int damage = p2.getMyShop().getTavernTier();
            for (int i = 0; i < p2b.getBoardSize(); i++) {
                damage += p2b.getBoardMinion(i).getStars();
            }
            p1.reduceHealth(damage);
            Game.appendToLeftTextArea("Player " + p2.getPlayerNr() + " won!\nPlayer " + p1.getPlayerNr() + " will take " + damage + " damage");

        } else {
            Game.appendToLeftTextArea("It's a draw!");
        }


    }   //end of battle method

    //zwei eier leben -> draw
    private static boolean twiceOnlyEggsLeft() {
        for (int i = 0; i < p1b.getBoardSize(); i++) {
            if (p1b.getBoardMinion(i).getAttack() > 0) return false;
        }
        for (int i = 0; i < p2b.getBoardSize(); i++) {
            if (p2b.getBoardMinion(i).getAttack() > 0) return false;
        }
        return true;
    }


    public static void p1GoToNextAttackerInLine() {
        if(p1b.getBoardSize() <= 0) return;
        int nextIndex = p1b.getBoardMinions().indexOf(p1nextAttacker) + 1;
        if(nextIndex >= p1b.getBoardSize()) nextIndex = 0;
        p1nextAttacker = p1b.getBoardMinion(nextIndex);
    }
    public static void p2GoToNextAttackerInLine() {
        if(p2b.getBoardSize() <= 0) return;
        int nextIndex = p2b.getBoardMinions().indexOf(p2nextAttacker) + 1;
        if(nextIndex >= p2b.getBoardSize()) nextIndex = 0;
        p2nextAttacker = p2b.getBoardMinion(nextIndex);
    }


    //for deathrattles
    public static void setP1nextAttacker(Minion p1nextAttacker) {
        Battler.p1nextAttacker = p1nextAttacker;
    }
    public static void setP2nextAttacker(Minion p2nextAttacker) {
        Battler.p2nextAttacker = p2nextAttacker;
    }
}
