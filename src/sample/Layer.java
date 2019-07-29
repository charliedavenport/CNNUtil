package sample;

import javafx.beans.property.*;

/**
 * Represents an entry in the cnn_util.layer table.
 * Use to populate the tableView in the 'model' tab
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

    public Layer() {
        this(0,0,"",
                0,0,"",
                "","","");
    }

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

    public int getCnn_id() {
        return cnn_id.get();
    }

    public int getDepth() {
        return depth.get();
    }

    public String getType() {
        return type.get();
    }

    public int getParams() {
        return params.get();
    }

    public int getFilters() {
        return filters.get();
    }

    public String getKernel() {
        return kernel.get();
    }

    public String getStride() {
        return stride.get();
    }

    public String getInputShape() {
        return inputShape.get();
    }

    public String getOutputShape() {
        return outputShape.get();
    }
}
