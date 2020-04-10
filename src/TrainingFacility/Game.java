package TrainingFacility;

import AI.AgentV1;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.LinkedList;

public class Game {

    private static boolean running = false;

    private static AgentV1[] agentV1s = new AgentV1[8];
    private static Player[] players = new Player[8];
    private static Player displayedPlayer;
    private static JTextArea textAreaR = new JTextArea(1, 50);
    private static JTextArea textAreaL = new JTextArea(1, 45);

    public static void main(String[] args) {

        JFrame f = new JFrame("Training Facility");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1100, 750);
        f.setLocation(300, 200);

        //console
        textAreaR.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    //get last line
                    int end = textAreaR.getDocument().getLength();
                    int start = 0;
                    String command = "";
                    try {
                        start = Utilities.getRowStart(textAreaR, end);
                        while (start == end) {
                            end--;
                            start = Utilities.getRowStart(textAreaR, end);
                        }
                        command = textAreaR.getText(start, end - start);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }

                    //react to different commands
                    command = command.toLowerCase();

                    //control commands
                    if(command.equals("hjelp")) {
                        textAreaR.append("\nHjelp:\n\n" +
                                "Control commands:\n" +
                                "player x:\tChoose as which player you play. x = player number (1-8)\n" +
                                "timeup:\tsimulates shopping phase being over. Hence battles start and the next shopping\n\tphase begins afterwards.\n" +
                                "restart:\tRestarts the game. The A.I. stays untouched.\n\n" +
                                "Non-Real commands:\n" +
                                "nextturn:\tStarts the next shopping phase for all players.\n" +
                                "battle x:\tBattle against another player. x = opponent's player number (1-8)\n" +
                                "battleall:\tAll players battle in semi-random matchups.\n" +
                                "get x:\tAdds a minion to hand. Doesn't affect pool. x = minion's name\n\n" +
                                "A.I.V1 commands: (only for turn one)\n" +
                                "aiv1move:\tLet the current players's A.I. agent make a move.\n" +
                                "aiv1turn:\tLet the A.I.V1 play a whole turn.\n" +
                                "aiv1turns x:\tLet the A.I.V1 play x turn ones and restart the game right after to gather\n\ttraining data. x = number of episodes" +
                                "Player's commands:\n" +
                                "level:\tGo to next Tavern Tier.\n" +
                                "roll:\tReroll Bob's offers.\n" +
                                "freeze:\tFreeze Bob's offers.\n" +
                                "buy x:\tBuy one of Bob's minions. x = index of bought minion\n" +
                                "sell x:\tSell one of your minions. x = index of sold minion\n" +
                                "move x y:\tMove one of your minions. x = old index of minion, y = new index for minion\n" +
                                "play x y z:\tPlay a card in your hand. x = index of played card,\n" +
                                "\ty = index for future position on board (required only for minions),\n" +
                                "\tz = index of battlecry target (only for targetable battlecries required)\n" +
                                "choose x:\tChoose an option in a discovery. x = index of option\n");
                        return;
                    }

                    if(command.startsWith("player ") && command.length() == 8 && command.charAt(6) == ' ' && Character.isDigit(command.charAt(7))) {
                        int nr = Integer.parseInt(command.substring(7));
                        if(nr >= 1 && nr <= 8) {
                            displayedPlayer = players[nr - 1];
                            updateDisplayedPlayer();
                            textAreaR.append("\nacting as player" + displayedPlayer.getPlayerNr() + "\n");
                            return;
                        }
                    }

                    if(command.equals("timeup")) {
                        battleAndStartNextTurn();
                        textAreaR.append("\neveryone battled and a new turn started\n");
                        return;
                    }

                    if(command.equals("restart")) {
                        startGame();
                        textAreaR.append("\nrestarted game\n");
                        return;
                    }

                    if(command.equals("nextturn")) {
                        startNextTurn();
                        textAreaR.append("\nstarted next turn\n");
                        return;
                    }

                    if(command.startsWith("battle ") && command.length() == 8 && Character.isDigit(command.charAt(7))) {
                        int nr = Integer.parseInt(command.substring(7));
                        Battler.battle(displayedPlayer, players[nr - 1]);
                        textAreaR.append("\nbattled player " + nr + "\n");
                        return;
                    }

                    if(command.equals("battleall")) {
                        Battler.battleAll(players);
                        textAreaR.append("\neveryone battled someone\n");
                        return;
                    }

                    if(command.startsWith("get ")) {
                        String name = command.substring(4);
                        Minion m;
                        try {
                            m = cheatMinionFor(displayedPlayer, name);
                        } catch (RuntimeException e) {
                            textAreaR.append("\ninvalid name\n");
                            return;
                        }
                        displayedPlayer.checkForTriple(m);
                        updateDisplayedPlayer();
                        return;
                    }


