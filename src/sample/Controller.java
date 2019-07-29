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

public class Controller {

    private class refHolder {
        public String modelName;
        public Series loss;
        public Series acc;

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

    private String currentModelName;
    private String currentDatasetName;
    private int currentDatasetClassNumber;
    private String evalDatasetName;
    private String statsDatasetName;
    private String statsModelName;

    private ArrayList<refHolder> lineChartList = new ArrayList<>();

    public Controller() {

    }

    @FXML
    private void initialize() {
        List<String> modelNames = DBAccess.SelectCnnNames();
        modelComboBox.getItems().setAll(modelNames.toArray(new String[0]));

        //layerTable.getItems().add(new Layer(0,0,"type",0,0,"kernel","stride","input","output"));

        List<String> datasetNames = DBAccess.SelectDatasetNames();
        datasetComboBox.getItems().setAll(datasetNames.toArray(new String[0]));
        evalDatasetComboBox.getItems().setAll(datasetNames.toArray(new String[0]));
        statsDatasetComboBox.getItems().setAll(datasetNames.toArray(new String[0]));

        datasetComboBox.valueProperty()
                .addListener((ov, s, t1) -> {
                    currentDatasetName = t1;
                    datasetListView.getItems().clear();
                    List<String> datasetInfo = DBAccess.datasetInfoByName(currentDatasetName);
                    datasetListView.getItems().addAll(datasetInfo);
                    int numClasses = DBAccess.datasetClassesByName(currentDatasetName);
                    for(int i = 1; i<=numClasses;i++){
                        datasetClassComboBox.getItems().add("Class " + Integer.toString(i));
                    }
                });
        datasetClassComboBox.valueProperty()
                .addListener((ov, s, t1) -> {
                    currentDatasetClassNumber = (Integer.parseInt(t1.replace("Class ", "")));
                    List<Blob> blobs = DBAccess.datasetIMGsByData(currentDatasetName,currentDatasetClassNumber);
                    if(blobs == null)
                        return;
                    List<ImageView> converted = new ArrayList<>();

                    try {
                        int curBlob = 0;
                        for(int j = 0; j < 4; j++) {
                            for (int k = 0; k < 3; k++) {
                                InputStream in = blobs.get(k+(j*3)).getBinaryStream();
                                System.out.println(blobs.get(k+(j*3)));
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

    @FXML
    private void handleDatasetGridPane(){

    }

    @FXML
    private void handleStatDatasetCombo(ActionEvent event) {
        List<String> modelNames = DBAccess.SelectCnnNames(statsDatasetName);
        statsModelComboBox.getItems().setAll(modelNames.toArray(new String[0]));
        statsModelComboBox.setVisible(true);
        lossChart.setVisible(false);
        lossChart.getData().clear();
        accChart.setVisible(false);
        accChart.getData().clear();
    }

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

    @FXML
    private void handleEvalDatasetCombo(ActionEvent event) {
        evalTable.getItems().clear();

        List<EvaluatedModel> models = DBAccess.SelectEval(evalDatasetName);
        for(EvaluatedModel model : models)
            evalTable.getItems().add(model);
        evalTable.setVisible(true);
    }
}