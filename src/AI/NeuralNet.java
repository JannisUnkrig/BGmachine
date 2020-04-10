package AI;

public class NeuralNet {

    private InputLayer inputLayer;
    private HiddenLayer[] hiddenLayers;
    private OutputLayer outputLayer;


    public NeuralNet(int inputNodes, int[] hiddenNodes, int outputNodes) {
        outputLayer = new OutputLayer(outputNodes);

        hiddenLayers = new HiddenLayer[hiddenNodes.length];
        for (int i = hiddenNodes.length - 1; i >= 0; i--) {
            if (i == hiddenNodes.length - 1) {
                hiddenLayers[i] = new HiddenLayer(hiddenNodes[i], outputLayer);
            } else {
                hiddenLayers[i] = new HiddenLayer(hiddenNodes[i], hiddenLayers[i + 1]);
            }
        }

        inputLayer = new InputLayer(inputNodes, hiddenLayers[0]);
    }

    /**deep copys a NeuralNet*/
    public NeuralNet(NeuralNet toDeepCopy) {
        this.outputLayer = new OutputLayer(toDeepCopy.outputLayer);

        HiddenLayer[] newHiddenLayers = new HiddenLayer[toDeepCopy.hiddenLayers.length];
        for (int i = newHiddenLayers.length - 1; i >= 0; i--) {
            newHiddenLayers[i] = new HiddenLayer(toDeepCopy.hiddenLayers[i]);
            if (i == newHiddenLayers.length - 1) {
                newHiddenLayers[i].nextLayer = this.outputLayer;
                this.outputLayer.previousLayer = newHiddenLayers[i];
            } else {
                newHiddenLayers[i].nextLayer = newHiddenLayers[i + 1];
                newHiddenLayers[i + 1].previousLayer = newHiddenLayers[i];
            }
        }
        this.hiddenLayers = newHiddenLayers;

        this.inputLayer = new InputLayer(toDeepCopy.inputLayer);
        this.inputLayer.nextLayer = this.hiddenLayers[0];
        this.hiddenLayers[0].previousLayer = this.inputLayer;
    }

    public double[] calcOutputs(int[] inputs) {
        return inputLayer.calcOutputs(inputs);
    }

}
