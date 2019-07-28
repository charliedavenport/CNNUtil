package sample;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.util.List;

public class Controller {

    @FXML
    private ComboBox<String> modelComboBox;

    @FXML
    private ListView modelDataListView;

    public Controller() {

    }

    @FXML
    private void initialize() {
        List<String> modelNames = TestQuery.SelectCnnNames();
        modelComboBox.getItems().setAll(modelNames.toArray(new String[0]));
    }

    @FXML
    private void printOutput() {

    }



}
