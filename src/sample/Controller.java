package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * Handles fxml injection
 *
 * @author Stone Daniel, Charles Davenport, Quinn Wyner
 */
public class Controller {

    /**
     * A trained model
     *
     * @author Quinn Wyner
     */
    private class refHolder {
        public String modelName;
        public Series loss;
        public Series acc;

        /**
         * Constructor for a refHolder
         * @param _modelName name of the trained model
         * @param _loss loss series of the trained model
         * @param _acc accuracy series of the trained model
         */
        public refHolder(String _modelName, Series _loss, Series _acc) {
            modelName = _modelName;
            loss = _loss;
            acc = _acc;
        }
    }

    @FXML
    private ComboBox<String> modelComboBox;

    @FXML
    private ListView modelDataListView;

    @FXML
    private ComboBox<String> datasetComboBox;

    @FXML
    private ComboBox<String> datasetClassComboBox;

    @FXML
    private GridPane datasetGridPane;

    @FXML
    private ComboBox<String> evalDatasetComboBox;

    @FXML
    private TableView evalTable;

    @FXML
    private TableColumn modelColumn;

    @FXML
    private TableColumn accColumn;

    @FXML
    private TableColumn lossColumn;

    @FXML
    private ListView datasetListView;

    @FXML
    private ComboBox<String> statsDatasetComboBox;

    @FXML
    private ComboBox<String> statsModelComboBox;

    @FXML
    private LineChart<Number, Number> lossChart;

    @FXML
    private LineChart<Number, Number> accChart;

    @FXML
    private Label modelLabel;

    @FXML
    private TableView<Layer> layerTable;

    @FXML
    private Label layerDataLabel;

    @FXML
    private Button newSampleButton;

    @FXML
    private Label labelClassName;

    private String currentModelName;
    private String currentDatasetName;
    private String evalDatasetName;
    private String statsDatasetName;
    private String statsModelName;

    private ArrayList<refHolder> lineChartList;

    /**
     * Default constructor
     *
     * @author Charles Davenport
     */
    public Controller() {

    }

    @FXML
    /**
     * Constructs initial states of gui elements
     *
     * @author Stone Daniel, Charles Davenport, Quinn Wyner
     */
    private void initialize() {
        List<String> modelNames = DBAccess.SelectCnnNames();
        modelComboBox.getItems().setAll(modelNames.toArray(new String[0]));

        //layerTable.getItems().add(new Layer(0,0,"type",0,0,"kernel","stride","input","output"));

        List<String> datasetNames = DBAccess.SelectDatasetNames();
        datasetComboBox.getItems().setAll(datasetNames.toArray(new String[0]));
        //System.out.println(datasetNames);
        evalDatasetComboBox.getItems().setAll(datasetNames.toArray(new String[0]));
        statsDatasetComboBox.getItems().setAll(datasetNames.toArray(new String[0]));

        datasetComboBox.valueProperty()
                .addListener((ov, s, t1) -> {
                    currentDatasetName = t1;
                    datasetListView.getItems().clear();
                    List<String> datasetInfo = DBAccess.datasetInfoByName(currentDatasetName);
                    datasetListView.getItems().addAll(datasetInfo);
                    List<String> classNames = DBAccess.datasetClassesByName(currentDatasetName);
                    datasetClassComboBox.getItems().clear();
                    for(String name : classNames){
                        datasetClassComboBox.getItems().add(name);
                    }
                });
        datasetClassComboBox.valueProperty()
                .addListener((ov, s, t1) -> {
                    List<Blob> blobs = null;
                    datasetGridPane.getChildren().clear();
                    labelClassName.setText(DBAccess.getClassName(currentDatasetName,Integer.parseInt(t1)));
                    try {
                        blobs = DBAccess.datasetIMGsByData(currentDatasetName, Integer.parseInt(t1));
                    } catch (NumberFormatException nfe){}
                    if(blobs == null)
                        return;
                    try {
                        for(int j = 0; j < 5; j++) {
                            for (int k = 0; k < 4; k++) {
                                InputStream in = blobs.get(k+(j*3)).getBinaryStream();
                                Image img = new Image(in);
                                datasetGridPane.add(new ImageView(img), k, j);
                            }
                        }
                    } catch (SQLException ex){

                    }
                });
        modelComboBox.valueProperty()
                .addListener((observableValue, s, t1) -> {
                    currentModelName = t1;
                    modelLabel.setText(currentModelName + " Model Data");
                    layerDataLabel.setText(currentModelName + " Layer Data");
                    // update modelData ListView
                    modelDataListView.getItems().clear();
                    List<String> modelData = DBAccess.CNNInfoByName(currentModelName);
                    modelDataListView.getItems().addAll(modelData);
                    // update layerData TableView
                    layerTable.getItems().clear();
                    List<Layer> layerInfo = DBAccess.layerInfoByCNN(currentModelName);
                    layerTable.getItems().addAll(layerInfo);
                });
        evalDatasetComboBox.valueProperty()
                .addListener((ov, s, tl) -> evalDatasetName = tl);
        statsDatasetComboBox.valueProperty()
                .addListener((ov, s, tl) -> statsDatasetName = tl);
        statsModelComboBox.valueProperty()
                .addListener((ov, s, tl) -> statsModelName = tl);

        modelColumn.setCellValueFactory(new PropertyValueFactory<>("modelName"));
        accColumn.setCellValueFactory(new PropertyValueFactory<>("acc"));
        lossColumn.setCellValueFactory(new PropertyValueFactory<>("loss"));
    }


