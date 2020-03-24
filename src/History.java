import java.util.ArrayList;
import java.util.Collections;

public class History {
    private ArrayList<Player[][]> history = new ArrayList<>();

    public void generateMatchups(ArrayList<Player> players) {
        Collections.shuffle(players);
    }
}
