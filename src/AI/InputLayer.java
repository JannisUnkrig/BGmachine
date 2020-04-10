package AI;

import java.util.Arrays;

public class InputLayer implements Layer {

    private int noOfInputs;
    protected Layer nextLayer = null;

    protected double[][] weightsOfOutgoingConnections = null;


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
    public double[] calcOutputs(double[] inputs) {
        return null;
    }

    public double[] calcOutputs(int[] inputs) {
        if(inputs.length != noOfInputs) throw new IllegalArgumentException();

        double[] doubleVector = Arrays.stream(inputs).asDoubleStream().toArray();

        return nextLayer.calcOutputs(MatrixVectorMultiply.multiply(weightsOfOutgoingConnections, doubleVector));
    }


    @Override
    public double[] getBiases() {
        return new double[0];
    }

    @Override
    public void setPreviousLayer(Layer previousLayer) {}        //doesnt need to do anything
}
