package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.util.List;

public class Controller {

    @FXML
    private ComboBox<String> modelComboBox;

    @FXML
    private ListView modelDataListView;

    @FXML
    private ComboBox<String> datasetComboBox;

    @FXML
    private ComboBox<String> statsDatasetComboBox;

    @FXML
    private ComboBox<String> statsModelComboBox;

    private String currentModelName;
    private String currentDatasetName;
    private String statsDatasetName;

    public Controller() {

    }

    @FXML
    private void initialize() {
        List<String> modelNames = DBAccess.SelectCnnNames();
        modelComboBox.getItems().setAll(modelNames.toArray(new String[0]));

        List<String> datasetNames = DBAccess.SelectDatasetNames();
        datasetComboBox.getItems().setAll(datasetNames.toArray(new String[0]));
        statsDatasetComboBox.getItems().setAll(datasetNames.toArray(new String[0]));

        datasetComboBox.valueProperty()
                .addListener((ov, s, t1) -> currentDatasetName = t1);
        modelComboBox.valueProperty()
                .addListener((observableValue, s, t1) -> {
                    currentModelName = t1;
                    modelDataListView.getItems().clear();
                    List<String> modelData = DBAccess.CNNInfoByName(currentModelName);
                    modelDataListView.getItems().addAll(modelData);
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
