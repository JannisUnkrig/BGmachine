package AI;

import java.util.Arrays;

public class InputLayer implements Layer {

    private int noOfInputs;
    protected Layer nextLayer = null;

    protected double[][] weightsOfOutgoingConnections = null;

    //for forward prop
    private double[] nodesOutputs;
    //for back prop
    private double[][] outgoingConnectionsWeightsGradients = null;


    /**connects to neighbor layers automatically, initializes weights randomly*/
    public InputLayer(int noOfInputs, Layer nextLayer) {
        this.noOfInputs = noOfInputs;
        connectToNextLayer(nextLayer);
    }

    /**doesnt connect to neighbor layers automatically, doesnt change weights*/
    public InputLayer(InputLayer toDeepCopy) {
        this.noOfInputs = toDeepCopy.noOfInputs;
        this.weightsOfOutgoingConnections = new double[toDeepCopy.weightsOfOutgoingConnections.length][toDeepCopy.weightsOfOutgoingConnections[0].length];
        for (int i = 0; i < toDeepCopy.weightsOfOutgoingConnections.length; i++) {
            System.arraycopy(toDeepCopy.weightsOfOutgoingConnections[i], 0, this.weightsOfOutgoingConnections[i], 0, toDeepCopy.weightsOfOutgoingConnections[0].length);
        }
    }



    private void connectToNextLayer(Layer nextLayer) {
        this.nextLayer = nextLayer;
        nextLayer.setPreviousLayer(this);
        weightsOfOutgoingConnections = new double[nextLayer.getBiases().length][noOfInputs];
        randomizeWeights();
    }

    private void randomizeWeights() {
        for (int i = 0; i < weightsOfOutgoingConnections.length; i++) {
            for (int j = 0; j < weightsOfOutgoingConnections[0].length; j++) {
                weightsOfOutgoingConnections[i][j] = Math.random() * 2 - 1;
            }
        }
    }

    @Override
    //useless in inputlayer as the input is a state which means encoded with ints
    public double[] calcOutputs(double[] inputs) { return null; }

    public double[] calcOutputs(int[] inputs) {
        if(inputs.length != noOfInputs) throw new IllegalArgumentException();

        double[] doubleVector = Arrays.stream(inputs).asDoubleStream().toArray();

        return nextLayer.calcOutputs(MathHelper.multiply(weightsOfOutgoingConnections, doubleVector));
    }


    @Override
    //useless in inputlayer as the input is a state which means encoded with ints
    public void forwardPropagate(double[] inputs) {}

    public void forwardPropagate(int[] inputs) {
        if(inputs.length != noOfInputs) throw new IllegalArgumentException();

        nodesOutputs = Arrays.stream(inputs).asDoubleStream().toArray();

        nextLayer.forwardPropagate(MathHelper.multiply(weightsOfOutgoingConnections, nodesOutputs));
    }


    @Override
    public void backPropagate() {
        //nothing to do
    }

    @Override
    public void updateWeightsAndBiases(double learningRate, int miniBatchSize) {
        for (int i = 0; i < weightsOfOutgoingConnections.length; i++) {
            for (int j = 0; j < weightsOfOutgoingConnections[0].length; j++) {
                weightsOfOutgoingConnections[i][j] -= learningRate * outgoingConnectionsWeightsGradients[i][j] / miniBatchSize;
            }
        }
        nextLayer.updateWeightsAndBiases(learningRate, miniBatchSize);
    }

    @Override
    public void resetGradients() {
        outgoingConnectionsWeightsGradients = null;
        nextLayer.resetGradients();
    }


    @Override
    public double[] getBiases() {
        return new double[0];
    }

    @Override
    //useless. there is no previous layer for input layer
    public void setPreviousLayer(Layer previousLayer) {}

    @Override
    public double[] getNodesOutputs() {
        return nodesOutputs;
    }

    @Override
    public double[][] getWeightsOfOutgoingConnections() {
        return weightsOfOutgoingConnections;
    }

    @Override
    //useless as no previous layer exists that would need them for calculations in backprop
    public void setNodesOutputsGradients(double[] nodesOutputsGradients) {}

    @Override
    //useless
    public void addNodesOutputsGradients(double[] nodesOutputsGradients) { }

    @Override
    //useless
    public double[] getNodesOutputsGradients() {
        return null;
    }

    @Override
    public void setOutgoingConnectionsWeightsGradients(double[][] outgoingConnectionsWeightsGradients) {
        this.outgoingConnectionsWeightsGradients = outgoingConnectionsWeightsGradients;
    }

    @Override
    public double[][] getOutgoingConnectionsWeightsGradients() {
        return outgoingConnectionsWeightsGradients;
    }

    @Override
    public void addOutgoingConnectionsWeightsGradients(double[][] outgoingConnectionsWeightsGradients) {
        for (int i = 0; i < outgoingConnectionsWeightsGradients.length; i++) {
            for (int j = 0; j < outgoingConnectionsWeightsGradients[0].length; j++) {
                this.outgoingConnectionsWeightsGradients[i][j] += outgoingConnectionsWeightsGradients[i][j];
            }
        }
    }

    @Override
    public String weightsAndBiasesAsString(String prefix) {
        String built = "\nInput Layer:\nWeights: (from: " + weightsOfOutgoingConnections[0].length + " to: " + weightsOfOutgoingConnections.length + ")\n";

        for (int i = 0; i < weightsOfOutgoingConnections.length; i++) {
            built += "[";
            for (int j = 0; j < weightsOfOutgoingConnections[0].length; j++) {
                built += this.weightsOfOutgoingConnections[i][j] + ", ";
            }
            built += "]\n";
        }

        return nextLayer.weightsAndBiasesAsString(built);
    }
}
