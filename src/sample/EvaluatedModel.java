package sample;

public class EvaluatedModel {
    private String modelName;
    private double acc;
    private double loss;

    public EvaluatedModel(String _modelName, double _acc, double _loss) {
        modelName = _modelName;
        acc = _acc;
        loss = _loss;
    }

    public String getModelName() {return modelName;}
    public double getAcc() {return acc;}
    public double getLoss() {return loss;}
}
