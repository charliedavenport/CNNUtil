package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

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
        statsModelComboBox.valueProperty()
                .addListener((ov, s, tl) -> statsModelName = tl);
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
