package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class containing static methods for querying the DataBase
 * @author Charles Davenport
 */
public class DBAccess {

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

    /**
     * Get a List of Strings to put in Dataset ListView
     *
     * @author Charles Davenport
     * @param name - name of dataset
     * @return List of Strings describing the dataset
     */
    public static List<String> datasetInfoByName(String name) {
        List<String> datasetInfo = new ArrayList<>();
        String query1 = "SELECT * FROM dataset WHERE name=?;";

        try {
            connectToDB();
            PreparedStatement pStmt = conn.prepareStatement(query1);
            pStmt.setString(1, name);
            pStmt.execute();
            ResultSet rs = pStmt.getResultSet();
            if (!rs.next()) {
                return datasetInfo;
            }
            rs.first();
            datasetInfo.add("Samples: " + rs.getInt("samples"));
            datasetInfo.add("# of Classes: " + rs.getInt("classes"));
            datasetInfo.add("Image Size: (" + rs.getInt("img_x") + ","
                + rs.getInt("img_y") + "," + rs.getInt("img_z") + ")");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return datasetInfo;
    }


    /**
     * Get a List of Strings to put in Model ListView
     *
     * @author Stone Daniel
     * @author Charles Davenport
     * @param name - model name to match in DB
     * @return List of Strings to be inserted in fxml ListView
     */
    public static List<String> CNNInfoByName(String name) {
        List<String> cnnInfo = new ArrayList<>();
        ResultSet rs;
        //Connection conn = null;
        String query1 = "SELECT id, layers,trainable_params FROM cnn WHERE name=?;";
        //System.out.println(query1);
        try {
            connectToDB();
            // FIRST QUERY - GET CNN INFO
            PreparedStatement pStmt = conn.prepareStatement(query1);
            pStmt.setString(1, name);
            pStmt.execute();
            rs = pStmt.getResultSet();
            rs.first();
            // get cnn_id for next query
            int cnnId = rs.getInt("id");
            //assume one entry
            cnnInfo.add("Layers:\t" + rs.getInt("layers"));
            cnnInfo.add("Trainable Params:\t" + rs.getInt("trainable_params"));




        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

        return cnnInfo;
    }//CNNInfoByName()

    public static List<Layer> layerInfoByCNN(String cnnName) {
        List<Layer> layerInfo = new ArrayList<>();
        ResultSet rs;
        String query1 = "SELECT id FROM cnn WHERE name=?;";
        String query2 = "SELECT * FROM layer where cnn_id=?;";
        try {
            connectToDB();
            PreparedStatement pStmt = conn.prepareStatement(query1);
            pStmt.setString(1, cnnName);
            pStmt.execute();
            rs = pStmt.getResultSet();
            // check if empty - no match in db
            if (!rs.next()) {
                return layerInfo;
            }
            rs.first();
            int cnnId = rs.getInt("id");
            pStmt = conn.prepareStatement(query2);
            pStmt.setInt(1, cnnId);
            pStmt.execute();
            rs = pStmt.getResultSet();
            if (!rs.next()) {
                return layerInfo;
            }
            rs.first();
            do {
                String type = rs.getString("type");
                String kernelStr = "(" + rs.getInt("kernel_x") + ","
                        + rs.getInt("kernel_y") + ")";
                String strideStr = "(" + rs.getInt("stride_x") + ","
                        + rs.getInt("stride_y") + ")";
                String inputStr = "(" + rs.getInt("input_x") + ","
                        + rs.getInt("input_y") + ","
                        + rs.getInt("input_z") + ")";
                String outputStr = "(" + rs.getInt("output_x") + ","
                        + rs.getInt("output_y") + ","
                        + rs.getInt("output_z") + ")";
                Layer tmp = (type.equals("DENSE"))
                        ? new Layer(cnnId,
                            rs.getInt("depth"),
                            rs.getString("type"),
                            rs.getInt("params"),
                            rs.getInt("filters"),
                            null,
                            null,
                            //String.valueOf(rs.getInt("input_x")),
                            inputStr,
                            String.valueOf(rs.getInt("output_x")))
                        : new Layer(cnnId,
                            rs.getInt("depth"),
                            rs.getString("type"),
                            rs.getInt("params"),
                            rs.getInt("filters"),
                            kernelStr,
                            strideStr,
                            inputStr,
                            outputStr);
                layerInfo.add(tmp);
            } while (rs.next());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return layerInfo;
    }



    /**
     * @param dataset   the dataset on which cnns should have been trained
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
            // check for empty ResultSet
            if (!rs.next()){
                //System.out.println("empty rs");
                return names;
            }
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

    /**
     * @param dataset   the dataset on which cnns should have been evaluated
     * @author Quinn Wyner
     * @return - List of EvaluatedModels evaluated on dataset
     */
    public static List<EvaluatedModel> SelectEval(String dataset) {
        final String query = "SELECT C.name, T.acc, T.loss\n" +
                "FROM cnn C,\n" +
                "\t (SELECT S.cnn_id, S.acc, S.loss\n" +
                "      FROM test S,\n" +
                "\t\t   (SELECT D.id\n" +
                "            FROM dataset D\n" +
                "            WHERE D.name = ?) E\n" +
                "\t  WHERE S.data_id = E.id) T\n" +
                "WHERE C.id = T.cnn_id\n" +
                "ORDER BY T.acc DESC;";
        List<EvaluatedModel> models = new ArrayList<>();
        try {
            connectToDB();

            CallableStatement stmt = conn.prepareCall(query);
            stmt.setString(1, dataset);
            stmt.execute();
            ResultSet rs = stmt.getResultSet();
            // check for empty ResultSet
            if (!rs.next()){
                //System.out.println("empty rs");
                return models;
            }
            rs.first();
            //System.out.println(rs.getString(1));
            do {
                models.add(new EvaluatedModel(rs.getString(1), rs.getDouble(2), rs.getDouble(3)));
            } while (rs.next());
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return models;
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
            if(!rs.next())
                return lists;
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

}