                    //A.I.commands
                    if(command.equals("aiv1move")) {
                        agentV1PlaysMove(displayedPlayer);
                        updateDisplayedPlayer();
                        textAreaR.append(" (Player " + displayedPlayer.getPlayerNr() + "'s A.I. agent played this move)\n");
                        return;
                    }

                    if(command.equals("aiv1turn")) {
                        agentV1PlaysRound();
                        updateDisplayedPlayer();
                        textAreaR.append("\nA complete turn was played by the A.I.\n");
                        return;
                    }

                    if(command.startsWith("aiv1turns ") && command.length() == 11 && Character.isDigit(command.charAt(10))) {
                        int nr = Integer.parseInt(command.substring(10));
                        agentV1PlaysRounds(nr);
                        textAreaR.append("\n" + nr + " complete turn ones were played by the A.I.\n");
                        return;
                    }

                    if(command.startsWith("aiv1improve ") && command.length() >= 13 && Character.isDigit(command.charAt(12))) {
                        int nr = Integer.parseInt(command.substring(12));
                        AgentV1.improve(nr);
                        textAreaR.append("\nA.I.V1 improved itself " + nr + " times\n");
                        return;
                    }


                    //player action commands
                    if(command.equals("level")) {
                        displayedPlayer.level();
                        updateDisplayedPlayer();
                        textAreaR.append("\n");
                        return;
                    }

                    if(command.equals("roll")) {
                        displayedPlayer.roll();
                        updateDisplayedPlayer();
                        textAreaR.append("\n");
                        return;
                    }

                    if(command.equals("freeze")) {
                        displayedPlayer.freeze();
                        updateDisplayedPlayer();
                        textAreaR.append("\n");
                        return;
                    }

                    if(command.startsWith("buy ") && command.length() == 5 && Character.isDigit(command.charAt(4))) {
                        int nr = Integer.parseInt(command.substring(4));
                        displayedPlayer.buy(nr);
                        updateDisplayedPlayer();
                        textAreaR.append("\n");
                        return;
                    }

                    if(command.startsWith("sell ") && command.length() == 6 && Character.isDigit(command.charAt(5))) {
                        int nr = Integer.parseInt(command.substring(5));
                        displayedPlayer.sell(nr);
                        updateDisplayedPlayer();
                        textAreaR.append("\n");
                        return;
                    }

                    if(command.startsWith("move ") && command.length() == 8 && Character.isDigit(command.charAt(5)) && command.charAt(6) == ' ' && Character.isDigit(command.charAt(7))) {
                        int nr1 = Integer.parseInt(command.substring(5, 6));
                        int nr2 = Integer.parseInt(command.substring(7));
                        displayedPlayer.move(nr1, nr2);
                        updateDisplayedPlayer();
                        textAreaR.append("\n");
                        return;
                    }

                    if (command.startsWith("play ")) {
                        if (command.length() == 10 && Character.isDigit(command.charAt(5)) && command.charAt(6) == ' ' && Character.isDigit(command.charAt(7)) && command.charAt(8) == ' ' && Character.isDigit(command.charAt(9))) {
                            int nr1 = Integer.parseInt(command.substring(5, 6));
                            int nr2 = Integer.parseInt(command.substring(7, 8));
                            int nr3 = Integer.parseInt(command.substring(9));
                            displayedPlayer.play(nr1, nr2, nr3);
                            updateDisplayedPlayer();
                            textAreaR.append("\n");
                            return;
                        }

                        if (command.length() == 8 && Character.isDigit(command.charAt(5)) && command.charAt(6) == ' ' && Character.isDigit(command.charAt(7))) {
                            int nr1 = Integer.parseInt(command.substring(5, 6));
                            int nr2 = Integer.parseInt(command.substring(7));
                            displayedPlayer.play(nr1, nr2, -1);
                            updateDisplayedPlayer();
                            textAreaR.append("\n");
                            return;
                        }

                        if (command.length() == 6 && Character.isDigit(command.charAt(5))) {
                            int nr1 = Integer.parseInt(command.substring(5));
                            displayedPlayer.play(nr1, 0, -1);
                            updateDisplayedPlayer();
                            textAreaR.append("\n");
                            return;
                        }
                    }

                    if(command.startsWith("choose ") && command.length() == 8 && Character.isDigit(command.charAt(7))) {
                        int nr = Integer.parseInt(command.substring(7));
                        displayedPlayer.choose(nr);
                        updateDisplayedPlayer();
                        textAreaR.append("\n");
                        return;
                    }

