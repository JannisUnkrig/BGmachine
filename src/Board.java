import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Board implements IBoard {

    private BoardState myBoardState = new BoardState();
    private Player myPlayer;
    private int pogoCounter = 0;


    public Board(Player myPlayer) {
        this.myPlayer = myPlayer;
    }


    //build up phase
    @Override
    public void playMinion(Minion minion, int pos, int targetedPos) {
        //TODO battlecries
        if(minion.getName().equals("Alleycat")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            for (int i = 0; i < howOften; i++) {
                Minion tabby = MinionPool.generateMinion("Tabbycat");
                if (minion.isGolden()) {
                    tabby.setGolden(true);
                    tabby.setAttack(2);
                    tabby.setHealth(2);
                }
                summonMinion(tabby, pos);
            }
        }

        if(minion.getName().equals("Vulgar Homunculus")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            for (int i = 0; i < howOften; i++) {
                reducePlayerHealth(2);
            }
        }

        LinkedList<Minion> murlocTidecallers = contains("Murloc Tidecaller");
        if (!murlocTidecallers.isEmpty() && (minion.getTribe() == Tribe.MURLOC || minion.getTribe() == Tribe.ALL)) {
            for (Minion mtc : murlocTidecallers) {
                if (!mtc.isGolden()) {
                    mtc.addAttack(1);
                } else {
                    mtc.addAttack(2);
                }
            }
        }

        if(minion.getName().equals("Murloc Tidehunter")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            for (int i = 0; i < howOften; i++) {
                Minion scout = MinionPool.generateMinion("Murloc Scout");
                if (minion.isGolden()) {
                    scout.setGolden(true);
                    scout.setAttack(2);
                    scout.setHealth(2);
                }
                summonMinion(scout, pos);
            }
        }

        if(minion.getName().equals("Rockpool Hunter")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            boolean hasMurloc = false;
            for (Minion possiblyMurloc : getBoardMinions()) {
                if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) hasMurloc = true;
            }
            if (hasMurloc) {
                for (int i = 0; i < howOften; i++) {
                    if (!minion.isGolden()) {
                        getBoardMinion(targetedPos).addAttack(1);
                        getBoardMinion(targetedPos).addHealth(1);
                    } else {
                        getBoardMinion(targetedPos).addAttack(2);
                        getBoardMinion(targetedPos).addHealth(2);
                    }
                }
            }
        }

        LinkedList<Minion> wrathWeavers = contains("Wrath Weaver");
        if (!wrathWeavers.isEmpty() && (minion.getTribe() == Tribe.DEMON || minion.getTribe() == Tribe.ALL)) {
            for (Minion ww : wrathWeavers) {
                if (!ww.isGolden()) {
                    ww.addAttack(2);
                    ww.addHealth(2);
                    reducePlayerHealth(1);
                } else {
                    ww.addAttack(4);
                    ww.addHealth(4);
                    reducePlayerHealth(1);
                }
            }
        }

        if(minion.getName().equals("Metaltooth Leaper")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            for (int i = 0; i < howOften; i++) {
                for (Minion ideallyMech : getBoardMinions()) {
                    if (ideallyMech.getTribe() == Tribe.MECH || ideallyMech.getTribe() == Tribe.ALL) {
                        if (!minion.isGolden()) {
                            ideallyMech.addAttack(2);
                        } else {
                            ideallyMech.addAttack(4);
                        }
                    }
                }
            }
        }

        if(minion.getName().equals("Pogo-Hopper")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            for (int i = 0; i < howOften; i++) {
                if (!minion.isGolden()) {
                    minion.addAttack(2 * pogoCounter);
                    minion.addHealth(2 * pogoCounter);
                } else {
                    minion.addAttack(4 * pogoCounter);
                    minion.addHealth(4 * pogoCounter);
                }
            }
            pogoCounter++;
        }

        if(minion.getName().equals("Zoobot")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            for (int i = 0; i < howOften; i++) {
                LinkedList<Minion> board = getBoardMinions();
                LinkedList<Minion> beasts = new LinkedList<>();
                LinkedList<Minion> dragons = new LinkedList<>();
                LinkedList<Minion> murlocs = new LinkedList<>();
                LinkedList<Minion> alls = new LinkedList<>();

                for (Minion tribedMinion : board) {
                    if (tribedMinion.getTribe() == Tribe.BEAST) {
                        beasts.add(tribedMinion);
                    }
                    else if (tribedMinion.getTribe() == Tribe.DRAGON) {
                        dragons.add(tribedMinion);
                    }
                    else if (tribedMinion.getTribe() == Tribe.MURLOC) {
                        murlocs.add(tribedMinion);
                    }
                    else if (tribedMinion.getTribe() == Tribe.ALL) {
                        alls.add(tribedMinion);
                    }
                }

                Collections.shuffle(beasts);
                Collections.shuffle(dragons);
                Collections.shuffle(murlocs);
                Collections.shuffle(alls);

                if (beasts.isEmpty() || dragons.isEmpty() || murlocs.isEmpty() || alls.isEmpty()) {
                   if (!beasts.isEmpty()) {
                       Minion luckyOne = beasts.get(0);
                       if (!minion.isGolden()) {
                           luckyOne.addAttack(1);
                           luckyOne.addHealth(1);
                       } else {
                           luckyOne.addAttack(2);
                           luckyOne.addHealth(2);
                       }
                   }
                    if (!dragons.isEmpty()) {
                        Minion luckyOne = dragons.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(1);
                            luckyOne.addHealth(1);
                        } else {
                            luckyOne.addAttack(2);
                            luckyOne.addHealth(2);
                        }
                    }
                    if (!murlocs.isEmpty()) {
                        Minion luckyOne = murlocs.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(1);
                            luckyOne.addHealth(1);
                        } else {
                            luckyOne.addAttack(2);
                            luckyOne.addHealth(2);
                        }
                    }
                    if (!alls.isEmpty()) {
                        Minion luckyOne = alls.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(1);
                            luckyOne.addHealth(1);
                        } else {
                            luckyOne.addAttack(2);
                            luckyOne.addHealth(2);
                        }
                    }
                } else {
                    //might be incorrect since this favors minions in a tribe with a small number on board
                    int ignoredTribe = new Random().nextInt(4);
                    if (ignoredTribe != 0) {
                        Minion luckyOne = beasts.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(1);
                            luckyOne.addHealth(1);
                        } else {
                            luckyOne.addAttack(2);
                            luckyOne.addHealth(2);
                        }
                    }
                    if (ignoredTribe != 1) {
                        Minion luckyOne = dragons.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(1);
                            luckyOne.addHealth(1);
                        } else {
                            luckyOne.addAttack(2);
                            luckyOne.addHealth(2);
                        }
                    }
                    if (ignoredTribe != 2) {
                        Minion luckyOne = murlocs.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(1);
                            luckyOne.addHealth(1);
                        } else {
                            luckyOne.addAttack(2);
                            luckyOne.addHealth(2);
                        }
                    }
                    if (ignoredTribe != 3) {
                        Minion luckyOne = alls.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(1);
                            luckyOne.addHealth(1);
                        } else {
                            luckyOne.addAttack(2);
                            luckyOne.addHealth(2);
                        }
                    }
                }
            }
        }

        myBoardState.playMinion(minion, pos, -1);
    }

    @Override
    public void moveMinion(int fromPos, int toPos) {
        myBoardState.moveMinion(fromPos, toPos);
    }

    @Override
    public void removeMinion(int pos) {
        myBoardState.removeMinion(pos);
    }


    public void triggerStartOfTurnEffects() {

        LinkedList<Minion> microMachines = contains("Micro Machine");
        if (!microMachines.isEmpty()) {
            for (Minion mm : microMachines) {
                if (!mm.isGolden()) {
                    mm.addAttack(1);
                } else {
                    mm.addAttack(2);
                }
            }
        }

    }

    public void summonMinion(Minion minion, int pos) {
        if (getBoardSize() >= 6) return;                //leave some space for the actual minion

        LinkedList<Minion> murlocTidecallers = contains("Murloc Tidecaller");
        if (!murlocTidecallers.isEmpty() && (minion.getTribe() == Tribe.MURLOC || minion.getTribe() == Tribe.ALL)) {
            for (Minion mtc : murlocTidecallers) {
                if (!mtc.isGolden()) {
                    mtc.addAttack(1);
                } else {
                    mtc.addAttack(2);
                }
            }
        }

        myBoardState.playMinion(minion, pos, -1);

        myPlayer.checkForTriple(minion);
    }

    private void reducePlayerHealth(int by) {
        myPlayer.reduceHealth(by);
    }

    private int getBattlecryMultiplierAndTriggerLovers() {

        LinkedList<Minion> crowdFavorites = contains("Crowd Favorite");
        if (!crowdFavorites.isEmpty()) {
            for (Minion cf : crowdFavorites) {
                if (!cf.isGolden()) {
                    cf.addAttack(1);
                    cf.addHealth(1);
                } else {
                    cf.addAttack(2);
                    cf.addHealth(2);
                }
            }
        }

        LinkedList<Minion> kalecgoses = contains("Kalecgos, Arcane Aspect");
        if (!kalecgoses.isEmpty()) {
            int buffs = 0;
            for (Minion kalecgos : kalecgoses) {
                if (!kalecgos.isGolden()) {
                    buffs++;
                } else {
                    buffs += 2;
                }
            }
            for (Minion ideallyDragon : getBoardMinions()) {
                if (ideallyDragon.getTribe() == Tribe.DRAGON || ideallyDragon.getTribe() == Tribe.ALL) {
                    ideallyDragon.addAttack(buffs);
                    ideallyDragon.addHealth(buffs);
                }
            }
        }

        LinkedList<Minion> branns = contains("Brann Bronzebeard");
        if (!branns.isEmpty()) {
            boolean golden = false;
            for (Minion brann : branns) {
                if (brann.isGolden()) golden = true;
            }
            if (!golden) {
                return 2;
            } else {
                return 3;
            }
        }
        return 1;
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

    public Minion getBoardMinionForDisplay(int pos) {
        Minion m = new Minion(myBoardState.getBoardMinion(pos));

        //handling aura effects
        LinkedList<Minion> murlocWarleaders = contains("Murloc Warleader");
        if (!murlocWarleaders.isEmpty() && (m.getTribe() == Tribe.MURLOC || m.getTribe() == Tribe.ALL)) {
            int buffs = 0;
            for (Minion mwl : murlocWarleaders) {
                if (myBoardState.getBoardMinions().indexOf(mwl) != pos) {
                    if (!mwl.isGolden()) {
                        buffs += 2;
                    } else {
                        buffs += 4;
                    }
                }
            }
            m.addAttack(buffs);
        }

        if(m.getName().equals("Old Murk-Eye")) {
            int murlocCounter = -1;                 //-1 because only other murlocs count
            for (Minion possiblyMurloc : getBoardMinions()) {
                if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) {
                    murlocCounter++;
                }
            }
            LinkedList<Minion> shopOffers = myPlayer.getMyShop().getOffers();
            for (Minion possiblyMurloc : shopOffers) {
                if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) {
                    murlocCounter++;
                }
            }
            if (!m.isGolden()) {
                m.addAttack(murlocCounter);
            } else {
                m.addAttack(murlocCounter * 2);
            }
        }

        if (pos - 1 >= 0) {
            Minion possiblyDWA = getBoardMinion(pos - 1);
            if(possiblyDWA.getName().equals("Dire Wolf Alpha")){
                if (!possiblyDWA.isGolden()) {
                    m.addAttack(1);
                } else {
                    m.addAttack(2);
                }
            }
        }
        if (pos + 1 < getBoardSize()) {
            Minion possiblyDWA = getBoardMinion(pos + 1);
            if(possiblyDWA.getName().equals("Dire Wolf Alpha")){
                if (!possiblyDWA.isGolden()) {
                    m.addAttack(1);
                } else {
                    m.addAttack(2);
                }
            }
        }

        return m;
    }

    public String toString() {
        String result = "Board:\n\n";
        for (int i = 0; i < getBoardSize(); i++) {
            result += getBoardMinionForDisplay(i).toString() + "\n";
        }
        if(result.equals("Board:\n\n")) result += "empty :(\n";
        return result;
    }

    @Override
    public LinkedList<Minion> contains(String minionName) {
        return myBoardState.contains(minionName);
    }

    public BoardState getMyBoardState() {
        return myBoardState;
    }

}
