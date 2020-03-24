import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;

public class Game {

    private static Player[] players = new Player[8];
    private static Player displayedPlayer;
    private static JTextArea textAreaR = new JTextArea(1, 40);
    private static JTextArea textAreaL = new JTextArea(1, 49);

    public static void main(String[] args) {

        JFrame f = new JFrame("Training Facility");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1000, 750);
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
                    if(command.startsWith("player ") && command.length() == 8 && command.charAt(6) == ' ' && Character.isDigit(command.charAt(7))) {
                        int nr = Integer.parseInt(command.substring(7));
                        if(nr >= 1 && nr <= 8) {
                            displayedPlayer = players[nr - 1];
                            updateDisplayedPlayer();
                            textAreaR.append("\nacting as player" + displayedPlayer.getPlayerNr() + "\n");
                            return;
                        }
                    }

                    if(command.equals("nextturn")) {
                        for (int i = 0; i < 8; i++) {
                            players[i].newTurnReset();
                        }
                        updateDisplayedPlayer();
                        textAreaR.append("\nstarted next turn\n");
                        return;
                    }

                    if(command.equals("restart")) {
                        startGame();
                        textAreaR.append("\nrestarted game\n");
                        return;
                    }


                    //player action commands
                    if(command.equals("level")) {
                        displayedPlayer.level();
                        updateDisplayedPlayer();
                        textAreaR.append("\nclicked level button\n");
                        return;
                    }

                    if(command.equals("roll")) {
                        displayedPlayer.roll();
                        updateDisplayedPlayer();
                        textAreaR.append("\nclicked roll button\n");
                        return;
                    }

                    if(command.equals("freeze")) {
                        displayedPlayer.freeze();
                        updateDisplayedPlayer();
                        textAreaR.append("\nclicked freeze button\n");
                        return;
                    }

                    if(command.startsWith("buy ") && command.length() == 5 && Character.isDigit(command.charAt(4))) {
                        int nr = Integer.parseInt(command.substring(4));
                        displayedPlayer.buy(nr - 1);                                                   //positions start with 1 from outside
                        updateDisplayedPlayer();
                        textAreaR.append("\ntried buying minion at position " + nr + " (fails if to litte gold, hand full, or invalid position)\n");
                        return;
                    }

                    if(command.startsWith("sell ") && command.length() == 6 && Character.isDigit(command.charAt(5))) {
                        int nr = Integer.parseInt(command.substring(5));
                            displayedPlayer.sell(nr - 1);                                              //positions start with 1 from outside
                            updateDisplayedPlayer();
                            textAreaR.append("\ntried selling minion at position " + nr + " (fails if invalid position)\n");
                            return;
                    }

                    if(command.startsWith("move ") && command.length() == 8 && Character.isDigit(command.charAt(5)) && command.charAt(6) == ' ' && Character.isDigit(command.charAt(7))) {
                        int nr1 = Integer.parseInt(command.substring(5, 6));
                        int nr2 = Integer.parseInt(command.substring(7));
                        displayedPlayer.move(nr1 - 1, nr2 - 1);                                              //positions start with 1 from outside
                        updateDisplayedPlayer();
                        textAreaR.append("\ntried moving minion from position " + nr1 + " to " + nr2 + " (fails if invalid positions)\n");
                        return;
                    }

                    if(command.startsWith("play ") && command.length() == 8 && Character.isDigit(command.charAt(5)) && command.charAt(6) == ' ' && Character.isDigit(command.charAt(7))) {
                        int nr1 = Integer.parseInt(command.substring(5, 6));
                        int nr2 = Integer.parseInt(command.substring(7));
                        displayedPlayer.play(nr1 - 1, nr2 - 1);                                              //positions start with 1 from outside
                        updateDisplayedPlayer();
                        textAreaR.append("\ntried playing minion from position " + nr1 + " in hand to position " + nr2 + " on board (fails if invalid positions or board full)\n");
                        return;
                    }


                    textAreaR.append("\ninvalid command\n");
                }
            }
        });
        //f.getContentPane().add(BorderLayout.EAST, textAreaR);
        JScrollPane scrollbar = new JScrollPane(textAreaR, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        f.getContentPane().add(BorderLayout.EAST, scrollbar);

        f.getContentPane().add(BorderLayout.WEST, textAreaL);

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
        updateDisplayedPlayer();
    }

    private static void generatePlayers() {
        for (int i = 0; i < 8; i++) {
            players[i] = new Player(i + 1);
            players[i].getMyShop().newTurnReset();
        }
    }

    private static void updateDisplayedPlayer() {
        textAreaL.setText(displayedPlayer.toString());
    }
}
