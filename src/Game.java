import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.LinkedList;

public class Game {

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
                    command.toLowerCase();

                    //control commands
                    if(command.equals("hjelp")) {
                        textAreaR.append("\nHjelp:\n\n" +
                                "Control commands:\n" +
                                "player x:\tChoose as which player you play. x = player number (1-8)\n" +
                                "timeup:\tsimulates shopping phase being over. Hence battles start and the next shopping\n\tphase begins afterwards.\n\n" +
                                "Non-Real commands:\n" +
                                "restart:\tRestarts the game.\n" +
                                "nextturn:\tStarts the next shopping phase for all players.\n" +
                                "battle x:\tBattle against another player. x = opponent's player number (1-8)\n" +
                                "battleall:\tAll players battle in semi-random matchups.\n\n" +
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


                    //player action commands
                    if(command.equals("level")) {
                        displayedPlayer.level();
                        updateDisplayedPlayer();
                        textAreaR.append("\nclicked level button (fails if to little gold or discovery running)\n");
                        return;
                    }

                    if(command.equals("roll")) {
                        displayedPlayer.roll();
                        updateDisplayedPlayer();
                        textAreaR.append("\nclicked roll button (fails if to little gold or discovery running)\n");
                        return;
                    }

                    if(command.equals("freeze")) {
                        displayedPlayer.freeze();
                        updateDisplayedPlayer();
                        textAreaR.append("\nclicked freeze button (fails is discovery running)\n");
                        return;
                    }

                    if(command.startsWith("buy ") && command.length() == 5 && Character.isDigit(command.charAt(4))) {
                        int nr = Integer.parseInt(command.substring(4));
                        displayedPlayer.buy(nr - 1);                                                   //positions start with 1 from outside
                        updateDisplayedPlayer();
                        textAreaR.append("\ntried buying minion at position " + nr + " (fails if to litte gold, hand full, invalid position or discovery running)\n");
                        return;
                    }

                    if(command.startsWith("sell ") && command.length() == 6 && Character.isDigit(command.charAt(5))) {
                        int nr = Integer.parseInt(command.substring(5));
                            displayedPlayer.sell(nr - 1);                                              //positions start with 1 from outside
                            updateDisplayedPlayer();
                            textAreaR.append("\ntried selling minion at position " + nr + " (fails if invalid position or discovery running)\n");
                            return;
                    }

                    if(command.startsWith("move ") && command.length() == 8 && Character.isDigit(command.charAt(5)) && command.charAt(6) == ' ' && Character.isDigit(command.charAt(7))) {
                        int nr1 = Integer.parseInt(command.substring(5, 6));
                        int nr2 = Integer.parseInt(command.substring(7));
                        displayedPlayer.move(nr1 - 1, nr2 - 1);                                              //positions start with 1 from outside
                        updateDisplayedPlayer();
                        textAreaR.append("\ntried moving minion from position " + nr1 + " to " + nr2 + " (fails if invalid positions or discovery running)\n");
                        return;
                    }

                    if (command.startsWith("play ")) {
                        if (command.length() == 10 && Character.isDigit(command.charAt(5)) && command.charAt(6) == ' ' && Character.isDigit(command.charAt(7)) && command.charAt(8) == ' ' && Character.isDigit(command.charAt(9))) {
                            int nr1 = Integer.parseInt(command.substring(5, 6));
                            int nr2 = Integer.parseInt(command.substring(7, 8));
                            int nr3 = Integer.parseInt(command.substring(9));
                            displayedPlayer.play(nr1 - 1, nr2 - 1, nr3 - 1);             //positions start with 1 from outside
                            updateDisplayedPlayer();
                            textAreaR.append("\ntried playing card from position " + nr1 + " in hand to position " + nr2 + " on board (fails if invalid positions, board full, invalid target or discovery running)\n");
                            return;
                        }

                        if (command.length() == 8 && Character.isDigit(command.charAt(5)) && command.charAt(6) == ' ' && Character.isDigit(command.charAt(7))) {
                            int nr1 = Integer.parseInt(command.substring(5, 6));
                            int nr2 = Integer.parseInt(command.substring(7));
                            displayedPlayer.play(nr1 - 1, nr2 - 1, -1);                  //positions start with 1 from outside
                            updateDisplayedPlayer();
                            textAreaR.append("\ntried playing card from position " + nr1 + " in hand to position " + nr2 + " on board (fails if invalid positions, board full, target required/invalid or discovery running)\n");
                            return;
                        }

                        if (command.length() == 6 && Character.isDigit(command.charAt(5))) {
                            int nr1 = Integer.parseInt(command.substring(5));
                            displayedPlayer.play(nr1 - 1, -1, -1);                       //positions start with 1 from outside
                            updateDisplayedPlayer();
                            textAreaR.append("\ntried playing card from position " + nr1 + " in hand (fails if used on a minion instead of a spell or discovery running)\n");
                            return;
                        }
                    }

                    if(command.startsWith("choose ") && command.length() == 8 && Character.isDigit(command.charAt(7))) {
                        int nr = Integer.parseInt(command.substring(7));
                        displayedPlayer.choose(nr - 1);                                              //positions start with 1 from outside
                        updateDisplayedPlayer();
                        textAreaR.append("\ntried choosing option " + nr + " (fails if invalid index or no discovery running)\n");
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

        Battler.setNextDeathWillBeRank(8);
        Battler.setDeathsLastTurn(new LinkedList<>());
        Battler.setCurRanking(null);
        Battler.calcCurrentRanking(players);
        Battler.generateMatchups(players);

        updateDisplayedPlayer();
    }

    private static void battleAndStartNextTurn() {
        Battler.battleAll(players);
        startNextTurn();
    }

    private static void startNextTurn() {
        Battler.calcCurrentRanking(players);
        Battler.generateMatchups(players);
        for (int i = 0; i < 8; i++) {
            players[i].newTurnReset();
        }
        updateDisplayedPlayer();
    }

    private static void generatePlayers() {
        for (int i = 0; i < 8; i++) {
            players[i] = new Player(i + 1);
            players[i].getMyShop().newTurnReset();
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
}
