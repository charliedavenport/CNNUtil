package sample;

/**
 * An evaluated model
 *
 * @author Quinn Wyner
 */
public class EvaluatedModel {
    private String modelName;
    private double acc;
    private double loss;

    /**
     * Constructor for an EvaluatedModel
     * @param _modelName name of the model
     * @param _acc validation accuracy of the model
     * @param _loss validation loss of the model
     */
    public EvaluatedModel(String _modelName, double _acc, double _loss) {
        modelName = _modelName;
        acc = _acc;
        loss = _loss;
    }

    /**
     * Returns model name
     * @return model name
     */
    public String getModelName() {return modelName;}

    /**
     * Returns validation accuracy
     * @return validation accuracy
     */
    public double getAcc() {return acc;}

    /**
     * Returns validation loss
     * @return validation loss
     */
    public double getLoss() {return loss;}
}
