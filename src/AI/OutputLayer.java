package AI;

public class OutputLayer implements Layer {

    protected Layer previousLayer = null;

    protected double[] biases;


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
        }
        return inputs;
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
