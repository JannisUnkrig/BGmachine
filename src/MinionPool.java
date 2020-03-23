public class MinionPool {

    private static int[] leftInPool = new int[100];


    //TODO
    public static Minion getRandomMinion(int tavernTier) {
        return null;
    }

    public static void returnMinion(Minion minion) {
        leftInPool[minion.getId()]++;
    }
}
