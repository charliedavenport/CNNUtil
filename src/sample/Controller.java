package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.util.ArrayList;
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
    private ListView modelListView;

    private String currentModelName;
    private String currentDatasetName;
    private String statsDatasetName;

    public Controller() {

    }

    @FXML
    private void initialize() {
        List<String> modelNames = TestQuery.SelectCnnNames();
        modelComboBox.getItems().setAll(modelNames.toArray(new String[0]));

        List<String> datasetNames = TestQuery.SelectDatasetNames();
        datasetComboBox.getItems().setAll(datasetNames.toArray(new String[0]));
        statsDatasetComboBox.getItems().setAll(datasetNames.toArray(new String[0]));

        datasetComboBox.valueProperty()
                .addListener((ov, s, t1) -> currentDatasetName = t1);
        modelComboBox.valueProperty()
                .addListener((observableValue, s, t1) -> currentModelName = t1);
        statsDatasetComboBox.valueProperty()
                .addListener((ov, s, tl) -> statsDatasetName = tl);
    }

    @FXML
    private void handleDatasetCombo(ActionEvent event) {

    }

    @FXML
    private void handleStatDatasetCombo(ActionEvent event) {
        System.out.println(statsDatasetName);
        List<String> modelNames = TestQuery.SelectCnnNames(statsDatasetName);
        for(int i = 0; i < modelNames.size(); i++)
            System.out.println(modelNames.get(i));

        modelListView.getItems().setAll(modelNames.toArray(new String[0]));
        modelListView.setVisible(true);
    }


}
