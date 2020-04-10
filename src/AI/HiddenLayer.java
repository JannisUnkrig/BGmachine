package AI;

public class HiddenLayer implements Layer {

    protected Layer previousLayer;
    protected Layer nextLayer;

    protected double[] biases;
    protected double[][] weightsOfOutgoingConnections;


    /**connects to neighbor layers automatically, initializes weights and biases randomly*/
    public HiddenLayer(int nodes, Layer nextLayer) {
        biases = new double[nodes];
        connectToNextLayer(nextLayer);
    }

    /**doesnt connect to neighbor layers automatically, doesnt change weights or biases*/
    public HiddenLayer(HiddenLayer toDeepCopy) {
        this.biases = new double[toDeepCopy.biases.length];
        System.arraycopy(toDeepCopy.biases, 0, this.biases, 0, toDeepCopy.biases.length);

        this.weightsOfOutgoingConnections = new double[toDeepCopy.weightsOfOutgoingConnections.length][toDeepCopy.weightsOfOutgoingConnections[0].length];
        for (int i = 0; i < toDeepCopy.weightsOfOutgoingConnections.length; i++) {
            System.arraycopy(toDeepCopy.weightsOfOutgoingConnections[i], 0, this.weightsOfOutgoingConnections[i], 0, toDeepCopy.weightsOfOutgoingConnections[0].length);
        }
    }



    private void connectToNextLayer(Layer nextLayer) {
        this.nextLayer = nextLayer;
        nextLayer.setPreviousLayer(this);
        weightsOfOutgoingConnections = new double[nextLayer.getBiases().length][this.biases.length];
        randomizeWeightsAndBiases();
    }

    private void randomizeWeightsAndBiases() {
        for (int i = 0; i < weightsOfOutgoingConnections.length; i++) {
            for (int j = 0; j < weightsOfOutgoingConnections[0].length; j++) {
                weightsOfOutgoingConnections[i][j] = Math.random() * 2 - 1;
            }
        }
        for (int i = 0; i < biases.length; i++) {
            biases[i] = Math.random() * 2 - 1;
        }
    }

    @Override
    public double[] calcOutputs(double[] inputs) {
        if(inputs.length != biases.length) throw new IllegalArgumentException();

        for (int i = 0; i < inputs.length; i++) {
            inputs[i] += biases[i];
            inputs[i] = Math.max(inputs[i], 0);
        }
        return nextLayer.calcOutputs(MatrixVectorMultiply.multiply(weightsOfOutgoingConnections, inputs));
    }



    @Override
    public double[] getBiases() {
        return biases;
    }

    @Override
    public void setPreviousLayer(Layer previousLayer) {
        this.previousLayer = previousLayer;
    }
}
