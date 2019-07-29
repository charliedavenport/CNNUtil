package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class Controller {

    @FXML
    private ComboBox<String> modelComboBox;

    @FXML
    private ListView modelDataListView;

    @FXML
    private ComboBox<String> datasetComboBox;

    @FXML
    private ListView datasetListView;

    @FXML
    private ComboBox<String> statsDatasetComboBox;

    @FXML
    private ComboBox<String> statsModelComboBox;

    @FXML
    private Label modelLabel;

    @FXML
    private TableView<Layer> layerTable;

    @FXML
    private Label layerDataLabel;

    private String currentModelName;
    private String currentDatasetName;
    private String statsDatasetName;

    public Controller() {

    }

    @FXML
    private void initialize() {
        List<String> modelNames = DBAccess.SelectCnnNames();
        modelComboBox.getItems().setAll(modelNames.toArray(new String[0]));

        //layerTable.getItems().add(new Layer(0,0,"type",0,0,"kernel","stride","input","output"));

        List<String> datasetNames = DBAccess.SelectDatasetNames();
        datasetComboBox.getItems().setAll(datasetNames.toArray(new String[0]));
        statsDatasetComboBox.getItems().setAll(datasetNames.toArray(new String[0]));

        datasetComboBox.valueProperty()
                .addListener((ov, s, t1) -> {
                    currentDatasetName = t1;
                    datasetListView.getItems().clear();
                    List<String> datasetInfo = DBAccess.datasetInfoByName(currentDatasetName);
                    datasetListView.getItems().addAll(datasetInfo);
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
        statsDatasetComboBox.valueProperty()
                .addListener((ov, s, tl) -> statsDatasetName = tl);
    }



    @FXML
    private void handleStatDatasetCombo(ActionEvent event) {
        List<String> modelNames = DBAccess.SelectCnnNames(statsDatasetName);
        statsModelComboBox.getItems().setAll(modelNames.toArray(new String[0]));
        statsModelComboBox.setVisible(true);
    }


}
