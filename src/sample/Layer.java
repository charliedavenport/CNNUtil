package sample;

import javafx.beans.property.*;

/**
 * Represents an entry in the cnn_util.layer table.
 * Use to populate the tableView in the 'model' tab
 *
 * @author Charles Davenport
 */
public class Layer {

    private SimpleIntegerProperty cnn_id;
    private SimpleIntegerProperty depth;
    private SimpleStringProperty type;
    private SimpleIntegerProperty params;
    private SimpleIntegerProperty filters;
    private SimpleStringProperty kernel;
    private SimpleStringProperty stride;
    private SimpleStringProperty inputShape;
    private SimpleStringProperty outputShape;

    /**
     * Default constructor
     */
    public Layer() {
        this(0,0,"",
                0,0,"",
                "","","");
    }

    /**
     * Parametrized constructor
     * @param _cnn_id id of cnn corresponding to layer
     * @param _depth depth of layer in corresponding cnn
     * @param _type type of layer
     * @param _params number of layer parameters
     * @param _filters number of filters in layer
     * @param _kernel size of filter in layer
     * @param _stride stride between filters in layer
     * @param _inputShape shape of input to layer
     * @param _outputShape shape of output from layer
     */
    public Layer(int _cnn_id, int _depth, String _type, int _params,
                 int _filters, String _kernel, String _stride,
                 String _inputShape, String _outputShape) {
        cnn_id = new SimpleIntegerProperty(_cnn_id);
        depth = new SimpleIntegerProperty(_depth);
        type = new SimpleStringProperty(_type);
        params = new SimpleIntegerProperty(_params);
        filters = new SimpleIntegerProperty(_filters);
        kernel = new SimpleStringProperty(_kernel);
        stride = new SimpleStringProperty(_stride);
        inputShape = new SimpleStringProperty(_inputShape);
        outputShape = new SimpleStringProperty(_outputShape);
    }

    /**
     * Returns id of cnn corresponding to layer
     * @return id of cnn corresponding to layer
     */
    public int getCnn_id() {
        return cnn_id.get();
    }

    /**
     * Returns depth of layer in corresponding cnn
     * @return depth of layer in corresponding cnn
     */
    public int getDepth() {
        return depth.get();
    }

    /**
     * Returns type of layer
     * @return type of layer
     */
    public String getType() {
        return type.get();
    }

    /**
     * Returns number of parameters in layer
     * @return number of parameters in layer
     */
    public int getParams() {
        return params.get();
    }

    /**
     * Returns number of filters in layer
     * @return number of filters in layer
     */
    public int getFilters() {
        return filters.get();
    }

    /**
     * Returns kernel size of layer
     * @return kernel size of layer
     */
    public String getKernel() {
        return kernel.get();
    }

    /**
     * Returns stride of layer
     * @return stride of layer
     */
    public String getStride() {
        return stride.get();
    }

    /**
     * Returns shape of input to layer
     * @return shape of input to layer
     */
    public String getInputShape() {
        return inputShape.get();
    }

    /**
     * Returns shape of output to layer
     * @return shape of output to layer
     */
    public String getOutputShape() {
        return outputShape.get();
    }
}