                    textAreaR.append("\ninvalid command\n");
                }
            }
        });
        //f.getContentPane().add(BorderLayout.EAST, textAreaR);
        JScrollPane scrollbar = new JScrollPane(textAreaR, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        f.getContentPane().add(BorderLayout.EAST, scrollbar);

        //f.getContentPane().add(BorderLayout.WEST, textAreaL);
        JScrollPane scrollbar2 = new JScrollPane(textAreaL, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        f.getContentPane().add(BorderLayout.WEST, scrollbar2);

        startGame();

        /*
        final JButton button = new JButton("Click Me");
        f.getContentPane().add(BorderLayout.SOUTH, button);
        button.addActionListener(e -> textAreaR.append("Button was clicked\n"));
        */
        f.setVisible(true);
    }



    private static void startGame() {
        try {
            MinionPool.extractMinionsFromFile();
        } catch (FileNotFoundException e) {
            System.out.println("minion file not found\nshutting down");
            return;
        }

        generatePlayers();
        displayedPlayer = players[0];
        generateOrAssignAgentsV1();

        Battler.setNextDeathWillBeRank(8);
        Battler.setDeathsLastTurn(new LinkedList<>());
        Battler.setCurRanking(null);
        Battler.calcCurrentRanking(players);
        Battler.generateMatchups(players);

        updateDisplayedPlayer();
        running = true;
    }

    private static void battleAndStartNextTurn() {
        Battler.battleAll(players);
        startNextTurn();
    }

    private static void startNextTurn() {
        Battler.calcCurrentRanking(players);
        Battler.generateMatchups(players);
        boolean gameDone = false;
        for (int i = 0; i < 8; i++) {
            players[i].newTurnReset();
            if (players[i].getDefeatedAsPlace() == 2) gameDone = true;
        }
        updateDisplayedPlayer();
        if (gameDone) {
            running = false;
            textAreaR.append("\n\nGame completed!\n\n" + Battler.getRankingAsString() + "\n");
        }
    }

    private static Minion cheatMinionFor(Player p, String whichOne) {
        Minion m = MinionPool.generateMinion(whichOne);
        textAreaR.append("\ngenerated " + whichOne + "\n");
        p.getHandCards().add(m);
        return m;
    }

    private static void generatePlayers() {
        for (int i = 0; i < 8; i++) {
            players[i] = new Player(i + 1);
            players[i].getMyShop().newTurnReset();
        }
    }

    private static void generateOrAssignAgentsV1() {
        if (agentV1s[0] == null) {
            for (int i = 0; i < 8; i++) {
                agentV1s[i] = new AgentV1(players[i]);
            }
        } else {
            for (int i = 0; i < 8; i++) {
                agentV1s[i].setAgentPlaysAs(players[i]);
            }
        }
    }

    private static void agentV1PlaysMove(Player player) {
        int index = -1;
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                index = i;
                break;
            }
        }
        agentV1s[index].makeAMove();
    }

    private static void agentV1PlaysRound() {
        for (AgentV1 agent : agentV1s) {
            agent.setDone(false);
        }

        boolean atleastOneStillGoing = true;
        while (atleastOneStillGoing) {
            //all agents make a move except they're done
            for (AgentV1 agent : agentV1s) {                        //TODO fix: this is biased for first players
                if (!agent.isDone()) {
                    int oldHealth = agent.getAgentPlaysAs().getHealth();
                    agent.makeAMove();
                    //last moves (agent done) are handled after combat to get correct reward
                    if (!agent.isDone()) {
                        //SARS gets completed and stored in ERS
                        agent.setEndStateOfCurSARS(0);

                        int newHealt = agent.getAgentPlaysAs().getHealth();
                        int reward = (newHealt - oldHealth) * 7000;
                        agent.addToRewardOfCurSARS(reward);
                        agent.saveCurSARStoERS();
                    }
                }
            }
            atleastOneStillGoing = false;
            for (AgentV1 agent : agentV1s) {
                if (!agent.isDone()) {
                    atleastOneStillGoing = true;
                    break;
                }
            }
        }

        battleAndStartNextTurn();

        //SARS for last move gets completed and stored in ERS
        for (AgentV1 agent : agentV1s) {
            Player player = agent.getAgentPlaysAs();

            agent.setEndStateOfCurSARS(1);

            int reward = player.getLastDamageTaken() * -7000;
            reward += player.getLastOpponent().getLastDamageTaken() * 1000;
            agent.addToRewardOfCurSARS(reward);

            agent.saveCurSARStoERS();
        }
    }

    private static void agentV1PlaysRounds(int howMany) {
        if (howMany < 1) {
            Game.appendToRightTextArea("\nmodifier must be > 0\n");
            return;
        }
        for (int i = 0; i < howMany; i++) {
            agentV1PlaysRound();
            startGame();
        }
    }

    private static void updateDisplayedPlayer() {
        textAreaL.setText(Battler.getRankingAsString() + "\n\n" + Battler.getNextOpponentAsString(displayedPlayer) +
                "\n\n   ---------------------------------------------------------------------------------------------------------------------\n\n"
                + displayedPlayer.toString() +
                "\n   ---------------------------------------------------------------------------------------------------------------------");
    }

    public static void appendToLeftTextArea(String s) {
        textAreaL.append(s);
    }

    public static void appendToRightTextArea(String s) {
        textAreaR.append(s);
    }
}
