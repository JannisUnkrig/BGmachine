package AI;

public class HiddenLayer implements Layer {

    protected Layer previousLayer;
    protected Layer nextLayer;

    protected double[] biases;
    protected double[][] weightsOfOutgoingConnections;

    //for forward prop
    private double[] nodesOutputs;
    private double[] z;
    //for back prop
    private double[] biasesGradients = null;
    private double[] nodesOutputsGradients = null;
    private double[][] outgoingConnectionsWeightsGradients = null;


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
        return nextLayer.calcOutputs(MathHelper.multiply(weightsOfOutgoingConnections, inputs));
    }

    public void forwardPropagate(double[] inputs) {
        if(inputs.length != biases.length) throw new IllegalArgumentException();

        z            = new double[inputs.length];
        nodesOutputs = new double[inputs.length];

        for (int i = 0; i < inputs.length; i++) {
            inputs[i] += biases[i];
            z[i] = inputs[i];
            inputs[i] = Math.max(inputs[i], 0);
            nodesOutputs[i] = inputs[i];
        }

        nextLayer.forwardPropagate(MathHelper.multiply(weightsOfOutgoingConnections, nodesOutputs));
    }

    @Override
    public void backPropagate() {
        //all derivatives of ReLU of z
        double[] dReLU = new double[z.length];
        for (int i = 0; i < dReLU.length; i++) {
            dReLU[i] = 1;
            if (z[i] < 0) dReLU[i] = 0;
        }


        //biasGradients
        if(biasesGradients == null) biasesGradients = new double[nodesOutputs.length];
        //calculate gradient for biases (ReLU activation function)
        for (int i = 0; i < biasesGradients.length; i++) {
            biasesGradients[i] += dReLU[i] * nodesOutputsGradients[i];
        }


        //previous nodesOutputGradients
        double[] previousNodesOutputs = previousLayer.getNodesOutputs();
        double[] previousNodesOutputsGradients = new double[previousNodesOutputs.length];

        for (int i = 0; i < previousNodesOutputsGradients.length; i++) {
            previousNodesOutputsGradients[i] = 0;
            //buils a sum as any previous node influences every node in this layer
            for (int j = 0; j < nodesOutputsGradients.length; j++) {
                previousNodesOutputsGradients[i] += previousLayer.getWeightsOfOutgoingConnections()[j][i] * dReLU[j] * nodesOutputsGradients[j];
            }
        }
        if (previousLayer.getNodesOutputsGradients() == null) {
            previousLayer.setNodesOutputsGradients(previousNodesOutputsGradients);
        } else {
            previousLayer.addNodesOutputsGradients(previousNodesOutputsGradients);
        }

        //previous outgoingWeightsGradients
        double[][] previousWeightsOfOutgoingConnections = previousLayer.getWeightsOfOutgoingConnections();
        double[][] previousOutgoingConnectionsWeightsGradients = new double[previousWeightsOfOutgoingConnections.length][previousWeightsOfOutgoingConnections[0].length];

        for (int i = 0; i < nodesOutputsGradients.length; i++) {
            for (int j = 0; j < previousNodesOutputs.length; j++) {
                //ReLU activation function
                previousOutgoingConnectionsWeightsGradients[i][j] = previousNodesOutputs[j] * dReLU[i] * nodesOutputsGradients[i];
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
        for (int i = 0; i < weightsOfOutgoingConnections.length; i++) {
            for (int j = 0; j < weightsOfOutgoingConnections[0].length; j++) {
                weightsOfOutgoingConnections[i][j] -= learningRate * outgoingConnectionsWeightsGradients[i][j];
            }
        }
        nextLayer.updateWeightsAndBiases(learningRate);
    }

    @Override
    public void resetGradients() {
        biasesGradients = null;
        nodesOutputsGradients = null;
        outgoingConnectionsWeightsGradients = null;
        nextLayer.resetGradients();
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
    public double[][] getWeightsOfOutgoingConnections() {
        return weightsOfOutgoingConnections;
    }

    @Override
    public void setNodesOutputsGradients(double[] nodesOutputsGradients) {
        this.nodesOutputsGradients = nodesOutputsGradients;
    }

    @Override
    public void addNodesOutputsGradients(double[] nodesOutputsGradients) {
        for (int i = 0; i < nodesOutputsGradients.length; i++) {
            this.nodesOutputsGradients[i] += nodesOutputsGradients[i];
        }
    }

    @Override
    public double[] getNodesOutputsGradients() {
        return nodesOutputsGradients;
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
}
