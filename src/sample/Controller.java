package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    private ComboBox<String> modelComboBox;

    @FXML
    private ListView modelDataListView;

    @FXML
    private ComboBox<String> datasetComboBox;

    private String currentModelName;
    private String currentDatasetName;

    public Controller() {

    }

    @FXML
    private void initialize() {
        List<String> modelNames = TestQuery.SelectCnnNames();
        modelComboBox.getItems().setAll(modelNames.toArray(new String[0]));

        List<String> datasetNames = TestQuery.SelectDatasetNames();
        datasetComboBox.getItems().setAll(datasetNames.toArray(new String[0]));

        datasetComboBox.valueProperty()
                .addListener((ov, s, t1) -> currentDatasetName = t1);
        modelComboBox.valueProperty()
                .addListener((observableValue, s, t1) -> currentModelName = t1);
        modelComboBox.valueProperty().addListener((observableValue, s, t1) -> updateModelData());
    }

    @FXML
    private void updateModelData(){
        try {
            modelDataListView.getItems().clear();
            ResultSet rs = TestQuery.selectCNNByName(currentModelName);
            ResultSetMetaData meta = rs.getMetaData();
            int numCols = meta.getColumnCount();
            rs.first();
            for (int i = 1; i <= numCols; i++) {
                modelDataListView.getItems().add(rs.getString(i) + " " + meta.getColumnName(i));
            }

        }catch (SQLException ex) {

        }
    }

    @FXML
    private void handleDatasetCombo(ActionEvent event) {

    }




}
