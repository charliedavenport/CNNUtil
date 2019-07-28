package sample;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

public class Controller {

    @FXML
    private ComboBox<String> modelComboBox;

    @FXML
    private ListView modelDataListView;

    public Controller() {

    }

    @FXML
    private void initialize() {
        modelComboBox.getItems().setAll("MODEL 1", "MODEL 2");
    }

    @FXML
    private void printOutput() {

    }



}
