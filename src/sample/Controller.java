package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ComboBox<String> dbtypeCbx;

    private ObservableList<String> dbTypeList = FXCollections.observableArrayList("SQLite");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbtypeCbx.setItems(dbTypeList);
    }
}
