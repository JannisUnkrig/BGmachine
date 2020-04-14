package AI;

public interface Layer {

    double[] getBiases();

    void setPreviousLayer(Layer previousLayer);

    double[] calcOutputs(double[] inputs);

    void forwardPropagate(double[] inputs);

    double[] getNodesOutputs();

    double[][]  getWeightsOfOutgoingConnections();

    void setNodesOutputsGradients(double[] nodesOutputsGradients);

    void addNodesOutputsGradients(double[] nodesOutputsGradients);

    double[] getNodesOutputsGradients();

    void setOutgoingConnectionsWeightsGradients(double[][] outgoingConnectionsWeightsGradients);

    double[][] getOutgoingConnectionsWeightsGradients();

    void addOutgoingConnectionsWeightsGradients(double[][] outgoingConnectionsWeightsGradients);

    void backPropagate();

    void updateWeightsAndBiases(double learningRate);

    void resetGradients();
}
