package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    private ComboBox<String> statsDatasetComboBox;

    @FXML
    private ComboBox<String> statsModelComboBox;

    @FXML
    private LineChart<Number, Number> lossChart;

    @FXML
    private LineChart<Number, Number> accChart;

    private String currentModelName;
    private String currentDatasetName;
    private String statsDatasetName;
    private String statsModelName;

    private ArrayList<refHolder> lineChartList = new ArrayList<>();

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
        modelComboBox.valueProperty().addListener((observableValue, s, t1) -> updateModelData());
        statsDatasetComboBox.valueProperty()
                .addListener((ov, s, tl) -> statsDatasetName = tl);
        statsModelComboBox.valueProperty()
                .addListener((ov, s, tl) -> statsModelName = tl);
    }

    @FXML
    private void updateModelData(){
        try {
            modelDataListView.getItems().clear();
            ResultSet rs = TestQuery.selectCNNByName(currentModelName);
            ResultSetMetaData meta = rs.getMetaData();
            int numCols = meta.getColumnCount();
            rs.first();
            modelDataListView.getItems().add(rs.getString(1) + " " + meta.getColumnName(1));
            modelDataListView.getItems().add(rs.getString(2) + " " + meta.getColumnName(2));
            do {
                String temp = "";
                for (int i = 3; i <= numCols; i++) {
                    temp += meta.getColumnName(i) + " " + rs.getString(i) + "\t";
                }
                modelDataListView.getItems().add(temp);
            } while (rs.next());
//            for (int i = 1; i <= numCols; i++) {
//                modelDataListView.getItems().add(rs.getString(i) + " " + meta.getColumnName(i));
//            }

        }catch (SQLException ex) {

        }
    }

    @FXML
    private void handleDatasetCombo(ActionEvent event) {

    }

    @FXML
    private void handleStatDatasetCombo(ActionEvent event) {
        List<String> modelNames = TestQuery.SelectCnnNames(statsDatasetName);
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

        List<List<Number>> lists = TestQuery.SelectLossAccuracy(statsDatasetName, statsModelName);
        for(int i = 0; i < lists.get(0).size(); i++) {
            System.out.println(lists.get(1).get(i));
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

}
