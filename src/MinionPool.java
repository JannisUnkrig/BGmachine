import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class MinionPool {

    private static int[] leftInPool;
    private static Minion[] vanillaMinions;
    private static int[] tierStarts = new int[7];      //tier 1 start at tierStarts[0], tier 2 at tierStarts[1], etc. Tokens start at tierStarts[6]


    public static Minion getRandomMinion(int tavernTier) {

        int absoluteNumberInPool = 0;
        for (int i = 0; i < tierStarts[tavernTier]; i++) {
            absoluteNumberInPool += leftInPool[i];
        }
        int random = new Random().nextInt(absoluteNumberInPool) + 1;
        int randomMinionId = -1;
        for (int i = 0; random > 0; i++) {
            random -= leftInPool[i];
            randomMinionId++;
        }

        leftInPool[randomMinionId]--;
        return new Minion(vanillaMinions[randomMinionId]);
    }

    public static void returnMinion(Minion minion) {
        leftInPool[minion.getId()]++;

    }


    public static void extractMinionsFromFile() throws FileNotFoundException {
        LinkedList<Minion> minionsLL = new LinkedList<>();
        Scanner scn = new Scanner(new File("C:\\Users\\Jannis\\IdeaProjects\\BGmachine\\src\\minions.txt"));
        int id = 0;
        int curStars = 0;
        int cellToWriteIn = 0;

        while (scn.hasNextLine()){
            String line = scn.nextLine();
            if(line.contains(",")) {
                String[] splitted = line.split(",");

                String name = splitted[0];
                int stars = Integer.parseInt(splitted[1]);
                int attack = Integer.parseInt(splitted[2]);
                int health = Integer.parseInt(splitted[3]);
                Tribe tribe = Tribe.parseTribe(splitted[4]);
                boolean taunt = Boolean.parseBoolean(splitted[5]);
                boolean divineShield = Boolean.parseBoolean(splitted[6]);
                boolean poisonous = Boolean.parseBoolean(splitted[7]);
                boolean windfury = Boolean.parseBoolean(splitted[8]);

                Minion toAdd = new Minion(  id, name, stars, attack, health, tribe, false, taunt, divineShield, poisonous, windfury);
                minionsLL.add(toAdd);

                if(toAdd.getStars() != curStars) {
                    curStars = toAdd.getStars();
                    tierStarts[cellToWriteIn] = id;
                    cellToWriteIn++;
                }
                id++;
            }
        }
        vanillaMinions = new Minion[minionsLL.size()];
        vanillaMinions = minionsLL.toArray(vanillaMinions);

        leftInPool = new int[minionsLL.size()];
        for (int i = tierStarts[0]; i < tierStarts[1]; i++) leftInPool[i] = 16;
        for (int i = tierStarts[1]; i < tierStarts[2]; i++) leftInPool[i] = 15;
        for (int i = tierStarts[2]; i < tierStarts[3]; i++) leftInPool[i] = 13;
        for (int i = tierStarts[3]; i < tierStarts[4]; i++) leftInPool[i] = 11;
        for (int i = tierStarts[4]; i < tierStarts[5]; i++) leftInPool[i] = 9;
        for (int i = tierStarts[5]; i < tierStarts[6]; i++) leftInPool[i] = 7;
        //danach nur noch tokens
    }

}
