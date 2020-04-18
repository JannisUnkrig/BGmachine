package AI;

import TrainingFacility.Card;
import TrainingFacility.Game;
import TrainingFacility.Minion;
import TrainingFacility.Player;

import java.util.Arrays;
import java.util.LinkedList;

/** plays turn one.
 * inputs: gold, shop minion 1-3, hand minion 1, board minion 1-2
 * outputs: roll, freeze, buy 1-3, sell 1-2, play, pass turn
 * rewards: illegal move: -3, freeze:-1, take one damage: -700, deal one damage: 100*/

public class AgentV1 {

    private static double epsilon = 0.5;                                //how often the nn chooses randomly (0 = no randomness, 1 = absolut randomness)
    private static double learningRate = 0.01;                          //aka alpha
    private static final int ERSSize = 10000;
    private static final double unusualSampleFactor = 0.1;             //how much samples with very high/low rewards are prefered in learning phase (0.0000001 = ~100% bias for highest reward, 1 no biasing)
    private static final int miniBatchSize = 10;
    private static final int numberOfImprovementsUntilTargetNetworkGetsUpdated = 20;
    private static int targetUpdateCounter = 0;
    private static final double discountFactor = 0.99;                   //aka gamma; how much the predicted future value gets factored in. 1 = completly, 0 = not at all

    //update calcCurState and makeAMove when adjusting the following constants
    private static final int inputNodes = 7;
    private static final int[] hiddenLayersNodes = new int[] {8, 9};
    private static final int outputNodes = 9;


    private static NeuralNet collectiveBrain = new NeuralNet(inputNodes, hiddenLayersNodes, outputNodes);
    private static NeuralNet targetNetwork = new NeuralNet(collectiveBrain);
    private static int[][] experienceReplayStorage = new int[ERSSize][];
    private static int howFullIsERS = 0;
    private static int nextERSIndex = 0;
    private static double errorAccumulatorForOneImproveSession = 0;                          //just for monitoring


    private static void addToERS(int[] sars) {
        //if (sars[7] == -1) System.out.println("ERS[" + nextERSIndex + "] = " + Arrays.toString(sars));
        experienceReplayStorage[nextERSIndex] = sars;
        nextERSIndex++;
        if (nextERSIndex >= ERSSize) nextERSIndex = 0;

        if (howFullIsERS < ERSSize) howFullIsERS++;
    }

    public static void improve(int howOften) {
        //biasing towards very high/low rewards
        //sorting high/low rewards first
        int[][] sortedERS = new int[howFullIsERS][];
        System.arraycopy(experienceReplayStorage, 0, sortedERS, 0, howFullIsERS);
        Arrays.sort(sortedERS, (ints, ints2) -> {
            if (Math.abs(ints[inputNodes + 1]) < Math.abs(ints2[inputNodes + 1])) return 1;
            else if (Math.abs(ints[inputNodes + 1]) == Math.abs(ints2[inputNodes + 1])) return 0;
            return -1;
        });

        errorAccumulatorForOneImproveSession = 0;

        for (int j = 0; j < howOften; j++) {
            for (int i = 0; i < miniBatchSize; i++) {
                //bias the sorted ERS
                double d = Math.random();
                d = Math.pow(d, 1/unusualSampleFactor);
                int idx = (int) (d * sortedERS.length);
                int[] sample = sortedERS[idx];


                //state at time t
                int[] state1 = new int[inputNodes];
                System.arraycopy(sample, 0, state1, 0, inputNodes);

                //action at time t
                int action = sample[inputNodes];

                //reward for action at time t
                int reward = sample[inputNodes + 1];

                //state at time t+1 (only relevant if not a terminal state)
                int[] state2 = new int[inputNodes];
                System.arraycopy(sample, inputNodes + 2, state2, 0, inputNodes);

                //state at time t terminal state?
                boolean terminal = false;
                if (sample[sample.length - 1] > 0) terminal = true;


                //target for the prediction for the chosen action (==reward + discountFactor * max reward of state2)
                double targetForChosenAction;
                //terminal state
                if (terminal) {
                    targetForChosenAction = reward;
                }
                //not a terminal state
                else {
                    double[] evaluations = targetNetwork.calcOutputs(state2);
                    double biggestFutureReward = Double.MIN_VALUE;
                    for (double evaluation : evaluations) {
                        if (evaluation > biggestFutureReward) biggestFutureReward = evaluation;
                    }
                    targetForChosenAction = reward + discountFactor * (biggestFutureReward);
                }

                //prediction for chosen action in state1
                collectiveBrain.forwardPropagate(state1);

                collectiveBrain.backPropagate(action, targetForChosenAction);
            }

            //update collectiveBrain
            collectiveBrain.updateWeightsAndBiases(learningRate, miniBatchSize);

            //reset gradient tracker
            collectiveBrain.resetGradients();

            targetUpdateCounter++;
            if (targetUpdateCounter >= numberOfImprovementsUntilTargetNetworkGetsUpdated) {
                targetUpdateCounter = 0;
                targetNetwork = new NeuralNet(collectiveBrain);
            }
        }
    }

