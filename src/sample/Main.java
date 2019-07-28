package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("cnnUtil.fxml"));
        primaryStage.setTitle("CNN Util");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }



    public static void main(String[] args) {
        try {

            ResultSet rs = TestQuery.selectCNN();
            ResultSetMetaData meta = rs.getMetaData();
            int numCols = meta.getColumnCount();
            for (int i = 1; i <= numCols; i++) {
                System.out.print(meta.getColumnName(i) + "\t\t");
            }
            System.out.println();
            rs.first();
            do {
                for (int i = 1; i <= numCols; i++) {
                    System.out.print(rs.getString(i) + "\t\t");
                }
                System.out.println();
            } while (rs.next());
        } catch (SQLException ex) {

        }

        launch(args);
    }
}
