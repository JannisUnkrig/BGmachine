package AI;

import java.util.Arrays;

public class OutputLayer implements Layer {

    protected Layer previousLayer = null;

    private double[] biases;

    //for forward prop
    private double[] nodesOutputs;
    private double[] z;
    //for back prop
    private double[] biasesGradients = null;


    /**connects to neighbor layers automatically, initializes biases randomly*/
    public OutputLayer(int nodes) {
        biases = new double[nodes];
        randomizeBiases();
    }

    /**doesnt connect to neighbor layers automatically, doesnt change biases*/
    public OutputLayer(OutputLayer toDeepCopy) {
        this.biases = new double[toDeepCopy.biases.length];
        System.arraycopy(toDeepCopy.biases, 0, this.biases, 0, toDeepCopy.biases.length);
    }



    private void randomizeBiases() {
        for (int i = 0; i < biases.length; i++) {
            biases[i] = Math.random() * 2 - 1;
        }
    }

    @Override
    public double[] calcOutputs(double[] inputs) {
        if(inputs.length != biases.length) throw new IllegalArgumentException();

        for (int i = 0; i < inputs.length; i++) {
            inputs[i] += biases[i];
            //no activation function
        }
        return inputs;
    }

    public void forwardPropagate(double[] inputs) {
        if(inputs.length != biases.length) throw new IllegalArgumentException();

        z            = new double[inputs.length];
        nodesOutputs = new double[inputs.length];

        for (int i = 0; i < inputs.length; i++) {
            inputs[i] += biases[i];
            z[i] = inputs[i];
            //no activation function
            nodesOutputs[i] = inputs[i];
        }
    }

    @Override
    //useless
    public void backPropagate() {}

    public void backPropagate(int chosenAction, double targetForChosenAction) {
        double predictionForChosenAction = nodesOutputs[chosenAction];

        //biasGradients
        if(biasesGradients == null) biasesGradients = new double[nodesOutputs.length];
        //no gradient for the predictions for all non-chosen actions can be calculated
        //calculate gradient for chosen actions gradient (no activation function)
        biasesGradients[chosenAction] += 2 * (predictionForChosenAction - targetForChosenAction);


        //previous nodesOutputGradients
        double[] previousNodesOutputs = previousLayer.getNodesOutputs();
        double[] previousNodesOutputsGradients = new double[previousNodesOutputs.length];

        for (int i = 0; i < previousNodesOutputsGradients.length; i++) {
            /*this previous nodeOutput influences all outputs, but the prediction - target part cancels itself out for all outputs that aren't the chosen
            action, as i dont have target values for these and thus assume the prediction is 100% correct (equal to target) (no activation function)*/
            previousNodesOutputsGradients[i] = previousLayer.getWeightsOfOutgoingConnections()[chosenAction][i] * 2 * (predictionForChosenAction - targetForChosenAction);
        }
        if (previousLayer.getNodesOutputsGradients() == null) {
            previousLayer.setNodesOutputsGradients(previousNodesOutputsGradients);
        } else {
            previousLayer.addNodesOutputsGradients(previousNodesOutputsGradients);
        }

        //previous outgoingWeightsGradients
        double[][] previousWeightsOfOutgoingConnections = previousLayer.getWeightsOfOutgoingConnections();
        double[][] previousOutgoingConnectionsWeightsGradients = new double[previousWeightsOfOutgoingConnections.length][previousWeightsOfOutgoingConnections[0].length];

        for (int i = 0; i < previousOutgoingConnectionsWeightsGradients.length; i++) {
            if (i == chosenAction) {
                for (int j = 0; j < previousOutgoingConnectionsWeightsGradients[0].length; j++) {
                    //no activation function
                    previousOutgoingConnectionsWeightsGradients[i][j] = previousNodesOutputs[j] * 2 * (predictionForChosenAction - targetForChosenAction);
                }
            } else {
                Arrays.fill(previousOutgoingConnectionsWeightsGradients[i], 0);
            }
        }
        if (previousLayer.getOutgoingConnectionsWeightsGradients() == null) {
            previousLayer.setOutgoingConnectionsWeightsGradients(previousOutgoingConnectionsWeightsGradients);
        } else {
            previousLayer.addOutgoingConnectionsWeightsGradients(previousOutgoingConnectionsWeightsGradients);
        }

        //back prop in next layer
        previousLayer.backPropagate();
    }

    @Override
    public void updateWeightsAndBiases(double learningRate) {
        for (int i = 0; i < biases.length; i++) {
            biases[i] -= learningRate * biasesGradients[i];
        }
    }

    @Override
    public void resetGradients() {
        biasesGradients = null;
    }



    @Override
    public double[] getBiases() {
        return biases;
    }

    @Override
    public void setPreviousLayer(Layer previousLayer) {
        this.previousLayer = previousLayer;
    }

    @Override
    public double[] getNodesOutputs() {
        return nodesOutputs;
    }

    @Override
    //useless as they don't exist
    public double[][] getWeightsOfOutgoingConnections() { return null; }

    @Override
    //useless as this is handled by the target
    public void setNodesOutputsGradients(double[] nodesOutputsGradients) { }

    @Override
    //useless
    public void addNodesOutputsGradients(double[] nodesOutputsGradients) { }

    @Override
    //useless
    public double[] getNodesOutputsGradients() { return null; }

    @Override
    //useless as they don't exist
    public void setOutgoingConnectionsWeightsGradients(double[][] outgoingConnectionsWeightsGradients) { }

    @Override
    //useless
    public double[][] getOutgoingConnectionsWeightsGradients() { return null; }

    @Override
    //useless
    public void addOutgoingConnectionsWeightsGradients(double[][] outgoingConnectionsWeightsGradients) { }
}
