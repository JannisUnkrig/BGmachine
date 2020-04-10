package TrainingFacility;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Battler {

    private static Minion p1nextAttacker;
    private static Minion p2nextAttacker;
    private static Minion p1activeAttacker;
    private static Minion p2activeAttacker;

    private static AuraBoard p1b;
    private static AuraBoard p2b;

    private static LinkedList<Player> nextMatchups;
    private static LinkedList<Player> curRanking;

    private static LinkedList<Player> deathsLastTurn;
    private static int nextDeathWillBeRank;

    public static void battleAll(Player[] players) {

        for (int i = 0; i < nextMatchups.size(); i += 2) {
            battle(nextMatchups.get(i), nextMatchups.get(i + 1));
        }

        generateMatchups(players);
    }

    /** sets {@link #nextMatchups} */
    public static void generateMatchups(Player[] players) {
        int alivePlayers = 8;
        for (int i = 0; i < 8; i++) {
            if (curRanking.get(i).getDefeatedAsPlace() > 1) {
                alivePlayers = i;
                break;
            }
        }

        LinkedList<Player> shuffleable = new LinkedList<>();
        Collections.addAll(shuffleable, players);

        if (alivePlayers <= 2) {
            shuffleable.removeIf(p2 -> p2.getDefeatedAsPlace() == 3 || p2.getDefeatedAsPlace() == 4);
        }
        if (alivePlayers <= 4) {
            shuffleable.removeIf(p2 -> p2.getDefeatedAsPlace() == 5 || p2.getDefeatedAsPlace() == 6);
        }
        if (alivePlayers <= 6) {
            shuffleable.removeIf(p2 -> p2.getDefeatedAsPlace() == 7 || p2.getDefeatedAsPlace() == 8);
        }



        if (alivePlayers >= 4) {
            loop: for (;;) {
                Collections.shuffle(shuffleable);
                for (int i = 0; i < shuffleable.size();) {
                    Player p1 = shuffleable.get(i++);
                    Player p2 = shuffleable.get(i++);

                    if (p1.getLastOpponent() == p2 || p1.getSecondToLastOpponent() == p2) continue loop;

                    //matchup only last three against ghost
                    if (    alivePlayers % 2 == 1
                            && p1.getDefeatedAsPlace() > 1
                            && curRanking.indexOf(p2) + 3 < curRanking.indexOf(p1)
                    ) continue loop;
                }
                break;
            }
        } else {
            //TODO find out if really no rules apply if less than 4 players
            Collections.shuffle(shuffleable);
        }
        nextMatchups = shuffleable;
    }


    public static void calcCurrentRanking(Player[] players) {

        if (!deathsLastTurn.isEmpty()) {
            deathsLastTurn.sort((p1, p2) -> {
                if (curRanking.indexOf(p1) > curRanking.indexOf(p2)) {
                    return 1;
                } else {
                    return -1;
                }
            });
            for (int i = deathsLastTurn.size() - 1; i >= 0; i--) {
                deathsLastTurn.get(i).setDefeatedAsPlace(nextDeathWillBeRank);
                nextDeathWillBeRank--;
            }
            deathsLastTurn.clear();
        }

        LinkedList<Player> ranking = new LinkedList<>();
        Collections.addAll(ranking, players);

        ranking.sort((o1, o2) -> {
            if ((o1.getDefeatedAsPlace() == o2.getDefeatedAsPlace()) && (o1.getHealth() == o2.getHealth())) {
                if (curRanking != null) {
                    if(curRanking.indexOf(o1) > curRanking.indexOf(o2)) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else {
                    if (o1.getPlayerNr() > o2.getPlayerNr()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            } else if (o1.getDefeatedAsPlace() == o2.getDefeatedAsPlace()) {
                if (o1.getHealth() < o2.getHealth()) {
                    return 1;
                } else if (o1.getHealth() == o2.getHealth()) {
                    return 0;
                } else {
                    return -1;
                }
            } else {
                if (o1.getDefeatedAsPlace() > o2.getDefeatedAsPlace()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        curRanking = ranking;
    }


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

        p1b = p1.getMyBoard().getMyAuraBoard().getDeepCopyOfThis();
        p2b = p2.getMyBoard().getMyAuraBoard().getDeepCopyOfThis();

        if (p1b.getBoardSize() > 0 && p2b.getBoardSize() > 0) {
            p1nextAttacker = p1b.getBoardMinion(0);
            p2nextAttacker = p2b.getBoardMinion(0);
        }

        LinkedList<Minion> p1RedWhelps = p1b.contains("Red Whelp");
        LinkedList<Minion> p2RedWhelps = p2b.contains("Red Whelp");
        if (!p1RedWhelps.isEmpty() || !p2RedWhelps.isEmpty()) {
            int nr = Math.max(p1RedWhelps.size(), p2RedWhelps.size());
            for (int i = 0; i < nr; i++) {
                if (i < p1RedWhelps.size() && p1b.getBoardMinions().contains(p1RedWhelps.get(i))) {
                    int howMuchdamage = 0;
                    int howOften;
                    for(Minion m : p1b.getBoardMinions()) {
                        if (m.getTribe() == Tribe.DRAGON) howMuchdamage++;
                    }
                    if (!p1RedWhelps.get(i).isGolden()) {
                        howOften = 1;
                    } else {
                        howOften = 2;
                    }
                    for (int j = 0; j < howOften; j++) {
                        if (p2b.getBoardSize() > 0) {
                            Minion target = p2b.getBoardMinion(new Random().nextInt(p2b.getBoardSize()));
                            Game.appendToLeftTextArea("Red Whelp dealt " + howMuchdamage + " damage to " + target.getName() + "\n");
                            dealDamage(p1b, p1RedWhelps.get(i), howMuchdamage, p2b, target);
                        }
                    }
                }

                if (i < p2RedWhelps.size() && p2b.getBoardMinions().contains(p2RedWhelps.get(i))) {
                    int howMuchdamage = 0;
                    int howOften;
                    for(Minion m : p2b.getBoardMinions()) {
                        if (m.getTribe() == Tribe.DRAGON) howMuchdamage++;
                    }
                    if (!p2RedWhelps.get(i).isGolden()) {
                        howOften = 1;
                    } else {
                        howOften = 2;
                    }
                    for (int j = 0; j < howOften; j++) {
                        if (p1b.getBoardSize() > 0) {
                            Minion target = p1b.getBoardMinion(new Random().nextInt(p1b.getBoardSize()));
                            Game.appendToLeftTextArea("Red Whelp dealt " + howMuchdamage + " damage to " + target.getName() + "\n");
                            dealDamage(p2b, p2RedWhelps.get(i), howMuchdamage, p1b, target);
                        }
                    }
                }
            }
        }

        //attacking
        while (p1b.getBoardSize() > 0 && p2b.getBoardSize() > 0 && !twiceOnlyEggsLeft()) {
            boolean sucessfullAttack1 = false;                              //necessary for 0 attack handling
            for (int i = 0; !sucessfullAttack1 && i < 8; i++) {
                p1activeAttacker = p1nextAttacker;
                p1GoToNextAttackerInLine();
                sucessfullAttack1 = attack(p1b, p1activeAttacker, p2b);
            }
            p1activeAttacker = null;

            if (p1b.getBoardSize() <= 0 || p2b.getBoardSize() <= 0) break;

            boolean sucessfullAttack2 = false;
            for (int i = 0; !sucessfullAttack2 && i < 8; i++) {
                p2activeAttacker = p2nextAttacker;
                p2GoToNextAttackerInLine();
                sucessfullAttack2 = attack(p2b, p2activeAttacker, p1b);
            }
            p2activeAttacker = null;
        }

        //dealing damage to loser
        if(p1b.getBoardSize() > 0 && p2b.getBoardSize() > 0) {
            p1.setSecondToLastDamageTaken(p1.getLastDamageTaken());
            p1.setLastDamageTaken(0);
            p2.setSecondToLastDamageTaken(p2.getLastDamageTaken());
            p2.setLastDamageTaken(0);
            Game.appendToLeftTextArea("Egg draw!");

        } else if(p1b.getBoardSize() > 0) {
            int damage = p1.getMyShop().getTavernTier();
            for (int i = 0; i < p1b.getBoardSize(); i++) {
                damage += p1b.getBoardMinion(i).getStars();
            }
            p2.reduceHealth(damage);
            p2.setSecondToLastDamageTaken(p2.getLastDamageTaken());
            p2.setLastDamageTaken(damage);
            p1.setSecondToLastDamageTaken(p1.getLastDamageTaken());
            p1.setLastDamageTaken(0);
            Game.appendToLeftTextArea("Player " + p1.getPlayerNr() + " won!\nPlayer " + p2.getPlayerNr() + " will take " + damage + " damage");

        } else if(p2b.getBoardSize() > 0) {
            int damage = p2.getMyShop().getTavernTier();
            for (int i = 0; i < p2b.getBoardSize(); i++) {
                damage += p2b.getBoardMinion(i).getStars();
            }
            p1.reduceHealth(damage);
            p1.setSecondToLastDamageTaken(p1.getLastDamageTaken());
            p1.setLastDamageTaken(damage);
            p2.setSecondToLastDamageTaken(p2.getLastDamageTaken());
            p2.setLastDamageTaken(0);
            Game.appendToLeftTextArea("Player " + p2.getPlayerNr() + " won!\nPlayer " + p1.getPlayerNr() + " will take " + damage + " damage");

        } else {
            p1.setSecondToLastDamageTaken(p1.getLastDamageTaken());
            p1.setLastDamageTaken(0);
            p2.setSecondToLastDamageTaken(p2.getLastDamageTaken());
            p2.setLastDamageTaken(0);
            Game.appendToLeftTextArea("It's a draw!");
        }

        //adding dead players to deathsLastTurn
        if (p1.getHealth() <= 0) {
            p1.setHealth(0);
            if (p1.getDefeatedAsPlace() == 1) deathsLastTurn.add(p1);
        }
        if (p2.getHealth() <= 0) {
            p2.setHealth(0);
            if (p2.getDefeatedAsPlace() == 1) deathsLastTurn.add(p2);
        }

        //tracking last opponents
        p1.setSecondToLastOpponent(p1.getLastOpponent());
        p1.setLastOpponent(p2);

        p2.setSecondToLastOpponent(p2.getLastOpponent());
        p2.setLastOpponent(p1);

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


    //executes one attack
    public static boolean attack(AuraBoard attackersBoard, Minion attacker, AuraBoard defendersBoard) {
        if(attacker.getAttack() <= 0) return false;

        int howOften = 1;
        if(attacker.isWindfury()) howOften = 2;
        if(attacker.getName().equals("Zapp Slywick") && attacker.isGolden()) howOften = 4;

        for (int i = 0; i < howOften && (attackersBoard.getBoardMinions().contains(attacker)); i++) {

            //find target
            int targetIndex;
            if (!attacker.getName().equals("Zapp Slywick")) {
                LinkedList<Integer> indexesOfOpposingTauntMinions = new LinkedList<>();
                for (int j = 0; j < defendersBoard.getBoardSize(); j++) {
                    if (defendersBoard.getBoardMinion(j).isTaunt()) indexesOfOpposingTauntMinions.add(j);
                }

                if (indexesOfOpposingTauntMinions.isEmpty()) {
                    targetIndex = new Random().nextInt(defendersBoard.getBoardSize());
                } else {
                    targetIndex = indexesOfOpposingTauntMinions.get(new Random().nextInt(indexesOfOpposingTauntMinions.size()));
                }

            } else {
                int lowestAttackOfEnemy = Integer.MAX_VALUE;
                LinkedList<Minion> defendersMinions = defendersBoard.getBoardMinions();

                //find lowest attack
                for (Minion m : defendersMinions) {
                    int defendersAttack = m.getAttack();

                    //handling old murk-eye
                    if (m.getName().equals("Old Murk-Eye")) {
                        int murlocCounter = -1;                 //-1 because only other murlocs count
                        for (Minion possiblyMurloc : defendersBoard.getBoardMinions()) {
                            if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) {
                                murlocCounter++;
                            }
                        }
                        for (Minion possiblyMurloc : attackersBoard.getBoardMinions()) {
                            if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) {
                                murlocCounter++;
                            }
                        }
                        if (!m.isGolden()) {
                            defendersAttack += murlocCounter;
                        } else {
                            defendersAttack += murlocCounter * 2;
                        }
                    }

                    if (defendersAttack < lowestAttackOfEnemy) lowestAttackOfEnemy = defendersAttack;
                }

                //find all minions with lowest attack
                LinkedList<Integer> indexesOfOpposingLowestAttackMinions = new LinkedList<>();
                for (Minion m : defendersMinions) {
                    int defendersAttack = m.getAttack();

                    //handling old murk-eye
                    if (m.getName().equals("Old Murk-Eye")) {
                        int murlocCounter = -1;                 //-1 because only other murlocs count
                        for (Minion possiblyMurloc : defendersBoard.getBoardMinions()) {
                            if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) {
                                murlocCounter++;
                            }
                        }
                        for (Minion possiblyMurloc : attackersBoard.getBoardMinions()) {
                            if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) {
                                murlocCounter++;
                            }
                        }
                        if (!m.isGolden()) {
                            defendersAttack += murlocCounter;
                        } else {
                            defendersAttack += murlocCounter * 2;
                        }
                    }

                    if (defendersAttack == lowestAttackOfEnemy) indexesOfOpposingLowestAttackMinions.add(defendersMinions.indexOf(m));
                }

                //pick a random minions of those with the lowest attack
                targetIndex = indexesOfOpposingLowestAttackMinions.get(new Random().nextInt(indexesOfOpposingLowestAttackMinions.size()));
            }

            Minion target = defendersBoard.getBoardMinion(targetIndex);

            if(attacker.getName().equals("Glyph Guardian")) {
                if (!attacker.isGolden()) {
                    attacker.addAttack(attacker.getAttack());
                } else {
                    attacker.addAttack(attacker.getAttack() * 2);
                }
            }


            //handling targets murk-eye
            int targetAttackSave = target.getAttack();
            if(target.getName().equals("Old Murk-Eye")) {
                int murlocCounter = -1;                 //-1 because only other murlocs count
                for (Minion possiblyMurloc : defendersBoard.getBoardMinions()) {
                    if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) {
                        murlocCounter++;
                    }
                }
                for (Minion possiblyMurloc : attackersBoard.getBoardMinions()) {
                    if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) {
                        murlocCounter++;
                    }
                }
                if (!target.isGolden()) {
                    target.addAttack(murlocCounter);
                } else {
                    target.addAttack(murlocCounter * 2);
                }
            }

            //handling attackers murk-eye
            int attackerAttackSave = attacker.getAttack();
            if(attacker.getName().equals("Old Murk-Eye")) {
                int murlocCounter = -1;                 //-1 because only other murlocs count
                for (Minion possiblyMurloc : defendersBoard.getBoardMinions()) {
                    if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) {
                        murlocCounter++;
                    }
                }
                for (Minion possiblyMurloc : attackersBoard.getBoardMinions()) {
                    if (possiblyMurloc.getTribe() == Tribe.MURLOC || possiblyMurloc.getTribe() == Tribe.ALL) {
                        murlocCounter++;
                    }
                }
                if (!attacker.isGolden()) {
                    attacker.addAttack(murlocCounter);
                } else {
                    attacker.addAttack(murlocCounter * 2);
                }
            }

            Game.appendToLeftTextArea(attacker.toString() + "  attacks  " + target.toString() + "\n");

            //execute attack (saves necessary cause one might die and lose its atk aura buff)
            int targetAtk   = target.getAttack();
            int attackerAtk = attacker.getAttack();
            //TODO find out who dies first
            dealDamage(defendersBoard, target, targetAtk, attackersBoard, attacker);
            dealDamage(attackersBoard, attacker, attackerAtk, defendersBoard, target);

            target.setAttack(targetAttackSave);
            attacker.setAttack(attackerAttackSave);
        }
        return true;
    }

    public static void dealDamage(AuraBoard damageDealersAuraBoard, Minion damageDealer, int howMuch, AuraBoard targetsAuraBoard, Minion target) {

        if (!target.isDivineShield()) {
            target.reduceHealth(howMuch);
            if (target.getHealth() <= 0 || (damageDealer.isPoisonous() && damageDealer.getAttack() > 0)) {
                killMinion(targetsAuraBoard, target, damageDealersAuraBoard, damageDealer);
            }
        } else {
            if(damageDealer.getAttack() > 0) target.setDivineShield(false);
        }
    }

    public static void killMinion(AuraBoard minionsAuraBoard, Minion minion, AuraBoard opposingAuraBoard, Minion killer) {
        Game.appendToLeftTextArea(minion.toString() + " died\n");
        boolean wasp1na = false;
        boolean wasp2na = false;
        if (minion == p1nextAttacker) {
            p1GoToNextAttackerInLine();
            wasp1na = true;
        }
        if (minion == p2nextAttacker) {
            p2GoToNextAttackerInLine();
            wasp2na = true;
        }

        int savePos = minionsAuraBoard.getBoardMinions().indexOf(minion);
        int atkBuff = minionsAuraBoard.removeMinion(savePos);

        if (minion.getName().equals("Fiendish Servant")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                if (minionsAuraBoard.getBoardSize() > 0) {
                    int oneceOrTwice = 1;
                    if(minion.isGolden()) oneceOrTwice = 2;
                    for (int j = 0; j < oneceOrTwice; j++) {
                        Minion luckyOne = minionsAuraBoard.getBoardMinion(new Random().nextInt(minionsAuraBoard.getBoardSize()));
                        luckyOne.addAttack(minion.getAttack() + atkBuff);
                        Game.appendToLeftTextArea("Fiendish Servant's deathrattle gave " + (minion.getAttack() + atkBuff) + " attack to " + luckyOne.getName() + "\n");
                    }
                }
            }
        }

        if (minion.getName().equals("Mecharoo")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                Minion joEBot = MinionPool.generateMinion("Jo-E Bot");
                assert joEBot != null;
                if (minion.isGolden()) {
                    joEBot.setGolden(true);
                    joEBot.setAttack(2);
                    joEBot.setHealth(2);
                }
                summonMinion(minionsAuraBoard, joEBot, savePos);
                if (wasp1na || minion == p1activeAttacker) setP1nextAttacker(joEBot);
                if (wasp2na || minion == p2activeAttacker) setP2nextAttacker(joEBot);
                Game.appendToLeftTextArea("Mecharoo's deathrattle summoned " + joEBot.toString() + "\n");
            }
        }

        if (minion.getName().equals("Selfless Hero")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                if (minionsAuraBoard.getBoardSize() > 0) {
                    int onceOrTwice = 1;
                    if(minion.isGolden()) onceOrTwice = 2;
                    for (int j = 0; j < onceOrTwice; j++) {
                        LinkedList<Minion> noDivineShielders = new LinkedList<>();
                        for (Minion m : minionsAuraBoard.getBoardMinions()) {
                            if (!m.isDivineShield()) noDivineShielders.add(m);
                        }
                        Collections.shuffle(noDivineShielders);
                        Minion luckyOne = noDivineShielders.getFirst();
                        luckyOne.setDivineShield(true);
                        Game.appendToLeftTextArea("Selfless Hero's deathrattle gave " + luckyOne.getName() + " Divine Shield\n");
                    }
                }
            }
        }

        if (minion.getName().equals("Harvest Golem")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                Minion damagedGolem = MinionPool.generateMinion("Damaged Golem");
                assert damagedGolem != null;
                if (minion.isGolden()) {
                    damagedGolem.setGolden(true);
                    damagedGolem.setAttack(4);
                    damagedGolem.setHealth(2);
                }
                summonMinion(minionsAuraBoard, damagedGolem, savePos);
                if (wasp1na || minion == p1activeAttacker) setP1nextAttacker(damagedGolem);
                if (wasp2na || minion == p2activeAttacker) setP2nextAttacker(damagedGolem);
                Game.appendToLeftTextArea("Harvest Golem's deathrattle summoned " + damagedGolem.toString() + "\n");
            }
        }

        if (minion.getName().equals("Imprisoner")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                Minion imp = MinionPool.generateMinion("Imp");
                assert imp != null;
                if (minion.isGolden()) {
                    imp.setGolden(true);
                    imp.setAttack(2);
                    imp.setHealth(2);
                }
                summonMinion(minionsAuraBoard, imp, savePos);
                if (wasp1na || minion == p1activeAttacker) setP1nextAttacker(imp);
                if (wasp2na || minion == p2activeAttacker) setP2nextAttacker(imp);
                Game.appendToLeftTextArea("Imprisoner's deathrattle summoned " + imp.toString() + "\n");
            }
        }

        if (minion.getName().equals("Kindly Grandmother")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                Minion bigBadWolf = MinionPool.generateMinion("Big Bad Wolf");
                assert bigBadWolf != null;
                if (minion.isGolden()) {
                    bigBadWolf.setGolden(true);
                    bigBadWolf.setAttack(6);
                    bigBadWolf.setHealth(4);
                }
                summonMinion(minionsAuraBoard, bigBadWolf, savePos);
                if (wasp1na || minion == p1activeAttacker) setP1nextAttacker(bigBadWolf);
                if (wasp2na || minion == p2activeAttacker) setP2nextAttacker(bigBadWolf);
                Game.appendToLeftTextArea("Kindly Grandmother's deathrattle summoned " + bigBadWolf.toString() + "\n");
            }
        }

        if (minion.getName().equals("Rat Pack")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                for (int j = 0; j < minion.getAttack() + atkBuff; j++) {
                    if (minionsAuraBoard.getBoardSize() < 7) {
                        Minion rat = MinionPool.generateMinion("Rat");
                        assert rat != null;
                        if (minion.isGolden()) {
                            rat.setGolden(true);
                            rat.setAttack(2);
                            rat.setHealth(2);
                        }
                        summonMinion(minionsAuraBoard, rat, savePos);
                        if (wasp1na || minion == p1activeAttacker) setP1nextAttacker(rat);
                        if (wasp2na || minion == p2activeAttacker) setP2nextAttacker(rat);
                        Game.appendToLeftTextArea("Rat Pack's deathrattle summoned " + rat.toString() + "\n");
                    }
                }
            }
        }

        LinkedList<Minion> scavengingHyenas = minionsAuraBoard.contains("Scavenging Hyena");
        if (!scavengingHyenas.isEmpty() && (minion.getTribe() == Tribe.BEAST || minion.getTribe() == Tribe.ALL)) {
            for (Minion sh : scavengingHyenas) {
                if (sh != minion) {
                    if (!sh.isGolden()) {
                        sh.addAttack(2);
                        minionsAuraBoard.addHealthTo(sh, 1);
                    } else {
                        sh.addAttack(4);
                        minionsAuraBoard.addHealthTo(sh, 2);
                    }
                }
            }
        }

        if(minion.getName().equals("Spawn of N'Zoth")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i = 0; i < howOften; i++) {
                for (Minion m : minionsAuraBoard.getBoardMinions()) {
                    if (!minion.isGolden()) {
                        m.addAttack(1);
                        minionsAuraBoard.addHealthTo(m, 1);
                    } else {
                        m.addAttack(2);
                        minionsAuraBoard.addHealthTo(m, 2);
                    }
                }
            }
        }

        if(minion.getName().equals("Unstable Ghoul")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i = 0; i < howOften; i++) {
                for (Minion m : minionsAuraBoard.getBoardMinions()) {
                    if (!minion.isGolden()) {
                        //TODO find out interactions with imp gang boss etc. and deathrattles
                    } else {

                    }
                }
            }
        }

        LinkedList<Minion> waxriders = opposingAuraBoard.contains("Waxrider Togwaggle");
        if (!waxriders.isEmpty() && (killer.getTribe() == Tribe.DRAGON || killer.getTribe() == Tribe.ALL)) {
            for (Minion wt : waxriders) {
                if (!wt.isGolden()) {
                    wt.addAttack(2);
                    minionsAuraBoard.addHealthTo(wt, 2);
                } else {
                    wt.addAttack(4);
                    minionsAuraBoard.addHealthTo(wt, 4);
                }
            }
        }

        if (minion.getName().equals("Infested Wolf")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                for (int j = 0; j < 2; j++) {
                    if (minionsAuraBoard.getBoardSize() < 7) {
                        Minion spuder = MinionPool.generateMinion("Spider");
                        assert spuder != null;
                        if (minion.isGolden()) {
                            spuder.setGolden(true);
                            spuder.setAttack(2);
                            spuder.setHealth(2);
                        }
                        summonMinion(minionsAuraBoard, spuder, savePos);
                        if (wasp1na || minion == p1activeAttacker) setP1nextAttacker(spuder);
                        if (wasp2na || minion == p2activeAttacker) setP2nextAttacker(spuder);
                        Game.appendToLeftTextArea("Infested Wolf's deathrattle summoned " + spuder.toString() + "\n");
                    }
                }
            }
        }

        if (minion.getName().equals("Piloted Shredder")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                int goldenMult = 1;
                if (minion.isGolden()) goldenMult = 2;
                for (int j = 0; j < goldenMult; j++) {
                    if (minionsAuraBoard.getBoardSize() < 7) {
                        Minion rndTwoCost = MinionPool.generateRandomPilotedShredderMinion();
                        summonMinion(minionsAuraBoard, rndTwoCost, savePos);
                        if (wasp1na || minion == p1activeAttacker) setP1nextAttacker(rndTwoCost);
                        if (wasp2na || minion == p2activeAttacker) setP2nextAttacker(rndTwoCost);
                        Game.appendToLeftTextArea("Piloted Shredder's deathrattle summoned " + rndTwoCost.toString() + "\n");
                    }
                }
            }
        }

        if (minion.getName().equals("Replicating Menace")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                for (int j = 0; j < 3; j++) {
                    if (minionsAuraBoard.getBoardSize() < 7) {
                        Minion microbot = MinionPool.generateMinion("Microbot");
                        assert microbot != null;
                        if (minion.isGolden()) {
                            microbot.setGolden(true);
                            microbot.setAttack(2);
                            microbot.setHealth(2);
                        }
                        summonMinion(minionsAuraBoard, microbot, savePos);
                        if (wasp1na || minion == p1activeAttacker) setP1nextAttacker(microbot);
                        if (wasp2na || minion == p2activeAttacker) setP2nextAttacker(microbot);
                        Game.appendToLeftTextArea("Replicating Menace's deathrattle summoned " + microbot.toString() + "\n");
                    }
                }
            }
        }

        if (minion.getName().equals("The Beast")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                if (opposingAuraBoard.getBoardSize() < 7) {
                    Minion finkleEinhorn = MinionPool.generateMinion("Finkle Einhorn");
                    assert finkleEinhorn != null;
                    if (minion.isGolden()) {
                        finkleEinhorn.setGolden(true);
                    }
                    summonMinion(opposingAuraBoard, finkleEinhorn, opposingAuraBoard.getBoardSize());
                    //TODO might affect opposing attack order
                    Game.appendToLeftTextArea("The Beast's deathrattle summoned " + finkleEinhorn.toString() + " for his opponent\n");
                }
            }
        }

        if (minion.getName().equals("Mechano-Egg")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                Minion robosaur = MinionPool.generateMinion("Robosaur");
                assert robosaur != null;
                if (minion.isGolden()) {
                    robosaur.setGolden(true);
                    robosaur.setAttack(16);
                    robosaur.setHealth(16);
                }
                summonMinion(minionsAuraBoard, robosaur, savePos);
                if (wasp1na || minion == p1activeAttacker) setP1nextAttacker(robosaur);
                if (wasp2na || minion == p2activeAttacker) setP2nextAttacker(robosaur);
                Game.appendToLeftTextArea("Mechano Egg's deathrattle summoned " + robosaur.toString() + "\n");
            }
        }

        if (minion.getName().equals("Savannah Highmane")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                for (int j = 0; j < 2; j++) {
                    if (minionsAuraBoard.getBoardSize() < 7) {
                        Minion hyena = MinionPool.generateMinion("Hyena");
                        assert hyena != null;
                        if (minion.isGolden()) {
                            hyena.setGolden(true);
                            hyena.setAttack(4);
                            hyena.setHealth(4);
                        }
                        summonMinion(minionsAuraBoard, hyena, savePos);
                        if (wasp1na || minion == p1activeAttacker) setP1nextAttacker(hyena);
                        if (wasp2na || minion == p2activeAttacker) setP2nextAttacker(hyena);
                        Game.appendToLeftTextArea("Savannah Highmane's deathrattle summoned " + hyena.toString() + "\n");
                    }
                }
            }
        }

        if(minion.getName().equals("Goldrinn, the Great Wolf")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i = 0; i < howOften; i++) {
                for (Minion ideallyBeast : minionsAuraBoard.getBoardMinions()) {
                    if (ideallyBeast.getTribe() == Tribe.BEAST || ideallyBeast.getTribe() == Tribe.ALL) {
                        if (!minion.isGolden()) {
                            ideallyBeast.addAttack(4);
                            minionsAuraBoard.addHealthTo(ideallyBeast, 4);
                        } else {
                            ideallyBeast.addAttack(8);
                            minionsAuraBoard.addHealthTo(ideallyBeast, 8);
                        }
                    }
                }
            }
        }

        if(minion.getName().equals("King Bagurgle")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i = 0; i < howOften; i++) {
                for (Minion ideallyMurloc : minionsAuraBoard.getBoardMinions()) {
                    if (ideallyMurloc.getTribe() == Tribe.MURLOC || ideallyMurloc.getTribe() == Tribe.ALL) {
                        if (!minion.isGolden()) {
                            ideallyMurloc.addAttack(2);
                            minionsAuraBoard.addHealthTo(ideallyMurloc, 2);
                        } else {
                            ideallyMurloc.addAttack(4);
                            minionsAuraBoard.addHealthTo(ideallyMurloc, 4);
                        }
                    }
                }
            }
        }

        if (minion.getName().equals("Sneed's Old Shredder")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                int goldenMult = 1;
                if (minion.isGolden()) goldenMult = 2;
                for (int j = 0; j < goldenMult; j++) {
                    if (minionsAuraBoard.getBoardSize() < 7) {
                        Minion rndLegendary = MinionPool.generateRandomSneedsMinion();
                        summonMinion(minionsAuraBoard, rndLegendary, savePos);
                        if (wasp1na || minion == p1activeAttacker) setP1nextAttacker(rndLegendary);
                        if (wasp2na || minion == p2activeAttacker) setP2nextAttacker(rndLegendary);
                        Game.appendToLeftTextArea("Sneed's Old Shredder's deathrattle summoned " + rndLegendary.toString() + "\n");
                    }
                }
            }
        }

        if (minion.getName().equals("Voidlord")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                for (int j = 0; j < 3; j++) {
                    if (minionsAuraBoard.getBoardSize() < 7) {
                        Minion voidwalker = MinionPool.generateMinion("Voidwalker");
                        assert voidwalker != null;
                        if (minion.isGolden()) {
                            voidwalker.setGolden(true);
                            voidwalker.setAttack(2);
                            voidwalker.setHealth(6);
                        }
                        summonMinion(minionsAuraBoard, voidwalker, savePos);
                        if (wasp1na || minion == p1activeAttacker) setP1nextAttacker(voidwalker);
                        if (wasp2na || minion == p2activeAttacker) setP2nextAttacker(voidwalker);
                        Game.appendToLeftTextArea("Voidlord's deathrattle summoned " + voidwalker.toString() + "\n");
                    }
                }
            }
        }

        if (minion.getName().equals("Ghastcoiler")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i  = 0; i < howOften; i++) {
                int goldenMult = 2;
                if (minion.isGolden()) goldenMult = 4;
                for (int j = 0; j < goldenMult; j++) {
                    if (minionsAuraBoard.getBoardSize() < 7) {
                        Minion rndDeathrattleMinion = MinionPool.generateRandomCoilerMinion();
                        summonMinion(minionsAuraBoard, rndDeathrattleMinion, savePos);
                        if (wasp1na || minion == p1activeAttacker) setP1nextAttacker(rndDeathrattleMinion);
                        if (wasp2na || minion == p2activeAttacker) setP2nextAttacker(rndDeathrattleMinion);
                        Game.appendToLeftTextArea("Ghastcoiler's deathrattle summoned " + rndDeathrattleMinion.toString() + "\n");
                    }
                }
            }
        }

        if(minion.getName().equals("Nadina the Red")) {
            int howOften = getDeathrattleMultiplier(minionsAuraBoard);
            for (int i = 0; i < howOften; i++) {
                for (Minion ideallyDragon : minionsAuraBoard.getBoardMinions()) {
                    if (ideallyDragon.getTribe() == Tribe.DRAGON || ideallyDragon.getTribe() == Tribe.ALL) {
                        ideallyDragon.setDivineShield(true);
                    }
                }
            }
        }


        if (minion.isReborn()) {
            Minion ressurrectedMinion = MinionPool.generateMinion(minion.getName());
            assert ressurrectedMinion != null;
            ressurrectedMinion.setReborn(false);
            ressurrectedMinion.setHealth(1);
            if (minion.isGolden()) {
                ressurrectedMinion.setGolden(true);
                ressurrectedMinion.addAttack(ressurrectedMinion.getAttack());
            }
            minionsAuraBoard.playMinion(ressurrectedMinion, savePos, -1);           //explicitly not a summon
            if (wasp1na || minion == p1activeAttacker) setP1nextAttacker(ressurrectedMinion);
            if (wasp2na || minion == p2activeAttacker) setP2nextAttacker(ressurrectedMinion);
            Game.appendToLeftTextArea(minion.getName() + " resurrected\n");
        }

    }

    public static void summonMinion(AuraBoard auraBoard, Minion minion, int pos) {
        if (auraBoard.getBoardSize() >= 7) return;

        LinkedList<Minion> murlocTidecallers = auraBoard.contains("Murloc Tidecaller");
        if (!murlocTidecallers.isEmpty() && (minion.getTribe() == Tribe.MURLOC || minion.getTribe() == Tribe.ALL)) {
            for (Minion mtc : murlocTidecallers) {
                if (!mtc.isGolden()) {
                    mtc.addAttack(1);
                } else {
                    mtc.addAttack(2);
                }
            }
        }

        auraBoard.playMinion(minion, pos, -1);
    }

    private static int getDeathrattleMultiplier(AuraBoard auraBoard) {

        LinkedList<Minion> rivendares = auraBoard.contains("Baron Rivendare");
        if (!rivendares.isEmpty()) {
            boolean golden = false;
            for (Minion rivendare : rivendares) {
                if (rivendare.isGolden()) {
                    golden = true;
                    break;
                }
            }
            if (!golden) {
                return 2;
            } else {
                return 3;
            }
        }
        return 1;
    }

    public static String getRankingAsString() {
        String built = "Current Ranking:\n";
        for (Player p : curRanking) {
            built += "\n";
            if (p.getDefeatedAsPlace() > 1) built += p.getDefeatedAsPlace() +". ";
            built += "Player " + p.getPlayerNr() + " | Health: " + p.getHealth();
            if (p.getLastOpponent() != null) {
                built += "   |   ";
                if (p.getLastDamageTaken() > 0) built += "Took " + p.getLastDamageTaken() + " Damage from ";
                else {
                    if(p.getLastOpponent().getLastDamageTaken() == 0) built += "Drew with ";
                    else built += "Dealt " + p.getLastOpponent().getLastDamageTaken() + " Damage to ";
                }
                built += "Player " + p.getLastOpponent().getPlayerNr();
            }
            if (p.getSecondToLastOpponent() != null) {
                built += "   |   ";
                if (p.getSecondToLastDamageTaken() > 0) built += "Took " + p.getSecondToLastDamageTaken() + " Damage from ";
                else {
                    if(p.getSecondToLastOpponent().getSecondToLastDamageTaken() == 0) built += "Drew with ";
                    else built += "Dealt " + p.getSecondToLastOpponent().getSecondToLastDamageTaken() + " Damage to ";
                }
                built += "Player " + p.getSecondToLastOpponent().getPlayerNr();
            }
        }
        return built;
    }

    public static String getNextOpponentAsString(Player p) {
        String built = "Next Opponent: Player ";
        int index = nextMatchups.indexOf(p);
        if(index % 2 == 0) {
            built += nextMatchups.get(index + 1).getPlayerNr();
        } else {
            built += nextMatchups.get(index - 1).getPlayerNr();
        }
        return built;
    }

    public static void setCurRanking(LinkedList<Player> curRanking) {
        Battler.curRanking = curRanking;
    }

    public static void setNextDeathWillBeRank(int nextDeathWillBeRank) {
        Battler.nextDeathWillBeRank = nextDeathWillBeRank;
    }

    public static void setDeathsLastTurn(LinkedList<Player> deathsLastTurn) {
        Battler.deathsLastTurn = deathsLastTurn;
    }
}
