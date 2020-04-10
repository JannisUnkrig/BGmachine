package TrainingFacility;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Board implements IBoard {

    private AuraBoard myAuraBoard = new AuraBoard();
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
                        myAuraBoard.addHealthTo(getBoardMinion(targetedPos), 1);
                    } else {
                        getBoardMinion(targetedPos).addAttack(2);
                        myAuraBoard.addHealthTo(getBoardMinion(targetedPos), 2);
                    }
                }
            }
        }

        LinkedList<Minion> wrathWeavers = contains("Wrath Weaver");
        if (!wrathWeavers.isEmpty() && (minion.getTribe() == Tribe.DEMON || minion.getTribe() == Tribe.ALL)) {
            for (Minion ww : wrathWeavers) {
                if (!ww.isGolden()) {
                    ww.addAttack(2);
                    myAuraBoard.addHealthTo(ww, 2);
                    reducePlayerHealth(1);
                } else {
                    ww.addAttack(4);
                    myAuraBoard.addHealthTo(ww, 4);
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

        if(minion.getName().equals("Nathrezim Overseer")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            boolean hasDemon = false;
            for (Minion possiblyDemon : getBoardMinions()) {
                if (possiblyDemon.getTribe() == Tribe.DEMON || possiblyDemon.getTribe() == Tribe.ALL) hasDemon = true;
            }
            if (hasDemon) {
                for (int i = 0; i < howOften; i++) {
                    if (!minion.isGolden()) {
                        getBoardMinion(targetedPos).addAttack(2);
                        myAuraBoard.addHealthTo(getBoardMinion(targetedPos), 2);
                    } else {
                        getBoardMinion(targetedPos).addAttack(4);
                        myAuraBoard.addHealthTo(getBoardMinion(targetedPos), 4);
                    }
                }
            }
        }

        if(minion.getName().equals("Pogo-Hopper")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            for (int i = 0; i < howOften; i++) {
                if (!minion.isGolden()) {
                    minion.addAttack(2 * pogoCounter);
                    myAuraBoard.addHealthTo(minion, 2 * pogoCounter);
                } else {
                    minion.addAttack(4 * pogoCounter);
                    myAuraBoard.addHealthTo(minion, 4 * pogoCounter);
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
                           myAuraBoard.addHealthTo(luckyOne, 1);
                       } else {
                           luckyOne.addAttack(2);
                           myAuraBoard.addHealthTo(luckyOne, 2);
                       }
                   }
                    if (!dragons.isEmpty()) {
                        Minion luckyOne = dragons.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(1);
                            myAuraBoard.addHealthTo(luckyOne, 1);
                        } else {
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        }
                    }
                    if (!murlocs.isEmpty()) {
                        Minion luckyOne = murlocs.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(1);
                            myAuraBoard.addHealthTo(luckyOne, 1);
                        } else {
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        }
                    }
                    if (!alls.isEmpty()) {
                        Minion luckyOne = alls.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(1);
                            myAuraBoard.addHealthTo(luckyOne, 1);
                        } else {
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        }
                    }
                } else {
                    //might be incorrect since this favors minions in a tribe with a small number on board
                    int ignoredTribe = new Random().nextInt(4);
                    if (ignoredTribe != 0) {
                        Minion luckyOne = beasts.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(1);
                            myAuraBoard.addHealthTo(luckyOne, 1);
                        } else {
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        }
                    }
                    if (ignoredTribe != 1) {
                        Minion luckyOne = dragons.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(1);
                            myAuraBoard.addHealthTo(luckyOne, 1);
                        } else {
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        }
                    }
                    if (ignoredTribe != 2) {
                        Minion luckyOne = murlocs.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(1);
                            myAuraBoard.addHealthTo(luckyOne, 1);
                        } else {
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        }
                    }
                    if (ignoredTribe != 3) {
                        Minion luckyOne = alls.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(1);
                            myAuraBoard.addHealthTo(luckyOne, 1);
                        } else {
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        }
                    }
                }
            }
        }

        if(minion.getName().equals("Coldlight Seer")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            for (int i = 0; i < howOften; i++) {
                for (Minion ideallyMurloc : getBoardMinions()) {
                    if (ideallyMurloc.getTribe() == Tribe.MURLOC || ideallyMurloc.getTribe() == Tribe.ALL) {
                        if (!minion.isGolden()) {
                            myAuraBoard.addHealthTo(ideallyMurloc, 2);
                        } else {
                            myAuraBoard.addHealthTo(ideallyMurloc, 4);
                        }
                    }
                }
            }
        }

        if(minion.getName().equals("Crystalweaver")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            for (int i = 0; i < howOften; i++) {
                for (Minion ideallyDemon : getBoardMinions()) {
                    if (ideallyDemon.getTribe() == Tribe.DEMON || ideallyDemon.getTribe() == Tribe.ALL) {
                        if (!minion.isGolden()) {
                            ideallyDemon.addAttack(1);
                            myAuraBoard.addHealthTo(ideallyDemon, 1);
                        } else {
                            ideallyDemon.addAttack(2);
                            myAuraBoard.addHealthTo(ideallyDemon, 2);
                        }
                    }
                }
            }
        }

        if(minion.getName().equals("Felfin Navigator")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            for (int i = 0; i < howOften; i++) {
                for (Minion ideallyMurloc : getBoardMinions()) {
                    if (ideallyMurloc.getTribe() == Tribe.MURLOC || ideallyMurloc.getTribe() == Tribe.ALL) {
                        if (!minion.isGolden()) {
                            ideallyMurloc.addAttack(1);
                            myAuraBoard.addHealthTo(ideallyMurloc, 1);
                        } else {
                            ideallyMurloc.addAttack(2);
                            myAuraBoard.addHealthTo(ideallyMurloc, 2);
                        }
                    }
                }
            }
        }

        if(minion.getName().equals("Houndmaster")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            boolean hasBeast = false;
            for (Minion possiblyBeast : getBoardMinions()) {
                if (possiblyBeast.getTribe() == Tribe.BEAST || possiblyBeast.getTribe() == Tribe.ALL) hasBeast = true;
            }
            if (hasBeast) {
                for (int i = 0; i < howOften; i++) {
                    getBoardMinion(targetedPos).setTaunt(true);
                    if (!minion.isGolden()) {
                        getBoardMinion(targetedPos).addAttack(2);
                        myAuraBoard.addHealthTo(getBoardMinion(targetedPos), 2);
                    } else {
                        getBoardMinion(targetedPos).addAttack(4);
                        myAuraBoard.addHealthTo(getBoardMinion(targetedPos), 4);
                    }
                }
            }
        }

        if(minion.getName().equals("Screwjank Clunker")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            boolean hasMech = false;
            for (Minion possiblyMech : getBoardMinions()) {
                if (possiblyMech.getTribe() == Tribe.MECH || possiblyMech.getTribe() == Tribe.ALL) hasMech = true;
            }
            if (hasMech) {
                for (int i = 0; i < howOften; i++) {
                    if (!minion.isGolden()) {
                        getBoardMinion(targetedPos).addAttack(2);
                        myAuraBoard.addHealthTo(getBoardMinion(targetedPos), 2);
                    } else {
                        getBoardMinion(targetedPos).addAttack(4);
                        myAuraBoard.addHealthTo(getBoardMinion(targetedPos), 4);
                    }
                }
            }
        }

        if(minion.getName().equals("Twilight Emissary")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            boolean hasDragon = false;
            for (Minion possiblyDragon : getBoardMinions()) {
                if (possiblyDragon.getTribe() == Tribe.DRAGON || possiblyDragon.getTribe() == Tribe.ALL) hasDragon = true;
            }
            if (hasDragon) {
                for (int i = 0; i < howOften; i++) {
                    if (!minion.isGolden()) {
                        getBoardMinion(targetedPos).addAttack(2);
                        myAuraBoard.addHealthTo(getBoardMinion(targetedPos), 2);
                    } else {
                        getBoardMinion(targetedPos).addAttack(4);
                        myAuraBoard.addHealthTo(getBoardMinion(targetedPos), 4);
                    }
                }
            }
        }

        if(minion.getName().equals("Defender of Argus")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            boolean hasLeft = false;
            boolean hasRight = false;
            if (pos - 1 >= 0) hasLeft = true;
            if (pos < getBoardSize()) hasRight = true;

            for (int i = 0; i < howOften; i++) {
                if (hasLeft) {
                    getBoardMinion(pos - 1).setTaunt(true);
                    if (!minion.isGolden()) {
                        getBoardMinion(pos - 1).addAttack(1);
                        myAuraBoard.addHealthTo(getBoardMinion(pos - 1), 1);
                    } else {
                        getBoardMinion(pos - 1).addAttack(2);
                        myAuraBoard.addHealthTo(getBoardMinion(pos - 1), 2);
                    }
                }
                if (hasRight) {
                    getBoardMinion(pos).setTaunt(true);
                    if (!minion.isGolden()) {
                        getBoardMinion(pos).addAttack(1);
                        myAuraBoard.addHealthTo(getBoardMinion(pos), 1);
                    } else {
                        getBoardMinion(pos).addAttack(2);
                        myAuraBoard.addHealthTo(getBoardMinion(pos), 2);
                    }
                }
            }
        }

        if(minion.getName().equals("Menagerie Magician")) {
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
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        } else {
                            luckyOne.addAttack(4);
                            myAuraBoard.addHealthTo(luckyOne, 4);
                        }
                    }
                    if (!dragons.isEmpty()) {
                        Minion luckyOne = dragons.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        } else {
                            luckyOne.addAttack(4);
                            myAuraBoard.addHealthTo(luckyOne, 4);
                        }
                    }
                    if (!murlocs.isEmpty()) {
                        Minion luckyOne = murlocs.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        } else {
                            luckyOne.addAttack(4);
                            myAuraBoard.addHealthTo(luckyOne, 4);
                        }
                    }
                    if (!alls.isEmpty()) {
                        Minion luckyOne = alls.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        } else {
                            luckyOne.addAttack(4);
                            myAuraBoard.addHealthTo(luckyOne, 4);
                        }
                    }
                } else {
                    //might be incorrect since this favors minions in a tribe with a small number on board
                    int ignoredTribe = new Random().nextInt(4);
                    if (ignoredTribe != 0) {
                        Minion luckyOne = beasts.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        } else {
                            luckyOne.addAttack(4);
                            myAuraBoard.addHealthTo(luckyOne, 4);
                        }
                    }
                    if (ignoredTribe != 1) {
                        Minion luckyOne = dragons.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        } else {
                            luckyOne.addAttack(4);
                            myAuraBoard.addHealthTo(luckyOne, 4);
                        }
                    }
                    if (ignoredTribe != 2) {
                        Minion luckyOne = murlocs.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        } else {
                            luckyOne.addAttack(4);
                            myAuraBoard.addHealthTo(luckyOne, 4);
                        }
                    }
                    if (ignoredTribe != 3) {
                        Minion luckyOne = alls.get(0);
                        if (!minion.isGolden()) {
                            luckyOne.addAttack(2);
                            myAuraBoard.addHealthTo(luckyOne, 2);
                        } else {
                            luckyOne.addAttack(4);
                            myAuraBoard.addHealthTo(luckyOne, 4);
                        }
                    }
                }
            }
        }

        if(minion.getName().equals("Toxfin")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            boolean hasMurloc = false;
            for (Minion possiblyMurloc : getBoardMinions()) {
                if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) hasMurloc = true;
            }
            if (hasMurloc) {
                for (int i = 0; i < howOften; i++) {
                    getBoardMinion(targetedPos).setPoisonous(true);
                }
            }
        }

        if(minion.getName().equals("Virmen Sensei")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            boolean hasBeast = false;
            for (Minion possiblyBeast : getBoardMinions()) {
                if (possiblyBeast.getTribe() == Tribe.BEAST || possiblyBeast.getTribe() == Tribe.ALL) hasBeast = true;
            }
            if (hasBeast) {
                for (int i = 0; i < howOften; i++) {
                    if (!minion.isGolden()) {
                        getBoardMinion(targetedPos).addAttack(2);
                        myAuraBoard.addHealthTo(getBoardMinion(targetedPos), 2);
                    } else {
                        getBoardMinion(targetedPos).addAttack(4);
                        myAuraBoard.addHealthTo(getBoardMinion(targetedPos), 4);
                    }
                }
            }
        }

        if(minion.getName().equals("Annihilan Battlemaster")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            for (int i = 0; i < howOften; i++) {
                //TODO exception for patchwerk
                myAuraBoard.addHealthTo(minion, (40 - myPlayer.getHealth()));
            }
        }

        if(minion.getName().equals("King Bagurgle")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            for (int i = 0; i < howOften; i++) {
                for (Minion ideallyMurloc : getBoardMinions()) {
                    if (ideallyMurloc.getTribe() == Tribe.MURLOC || ideallyMurloc.getTribe() == Tribe.ALL) {
                        if (!minion.isGolden()) {
                            ideallyMurloc.addAttack(2);
                            myAuraBoard.addHealthTo(ideallyMurloc, 2);
                        } else {
                            ideallyMurloc.addAttack(4);
                            myAuraBoard.addHealthTo(ideallyMurloc, 4);
                        }
                    }
                }
            }
        }

        if(minion.getName().equals("Primalfin Lookout")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            boolean hasMurloc = false;
            for (Minion ideallyMurloc : getBoardMinions()) {
                if (ideallyMurloc.getTribe() == Tribe.MURLOC || ideallyMurloc.getTribe() == Tribe.ALL) hasMurloc = true;
            }
            if (hasMurloc) {
                for (int i = 0; i < howOften; i++) {
                    //TODO find out if all murlocs are equally likely. does draw from pool and can go below 0 in pool
                }
            }
        }

        if(minion.getName().equals("Strongshell Scavenger")) {
            int howOften = getBattlecryMultiplierAndTriggerLovers();
            for (int i = 0; i < howOften; i++) {
                for (Minion ideallyTaunted : getBoardMinions()) {
                    if (ideallyTaunted.isTaunt()) {
                        if (!minion.isGolden()) {
                            ideallyTaunted.addAttack(2);
                            myAuraBoard.addHealthTo(ideallyTaunted, 2);
                        } else {
                            ideallyTaunted.addAttack(4);
                            myAuraBoard.addHealthTo(ideallyTaunted, 4);
                        }
                    }
                }
            }
        }

        myAuraBoard.playMinion(minion, pos, -1);
    }

    @Override
    public void moveMinion(int fromPos, int toPos) {
        myAuraBoard.moveMinion(fromPos, toPos);
    }

    @Override
    public int removeMinion(int pos) {
        myAuraBoard.removeMinion(pos);
        return 1;
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

        myAuraBoard.playMinion(minion, pos, -1);

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
                    myAuraBoard.addHealthTo(cf, 1);
                } else {
                    cf.addAttack(2);
                    myAuraBoard.addHealthTo(cf, 2);
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
                    myAuraBoard.addHealthTo(ideallyDragon, buffs);
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
        return myAuraBoard.getBoardMinions();
    }

    @Override
    public int getBoardSize() {
        return myAuraBoard.getBoardSize();
    }

    @Override
    public Minion getBoardMinion(int pos) {
        return myAuraBoard.getBoardMinion(pos);
    }

    public Minion getBoardMinionForDisplay(int pos) {
        Minion m = new Minion(myAuraBoard.getBoardMinion(pos));

        //handling murk-eye
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
        return myAuraBoard.contains(minionName);
    }

    public AuraBoard getMyAuraBoard() {
        return myAuraBoard;
    }

}