    /**
     * Handles interaction with the Stats tab's dataset selector
     * @param event event requiring handling
     *
     * @author Quinn Wyner
     */
    @FXML
    private void handleStatDatasetCombo(ActionEvent event) {
        lineChartList = new ArrayList<>();
        List<String> modelNames = DBAccess.SelectCnnNames(statsDatasetName);
        statsModelComboBox.getItems().setAll(modelNames.toArray(new String[0]));
        statsModelComboBox.setVisible(true);
        lossChart.setVisible(false);
        lossChart.getData().clear();
        accChart.setVisible(false);
        accChart.getData().clear();
    }

    /**
     * Handles interaction with the Stats tab's model selector
     * @param event event requiring handling
     *
     * @author Quinn Wyner
     */
    @FXML
    private void handleStatModelCombo(ActionEvent event) {
        refHolder statsRef = null;
        for(refHolder reference : lineChartList)
            if(reference.modelName.equals(statsModelName)) {
                statsRef = reference;
                break;
            }
        if(statsRef != null) {
            lossChart.getData().remove(statsRef.loss);
            accChart.getData().remove(statsRef.acc);
            lineChartList.remove(statsRef);
            return;
        }
        Series lossSeries = new Series();
        Series accSeries = new Series();

        List<List<Number>> lists = DBAccess.SelectLossAccuracy(statsDatasetName, statsModelName);
        for(int i = 0; i < lists.get(0).size(); i++) {
            lossSeries.getData().add(new Data(lists.get(0).get(i), lists.get(1).get(i)));
            accSeries.getData().add(new Data(lists.get(0).get(i), lists.get(2).get(i)));
        }

        lossSeries.setName(statsModelName);
        accSeries.setName(statsModelName);

        lossChart.getData().add(lossSeries);
        accChart.getData().add(accSeries);
        lossChart.setVisible(true);
        accChart.setVisible(true);

        statsRef = new refHolder(statsModelName, lossSeries, accSeries);
        lineChartList.add(statsRef);
    }

    /**
     * Handles interaction with Eval tab's dataset selector
     * @param event event requiring handling
     *
     * @author Quinn Wyner
     */
    @FXML
    private void handleEvalDatasetCombo(ActionEvent event) {
        evalTable.getItems().clear();

        List<EvaluatedModel> models = DBAccess.SelectEval(evalDatasetName);
        for(EvaluatedModel model : models)
            evalTable.getItems().add(model);
        evalTable.setVisible(true);
    }

    @FXML
    private void handleNewSampleBtn(ActionEvent event) {
        List<Blob> blobs = null;
        datasetGridPane.getChildren().clear();
        try {
            blobs = DBAccess.datasetIMGsByData(currentDatasetName, Integer.parseInt(datasetClassComboBox.getValue()));
        } catch (NumberFormatException nfe){}
        if(blobs == null)
            return;
        try {
            for(int j = 0; j < 5; j++) {
                for (int k = 0; k < 4; k++) {
                    InputStream in = blobs.get(k+(j*3)).getBinaryStream();
                    Image img = new Image(in);
                    datasetGridPane.add(new ImageView(img), k, j);
                }
            }
        } catch (SQLException ex){

        }


    }


}