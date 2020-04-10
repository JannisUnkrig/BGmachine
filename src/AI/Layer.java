package AI;

public interface Layer {

    double[] getBiases();

    void setPreviousLayer(Layer previousLayer);

    double[] calcOutputs(double[] inputs);
}
