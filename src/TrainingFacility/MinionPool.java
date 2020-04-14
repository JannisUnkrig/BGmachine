package TrainingFacility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class MinionPool {

    private static int[] leftInPool;
    private static Minion[] vanillaMinions = null;
    private static int[] tierStarts = new int[7];      //tier 1 start at tierStarts[0], tier 2 at tierStarts[1], etc. Tokens start at tierStarts[6]

    private static LinkedList<Integer> beastIDs = new LinkedList<>();
    private static LinkedList<Integer> demonIDs = new LinkedList<>();
    private static LinkedList<Integer> dragonIDs = new LinkedList<>();
    private static LinkedList<Integer> mechIDs = new LinkedList<>();
    private static LinkedList<Integer> murlocIDs = new LinkedList<>();

    private static LinkedList<Integer> sneedsIDs = new LinkedList<>();
    private static LinkedList<Integer> coilerIDs = new LinkedList<>();
    private static LinkedList<Integer> pilotedShredderIDs = new LinkedList<>();

    private static int repMenId;
    private static int anModId;


    public static Minion getRandomMinionUpTo(int tavernTier) {

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

    public static Minion getRandomMinionOf(int tavernTier) {

        int absoluteNumberInPool = 0;
        for (int i = tierStarts[tavernTier - 1]; i < tierStarts[tavernTier]; i++) {
            absoluteNumberInPool += leftInPool[i];
        }
        int random = new Random().nextInt(absoluteNumberInPool) + 1;
        int randomMinionId = tierStarts[tavernTier - 1] - 1;
        for (int i = 0; random > 0; i++) {
            random -= leftInPool[i];
            randomMinionId++;
        }

        leftInPool[randomMinionId]--;
        return new Minion(vanillaMinions[randomMinionId]);
    }

    public static void returnMinion(Minion minion) {
        if(!minion.isGolden()) {
            leftInPool[minion.getId()]++;
        } else {
            leftInPool[minion.getId()] += 3;
        }
        if(minion.getReplicatingMenace() > 0) {
            leftInPool[repMenId] += minion.getReplicatingMenace();
        }
        if(minion.getGoldenReplicatingMenace() > 0) {
            leftInPool[repMenId] += 3 * minion.getGoldenAnnoyOModule();
        }
        if(minion.getAnnoyOModule() > 0) {
            leftInPool[anModId] += minion.getAnnoyOModule();
        }
        if(minion.getGoldenAnnoyOModule() > 0) {
            leftInPool[anModId] += 3 * minion.getGoldenAnnoyOModule();
        }
    }

    public static Minion generateMinion(String name) {
        for (int i = vanillaMinions.length - 1; i >= 0; i--) {
            if(vanillaMinions[i].getName().equals(name)) return new Minion(vanillaMinions[i]);
        }
        throw new RuntimeException();
    }

    public static Minion generateRandomTribeMinion(Tribe tribe, String exclude) {
        if (tribe == Tribe.BEAST) {
            for (;;) {
                Minion m = new Minion(vanillaMinions[beastIDs.get(new Random().nextInt(beastIDs.size()))]);
                if (!m.getName().equals(exclude)) return m;
            }
        }
        if (tribe == Tribe.DRAGON) {
            for (;;) {
                Minion m = new Minion(vanillaMinions[dragonIDs.get(new Random().nextInt(dragonIDs.size()))]);
                if (!m.getName().equals(exclude)) return m;
            }
        }
        if (tribe == Tribe.DEMON) {
            for (;;) {
                Minion m = new Minion(vanillaMinions[demonIDs.get(new Random().nextInt(demonIDs.size()))]);
                if (!m.getName().equals(exclude)) return m;
            }
        }
        if (tribe == Tribe.MECH) {
            for (;;) {
                Minion m = new Minion(vanillaMinions[mechIDs.get(new Random().nextInt(mechIDs.size()))]);
                if (!m.getName().equals(exclude)) return m;
            }
        }
        if (tribe == Tribe.MURLOC) {
            for (;;) {
                Minion m = new Minion(vanillaMinions[murlocIDs.get(new Random().nextInt(murlocIDs.size()))]);
                if (!m.getName().equals(exclude)) return m;
            }
        }
        return null;
    }

    public static Minion generateRandomPilotedShredderMinion() {
        return new Minion(vanillaMinions[pilotedShredderIDs.get(new Random().nextInt(pilotedShredderIDs.size()))]);
    }

    public static Minion generateRandomSneedsMinion() {
        return new Minion(vanillaMinions[sneedsIDs.get(new Random().nextInt(sneedsIDs.size()))]);
    }

    public static Minion generateRandomCoilerMinion() {
        return new Minion(vanillaMinions[coilerIDs.get(new Random().nextInt(coilerIDs.size()))]);
    }


    public static void extractMinionsFromFile() throws FileNotFoundException {
        //if (vanillaMinions != null) return;

        LinkedList<Minion> minionsLL = new LinkedList<>();
        Scanner scn = new Scanner(new File("C:\\Users\\Jannis\\IdeaProjects\\BGmachine\\src\\TrainingFacility\\minions.txt"));
        int id = 1;
        int curStars = 0;
        int cellToWriteIn = 0;

        while (scn.hasNextLine()){
            String line = scn.nextLine();
            if(!line.startsWith("/") && line.contains(";")) {
                String[] splitted = line.split(";");

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

                if(name.equals("Replicating Menace")) repMenId = id;
                if(name.equals("Annoy-O-Module")) anModId = id;

                if (tribe == Tribe.BEAST  || tribe == Tribe.ALL) beastIDs.add(id);
                if (tribe == Tribe.DEMON  || tribe == Tribe.ALL) demonIDs.add(id);
                if (tribe == Tribe.DRAGON || tribe == Tribe.ALL) dragonIDs.add(id);
                if (tribe == Tribe.MECH   || tribe == Tribe.ALL) mechIDs.add(id);
                if (tribe == Tribe.MURLOC || tribe == Tribe.ALL) murlocIDs.add(id);

                if (name.equals("Selfless Hero") || name.equals("Mecharoo") || name.equals("Fiendish Servant") || name.equals("Spawn of N'Zoth") ||
                        name.equals("Kindly Grandmother") || name.equals("Rat Pack") || name.equals("Harvest Golem") || name.equals("Kaboom Bot") ||
                        name.equals("Imprisoner") || name.equals("Unstable Ghoul") || name.equals("Infested Wolf") || name.equals("The Beast") ||
                        name.equals("Piloted Shredder") || name.equals("Replicating Menace") || name.equals("Mechano Egg") || name.equals("Goldrinn, the Great Wolf") ||
                        name.equals("Savannah Highmane") || name.equals("Voidlord") || name.equals("King Bagurgle") || name.equals("Sneed's Old Shredder") ||
                        name.equals("Kangor's Apprentice") || name.equals("Nadina the Red")) coilerIDs.add(id);
                if (name.equals("Old Murk-Eye") || name.equals("Waxrider Togwaggle") || name.equals("Khadgar") || name.equals("Shifter Zerus") ||
                        name.equals("The Beast") || name.equals("Bolvar, Fireblood") || name.equals("Baron Rivendare") || name.equals("Brann Bronzebeard") ||
                        name.equals("Goldrinn, the Great Wolf") || name.equals("King Bagurgle") || name.equals("Murozond") || name.equals("Mal'Ganis") ||
                        name.equals("Razorgore, the Untamed") || name.equals("Foe Reaper 4000") || name.equals("Kalecgos, Arcane Aspect") || name.equals("Maexxna") ||
                        name.equals("Nadina the Red") || name.equals("Zapp Slywick")) sneedsIDs.add(id);
                if (name.equals("Dire Wolf Alpha") || name.equals("Vulgar Homunculus") || name.equals("Micro Machine") || name.equals("Murloc Tidehunter") ||
                        name.equals("Rockpool Hunter") || name.equals("Dragonspawn Lieutenant") || name.equals("Kindly Grandmother") || name.equals("Scavenging Hyena") ||
                        name.equals("Unstable Ghoul") || name.equals("Khadgar")) pilotedShredderIDs.add(id);

                if(toAdd.getStars() != curStars) {
                    curStars = toAdd.getStars();
                    tierStarts[cellToWriteIn] = id;
                    if(cellToWriteIn <= 5) cellToWriteIn++;
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