    public static void setEpsilon(double newEpsilon) {
        epsilon = newEpsilon;
    }

    public static double getEpsilon() {
        return epsilon;
    }

    public static double getLearningRate() {
        return learningRate;
    }

    public static void setLearningRate(double learningRate) {
        AgentV1.learningRate = learningRate;
    }

    public static String getBrainAsString() {
        return collectiveBrain.weightsAndBiasesAsString();
    }

    public static void addToErrorAccumulatorForOneImproveSession(double howMuch) {
        errorAccumulatorForOneImproveSession += howMuch;
    }

    public static void setErrorAccumulatorForOneImproveSession(double to) {
        errorAccumulatorForOneImproveSession = to;
    }

    public static double getErrorAccumulatorForOneImproveSession() {
        return errorAccumulatorForOneImproveSession;
    }

    public static double getMiniBatchSize() {
        return miniBatchSize;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    private Player agentPlaysAs;
    private int[] curState = null;
    private boolean done = false;
    private int[] curSARS = new int[inputNodes+1+1+inputNodes+1];


    public AgentV1(Player playsAs) {
        this.agentPlaysAs = playsAs;
    }


    public void makeAMove() {
        if (curState == null) curState = calcCurState();

        double[] outputs = collectiveBrain.calcOutputs(curState);

        int indexOfChosenAction = 1234;
        boolean rng = false;

        if (Math.random() >= epsilon) {
            double biggest = 1234;
            for (int i = 0; i < outputs.length; i++) {
                if (i== 0 || outputs[i] > biggest) {
                    indexOfChosenAction = i;
                    biggest = outputs[i];
                }
            }

        } else {
            indexOfChosenAction = (int) (Math.random() * ((double) outputNodes));
            rng = true;
        }


        boolean legal = false;
        if      (indexOfChosenAction == 0) legal = agentPlaysAs.roll();
        else if (indexOfChosenAction == 1) legal = agentPlaysAs.freeze();
        else if (indexOfChosenAction == 2) legal = agentPlaysAs.buy(0);
        else if (indexOfChosenAction == 3) legal = agentPlaysAs.buy(1);
        else if (indexOfChosenAction == 4) legal = agentPlaysAs.buy(2);
        else if (indexOfChosenAction == 5) legal = agentPlaysAs.sell(0);
        else if (indexOfChosenAction == 6) legal = agentPlaysAs.sell(1);
        else if (indexOfChosenAction == 7) legal = agentPlaysAs.play(0, 0, -1);
        else if (indexOfChosenAction == 8) {
            legal = true;
            done = true;
            if (Game.logPlayersActions) Game.appendToRightTextArea("\nPlayer " + agentPlaysAs.getPlayerNr() + " decided he's done with his turn (successful)");
        }
        if (Game.logPlayersActions && rng) Game.appendToRightTextArea(" (randomly)");


        int[] newSARS = new int[inputNodes+1+1+inputNodes+1];

        //state
        System.arraycopy(curState, 0, newSARS, 0, inputNodes);

        //action
        newSARS[inputNodes] = indexOfChosenAction;

        //rewards further modified later
        if (!legal)                     newSARS[inputNodes + 1] = 0;        //try to stop illegal moves
        if (indexOfChosenAction == 1)  newSARS[inputNodes + 1] = 0;         //try to stop ai from freezing over and over
        if (indexOfChosenAction != 8)  newSARS[inputNodes + 1] = -1;        //try to stop ai from never ending turn

        //new state
        curState = calcCurState();
        System.arraycopy(curState, 0, newSARS, inputNodes + 1 + 1, inputNodes);

        //end state (?) added later

        curSARS = newSARS;
    }

    public void addToRewardOfCurSARS(int reward) {
        curSARS[inputNodes + 1] += reward;
    }

    public void setEndStateOfCurSARS(int reward) {
        curSARS[inputNodes + 1 + 1 + inputNodes] = reward;
    }

    public void saveCurSARStoERS() {
        addToERS(curSARS);
    }


    private int[] calcCurState() {
        int[] encodedState = new int[inputNodes];

        //encode gold
        encodedState[0] = agentPlaysAs.getCurGold();

        //encode shop
        LinkedList<Minion> offers = agentPlaysAs.getMyShop().getOffers();
        int size = offers.size();
        for (int i = 0; i < size; i++) {
            encodedState[i + 1] = offers.get(i).getId();
        }

        //encode hand
        LinkedList<Card> hand = agentPlaysAs.getHandCards();
        int size2 = hand.size();
        for (int i = 0; i < size2; i++) {
            encodedState[i + 4] = ((Minion) hand.get(i)).getId();
        }

        //encode board
        LinkedList<Minion> board = agentPlaysAs.getMyBoard().getBoardMinions();
        int size3 = board.size();
        for (int i = 0; i < size3; i++) {
            encodedState[i + 5] = board.get(i).getId();
        }

        return encodedState;
    }


    public void setAgentPlaysAs(Player agentPlaysAs) {
        this.agentPlaysAs = agentPlaysAs;
        curState = calcCurState();
    }

    public Player getAgentPlaysAs() {
        return agentPlaysAs;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
