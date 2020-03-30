import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Battler {

    private static Minion p1nextAttacker;
    private static Minion p2nextAttacker;

    private static BoardState p1b;
    private static BoardState p2b;

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
            for (Player p2 : shuffleable) {
                if (p2.getDefeatedAsPlace() == 3 || p2.getDefeatedAsPlace() == 4) shuffleable.remove(p2);
            }
        }
        if (alivePlayers <= 4) {
            for (Player p2 : shuffleable) {
                if (p2.getDefeatedAsPlace() == 5 || p2.getDefeatedAsPlace() == 6) shuffleable.remove(p2);
            }
        }
        //TODO fehler bei löschen aus liste über die iteriert wird
        if (alivePlayers <= 6) {
            for (Player p2 : shuffleable) {
                if (p2.getDefeatedAsPlace() == 7 || p2.getDefeatedAsPlace() == 8) shuffleable.remove(p2);
            }
        }



        if (alivePlayers >= 4) {
            loop: for (;;) {
                Collections.shuffle(shuffleable);
                for (int i = 0; i < shuffleable.size(); i += 2) {
                    //matchup not twice in a row
                    if (shuffleable.getFirst().getLastOpponent() != null) {
                        if (shuffleable.get(i).getLastOpponent() == shuffleable.get(i + 1)) continue loop;
                    }
                    //matchup not twice in last three
                    if (shuffleable.getFirst().getSecondToLastOpponent() != null) {
                        if (shuffleable.get(i).getSecondToLastOpponent() == shuffleable.get(i + 1)) continue loop;
                    }
                    //matchup only last three against ghost TODO doesnt work
                    if (alivePlayers % 2 == 1) {
                        if (shuffleable.get(1).getDefeatedAsPlace() > 1 && curRanking.indexOf(shuffleable.get(i + 1)) + 3 < curRanking.indexOf(shuffleable.get(i))) continue loop;
                    }
                }
                nextMatchups = shuffleable;
                break loop;
            }
        } else {
            //TODO find out if realy no rules apply if less than 4 players
            Collections.shuffle(shuffleable);
            nextMatchups = shuffleable;
        }
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

        p1b = p1.getMyBoard().getMyBoardState().getDeepCopyOfThis();
        p2b = p2.getMyBoard().getMyBoardState().getDeepCopyOfThis();

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
                Minion activeAttacker = p1nextAttacker;
                p1GoToNextAttackerInLine();
                sucessfullAttack1 = attack(p1b, activeAttacker, p2b);
            }

            if (p1b.getBoardSize() <= 0 || p2b.getBoardSize() <= 0) break;

            boolean sucessfullAttack2 = false;
            for (int i = 0; !sucessfullAttack2 && i < 8; i++) {
                Minion activeAttacker = p2nextAttacker;
                p2GoToNextAttackerInLine();
                sucessfullAttack2 = attack(p2b, activeAttacker, p1b);
            }
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
            if (p1.getDefeatedAsPlace() == 1) deathsLastTurn.add(p2);
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
    public static boolean attack(BoardState attackersBoard, Minion attacker, BoardState defendersBoard) {
        if(attacker.getAttack() <= 0) return false;

        int howOften = 1;
        if(attacker.isWindfury()) howOften = 2;
        if(attacker.getName().equals("Zapp Slywick") && attacker.isGolden()) howOften = 4;

        for (int i = 0; i < howOften && (attackersBoard.getBoardMinions().contains(attacker)); i++) {

            //find target
            LinkedList<Integer> indexesOfOpposingTauntMinions = new LinkedList<>();
            for (int j = 0; j < defendersBoard.getBoardSize(); j++) {
                if(defendersBoard.getBoardMinion(j).isTaunt()) indexesOfOpposingTauntMinions.add(j);
            }

            int targetIndex;
            if (indexesOfOpposingTauntMinions.isEmpty()) {
                targetIndex = new Random().nextInt(defendersBoard.getBoardSize());
            } else {
                targetIndex = indexesOfOpposingTauntMinions.get(new Random().nextInt(indexesOfOpposingTauntMinions.size()));
            }

            Minion target = defendersBoard.getBoardMinion(targetIndex);

            Game.appendToLeftTextArea(attacker.toString() + "  attacks  " + target.toString() + " (not displaying aura-buffs)\n");

            int targetAttack = target.getAttack();
            int attackerAttack = attacker.getAttack();

            //handling targets aura effects
            LinkedList<Minion> murlocWarleaders = defendersBoard.contains("Murloc Warleader");
            if (!murlocWarleaders.isEmpty() && (target.getTribe() == Tribe.MURLOC || target.getTribe() == Tribe.ALL)) {
                int buffs = 0;
                for (Minion mwl : murlocWarleaders) {
                    if (mwl != target) {
                        if (!mwl.isGolden()) {
                            buffs += 2;
                        } else {
                            buffs += 4;
                        }
                    }
                }
                targetAttack += buffs;
            }

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
                    targetAttack += murlocCounter;
                } else {
                    targetAttack += murlocCounter * 2;
                }
            }

            if (targetIndex - 1 >= 0) {
                Minion possiblyDWA = defendersBoard.getBoardMinion(targetIndex - 1);
                if(possiblyDWA.getName().equals("Dire Wolf Alpha")){
                    if (!possiblyDWA.isGolden()) {
                        targetAttack++;
                    } else {
                        targetAttack += 2;
                    }
                }
            }
            if (targetIndex + 1 < defendersBoard.getBoardSize()) {
                Minion possiblyDWA = defendersBoard.getBoardMinion(targetIndex + 1);
                if(possiblyDWA.getName().equals("Dire Wolf Alpha")){
                    if (!possiblyDWA.isGolden()) {
                        targetAttack++;
                    } else {
                        targetAttack += 2;
                    }
                }
            }

            //handling attackers aura effects
            LinkedList<Minion> murlocWarleaders2 = attackersBoard.contains("Murloc Warleader");
            if (!murlocWarleaders2.isEmpty() && (attacker.getTribe() == Tribe.MURLOC || attacker.getTribe() == Tribe.ALL)) {
                int buffs = 0;
                for (Minion mwl : murlocWarleaders2) {
                    if (mwl != attacker) {
                        if (!mwl.isGolden()) {
                            buffs += 2;
                        } else {
                            buffs += 4;
                        }
                    }
                }
                attackerAttack += buffs;
            }

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
                    attackerAttack += murlocCounter;
                } else {
                    attackerAttack += murlocCounter * 2;
                }
            }

            int attackerIndex = attackersBoard.getBoardMinions().indexOf(attacker);
            if (attackerIndex - 1 >= 0) {
                Minion possiblyDWA = attackersBoard.getBoardMinion(attackerIndex - 1);
                if(possiblyDWA.getName().equals("Dire Wolf Alpha")){
                    if (!possiblyDWA.isGolden()) {
                        attackerAttack++;
                    } else {
                        attackerAttack += 2;
                    }
                }
            }
            if (attackerIndex + 1 < attackersBoard.getBoardSize()) {
                Minion possiblyDWA = attackersBoard.getBoardMinion(attackerIndex + 1);
                if(possiblyDWA.getName().equals("Dire Wolf Alpha")){
                    if (!possiblyDWA.isGolden()) {
                        attackerAttack++;
                    } else {
                        attackerAttack += 2;
                    }
                }
            }

            //execute attack
            dealDamage(defendersBoard, target, targetAttack, attackersBoard, attacker);
            dealDamage(attackersBoard, attacker, attackerAttack, defendersBoard, target);
        }
        return true;
    }

    public static void dealDamage(BoardState damageDealersBoardState, Minion damageDealer, int howMuch, BoardState targetsBoardState, Minion target) {

        if (!target.isDivineShield()) {
            target.reduceHealth(howMuch);
            if (target.getHealth() <= 0 || (damageDealer.isPoisonous() && damageDealer.getAttack() > 0)) {
                killMinion(targetsBoardState, target, damageDealersBoardState, damageDealer);
            }
        } else {
            if(damageDealer.getAttack() > 0) target.setDivineShield(false);
        }
    }

    public static void killMinion(BoardState boardState, Minion minion, BoardState opposingBoardState, Minion killer) {
        //TODO deathrattles, board dependent stuff
        Game.appendToLeftTextArea(minion.toString() + " died\n");
        if(minion == p1nextAttacker) p1GoToNextAttackerInLine();
        if(minion == p2nextAttacker) p2GoToNextAttackerInLine();
        int savePos = boardState.getBoardMinions().indexOf(minion);
        boardState.getBoardMinions().remove(minion);

        if (minion.getName().equals("Fiendish Servant")) {
            int howOften = getDeathrattleMultiplier(boardState);
            for (int i  = 0; i < howOften; i++) {
                if (boardState.getBoardSize() > 0) {
                    int oneceOrTwice = 1;
                    if(minion.isGolden()) oneceOrTwice = 2;
                    for (int j = 0; j < oneceOrTwice; j++) {
                        Minion luckyOne = boardState.getBoardMinion(new Random().nextInt(boardState.getBoardSize()));
                        luckyOne.addAttack(minion.getAttack());
                        Game.appendToLeftTextArea("Fiendish Servant's deathrattle gave " + minion.getAttack() + " attack to " + luckyOne.getName() + "\n");
                    }
                }
            }
        }

        if (minion.getName().equals("Mecharoo")) {
            int howOften = getDeathrattleMultiplier(boardState);
            for (int i  = 0; i < howOften; i++) {
                Minion joEBot = MinionPool.generateMinion("Jo-E Bot");
                if (minion.isGolden()) {
                    joEBot.setGolden(true);
                    joEBot.setAttack(2);
                    joEBot.setHealth(2);
                }
                summonMinion(boardState, joEBot, savePos);
                if (boardState == p1b) setP1nextAttacker(joEBot);
                if (boardState == p2b) setP2nextAttacker(joEBot);
                Game.appendToLeftTextArea("Mecharoo's deathrattle summoned Jo-E Bot\n");
            }
        }

        if (minion.getName().equals("Selfless Hero")) {
            int howOften = getDeathrattleMultiplier(boardState);
            for (int i  = 0; i < howOften; i++) {
                if (boardState.getBoardSize() > 0) {
                    int onceOrTwice = 1;
                    if(minion.isGolden()) onceOrTwice = 2;
                    for (int j = 0; j < onceOrTwice; j++) {
                        LinkedList<Minion> noDivineShielders = new LinkedList<>();
                        for (Minion m : boardState.getBoardMinions()) {
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
            int howOften = getDeathrattleMultiplier(boardState);
            for (int i  = 0; i < howOften; i++) {
                Minion damagedGolem = MinionPool.generateMinion("Damaged Golem");
                if (minion.isGolden()) {
                    damagedGolem.setGolden(true);
                    damagedGolem.setAttack(4);
                    damagedGolem.setHealth(2);
                }
                summonMinion(boardState, damagedGolem, savePos);
                if (boardState == p1b) setP1nextAttacker(damagedGolem);
                if (boardState == p2b) setP2nextAttacker(damagedGolem);
                Game.appendToLeftTextArea("Harvest Golem's deathrattle summoned Damaged Golem\n");
            }
        }

        if (minion.getName().equals("Bronze Warden")) {
            Minion bwr = MinionPool.generateMinion("Bronze Warden Reborn");
            boardState.playMinion(bwr, savePos, -1);                                        //explicitly not a summon
            if(boardState == p1b) setP1nextAttacker(bwr);
            if(boardState == p2b) setP2nextAttacker(bwr);
            Game.appendToLeftTextArea("Bronze Warden resurrected\n");
        }

    }

    public static void summonMinion(BoardState boardState, Minion minion, int pos) {
        if (boardState.getBoardSize() >= 7) return;

        LinkedList<Minion> murlocTidecallers = boardState.contains("Murloc Tidecaller");
        if (!murlocTidecallers.isEmpty() && (minion.getTribe() == Tribe.MURLOC || minion.getTribe() == Tribe.ALL)) {
            for (Minion mtc : murlocTidecallers) {
                if (!mtc.isGolden()) {
                    mtc.addAttack(1);
                } else {
                    mtc.addAttack(2);
                }
            }
        }

        boardState.playMinion(minion, pos, -1);
    }

    private static int getDeathrattleMultiplier(BoardState boardState) {

        LinkedList<Minion> Rivendares = boardState.contains("Baron Rivendare");
        if (!Rivendares.isEmpty()) {
            boolean golden = false;
            for (Minion rivendare : Rivendares) {
                if (rivendare.isGolden()) golden = true;
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
