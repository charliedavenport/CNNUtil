package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class containing static methods for querying the DataBase
 * @author Charles Davenport
 */
public class TestQuery {

    private static Connection conn;
    private static String url = "jdbc:mysql://localhost:3306/cnn_util";

    private static final String cnnSelectQuery = "SELECT * FROM cnn";

    /**
     * @author Charles Davenport
     * @return - List of names of all cnn's in DB
     */
    public static List<String> SelectCnnNames() {
        final String query = "SELECT name FROM cnn";
        List<String> names = new ArrayList<>();
        try {
            connectToDB();

            CallableStatement stmt = conn.prepareCall(query);
            stmt.execute();
            ResultSet rs = stmt.getResultSet();
            rs.first();
            System.out.println(rs.getString(1));
            do {
                names.add(rs.getString(1));
            } while (rs.next());
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return names;
    }

    /**
     * @author Charles Davenport
     * @return - List of names of all datasets in DB
     */
    public static List<String> SelectDatasetNames() {
        final String query = "SELECT name FROM dataset";
        List<String> names = new ArrayList<>();
        try {
            connectToDB();

            CallableStatement stmt = conn.prepareCall(query);
            stmt.execute();
            ResultSet rs = stmt.getResultSet();
            rs.first();
            //System.out.println(rs.getString(1));
            do {
                names.add(rs.getString(1));
            } while (rs.next());
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return names;
    }


    public static ResultSet selectCNN() {
        ResultSet rs = null;
        //Connection conn = null;
        String query = "SELECT * FROM cnn JOIN layer;";
        try {
            connectToDB();
            CallableStatement cStmt = conn.prepareCall(query);
            boolean success = cStmt.execute();
            //System.out.println(success);
            rs = cStmt.getResultSet();

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

        return rs;
    }

    public static ResultSet selectCNNByName(String name) {
        ResultSet rs = null;
        //Connection conn = null;
        String query = "SELECT layers,trainable_params,depth, type,params FROM cnn JOIN layer WHERE name='" + name + "';";
        System.out.println(query);
        try {
            connectToDB();
            CallableStatement cStmt = conn.prepareCall(query);
            boolean success = cStmt.execute();
            //System.out.println(success);
            rs = cStmt.getResultSet();

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

        return rs;
    }


    /**
     * @param dataset   the dataset on which cnn's should have been trained
     * @author Quinn Wyner
     * @return - List of names of all cnn's in DB that have trained on dataset
     */
    public static List<String> SelectCnnNames(String dataset) {
        final String query = "SELECT DISTINCT C.name\n" +
                "FROM cnn C,\n" +
                "\t(SELECT T.cnn_id\n" +
                "     FROM train T, \n" +
                "\t\t(SELECT D.id\n" +
                "         FROM dataset D\n" +
                "         WHERE D.name = ?) E\n" +
                "\t WHERE T.data_id = E.id) U\n" +
                "WHERE C.id = U.cnn_id;";
        List<String> names = new ArrayList<>();
        try {
            connectToDB();

            CallableStatement stmt = conn.prepareCall(query);
            stmt.setString(1, dataset);
            stmt.execute();
            ResultSet rs = stmt.getResultSet();
            rs.first();
            do {
                names.add(rs.getString(1));
            } while (rs.next());
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return names;
    }

    /**
     * @param dataset   the dataset on which cnn's should have been trained
     * @param cnn       the cnn whose training data should be loaded
     * @author Quinn Wyner
     * @return - lists of epochs, losses, and accuracies for training cnn on dataset
     */
    public static List<List<Number>> SelectLossAccuracy(String dataset, String cnn) {
        final String query = "SELECT T.epoch, T.loss, T.acc\n" +
                "FROM train T,\n" +
                "\t(SELECT D.id\n" +
                "     FROM dataset D\n" +
                "     WHERE D.name = ?) E,\n" +
                "\t(SELECT C.id\n" +
                "     FROM cnn C\n" +
                "     WHERE C.name = ?) N\n" +
                " WHERE T.cnn_id = N.id\n" +
                "   AND T.data_id = E.id\n" +
                "ORDER BY T.epoch;";
        List<List<Number>> lists = new ArrayList<>();
        for(int i = 0; i < 3; i++)
            lists.add(new ArrayList<>());
        try {
            connectToDB();

            CallableStatement stmt = conn.prepareCall(query);
            stmt.setString(1, dataset);
            stmt.setString(2, cnn);
            stmt.execute();
            ResultSet rs = stmt.getResultSet();
            rs.first();
            do {
                lists.get(0).add(rs.getInt("epoch"));
                lists.get(1).add(rs.getDouble("loss"));
                lists.get(2).add(rs.getDouble("acc"));
            } while (rs.next());
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lists;
    }

    /**
     * Connect to mysql database.
     * @author Charles Davenport
     */
    private static void connectToDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Properties prop = new Properties();
            prop.put("user", "root");
            prop.put("password", "16FishFillet");
            prop.put("serverTimezone", "UTC");
            prop.put("allowMultiQueries", true);
            conn = DriverManager.getConnection(url, prop);
            //System.out.println("Connected to DB: " + conn.getCatalog());
        }
        catch (ClassNotFoundException ex) {
            System.out.println("DBInsert.connectToDB(): ClassNotFoundException: "
                    + ex.getMessage());
        }
        catch (SQLException ex) {
            System.out.println("DBInsert.connectToDB(): SQLException: "
                    + ex.getMessage());
        }

    }

    /**
     * @author Charles Davenport
     * @param dataset - name of dataset
     * @return List of Strings that describe this dataset
     */
    public static List<String> selectDatasetInfo(String dataset) {
        List<String> rows = new ArrayList<>();



        return rows;
    }
}
