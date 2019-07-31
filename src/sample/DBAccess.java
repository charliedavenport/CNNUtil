package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Class containing static methods for querying the DataBase
 * @author Stone Daniel, Charles Davenport, Quinn Wyner
 */
public class DBAccess {

    private static Connection conn;
    private static String url = "jdbc:mysql://localhost:3306/cnn_util";

    /**
     * Returns list of names of all cnn's in DB
     * @return - List of names of all cnn's in DB
     *
     * @author Charles Davenport
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
     * Returns list of names of all datasets in DB
     * @return - List of names of all datasets in DB
     *
     * @author Charles Davenport
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
     * @param name - name of dataset
     * @return List of Strings describing the dataset
     *
     * @author Charles Davenport
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
     * Gets list of classes of a given dataset
     * @param name name of the dataset
     * @return list of classes of a given dataset
     *
     * @author Stone Daniel
     */
    public static List<String> datasetClassesByName(String name){
        ResultSet rs;
        String query1 = "SELECT DISTINCT D.class\n" +
                "FROM data D,\n" +
                "\t (SELECT S.id\n" +
                "      FROM dataset S\n" +
                "\t  WHERE S.name = ?) T\n" +
                "WHERE D.dataset = T.id\n" +
                "ORDER BY D.class";
        List<String> names = new ArrayList<>();
        try {
            connectToDB();
            PreparedStatement pStmt = conn.prepareStatement(query1);
            pStmt.setString(1, name);
            pStmt.execute();
            rs = pStmt.getResultSet();
            rs.first();

            do {
                names.add(rs.getString(1));
            } while (rs.next());

            return names;
        } catch (SQLException ex) {
            return names;
        }
    }

    /**
     * Determines validity of a user login
     * @param username username given in login attempt
     * @param hashedPWD password that has been hashed during login attempt
     * @return true if login valid, false otherwise
     *
     * @author Stone Daniel
     */
    public static boolean isValidUser(String username, String hashedPWD){
        ResultSet rs;
        String query1 = "SELECT COUNT(username) FROM users WHERE username=? and password=?;";
        try {
            connectToDB();
            PreparedStatement pStmt = conn.prepareStatement(query1);
            pStmt.setString(1, username);
            pStmt.setString(2, hashedPWD);
            pStmt.execute();
            rs = pStmt.getResultSet();
            rs.first();
            int numAccounts = rs.getInt(1);
            if(numAccounts == 0)
                return false;
            else if(numAccounts >= 1)
                return true;

        }catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        return false;
    }

    /**
     * Gets images from a given class and dataset
     * @param datasetName name of the dataset
     * @param curClass class label
     * @return 12 random images from the given class
     *
     * @author Stone Daniel
     */
    public static List<Blob> datasetIMGsByData(String datasetName, int curClass) {
        List<Blob> blobs = new ArrayList<>();
        ResultSet rs;
        String query1 = "SELECT D.image\n" +
                "FROM (SELECT E.image, E.dataset\n" +
                "\t  FROM data E\n" +
                "      WHERE E.class = ?) D,\n" +
                "\t (SELECT S.id\n" +
                "      FROM dataset S\n" +
                "      WHERE S.name = ?) T\n" +
                "WHERE D.dataset = T.id;";
        try {
            connectToDB();
            PreparedStatement pStmt = conn.prepareStatement(query1);
            //pStmt.setString(1, datasetName);
            pStmt.setInt(1, curClass);
            pStmt.setString(2, datasetName);
            pStmt.execute();
            rs = pStmt.getResultSet();
            rs.first();
            int rsLen = 0;
            while(rs.next()) {
                rsLen++;
            }
            if(rsLen <= 0)
                return null;
            rs.first();
            Random r = new Random();
            List<Blob> allBlobs = new ArrayList<>();
            while(rs.next()){
                allBlobs.add(rs.getBlob(1));
            }
            for(int i = 0; i<12;i++){
                blobs.add(allBlobs.get(r.nextInt(rsLen)));
            }

            return blobs;

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
        return null;
    }
    /**
     * Get a List of Strings to put in Model ListView
     * @param name - model name to match in DB
     * @return List of Strings to be inserted in fxml ListView
     *
     * @author Stone Daniel, Charles Davenport
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

    /**
     * Gets information on the layers of a given cnn
     * @param cnnName name of the given cnn
     * @return list of Layers of a given cnn
     *
     * @author Charles Davenport
     */
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
                Layer tmp;
                if (type.equals("DENSE")) {
                    tmp = new Layer(cnnId,
                            rs.getInt("depth"),
                            rs.getString("type"),
                            rs.getInt("params"),
                            rs.getInt("filters"),
                            null,
                            null,
                            String.valueOf(rs.getInt("input_x")),
                            //inputStr,
                            String.valueOf(rs.getInt("output_x")));
                }
                else if (type.equals("FLATTEN")) {
                    tmp = new Layer(cnnId,
                            rs.getInt("depth"),
                            rs.getString("type"),
                            rs.getInt("params"),
                            rs.getInt("filters"),
                            null,
                            null,
                            //String.valueOf(rs.getInt("input_x")),
                            inputStr,
                            String.valueOf(rs.getInt("output_x")));
                }
                else {
                    tmp = new Layer(cnnId,
                            rs.getInt("depth"),
                            rs.getString("type"),
                            rs.getInt("params"),
                            rs.getInt("filters"),
                            kernelStr,
                            strideStr,
                            inputStr,
                            outputStr);
                }

                layerInfo.add(tmp);
            } while (rs.next());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return layerInfo;
    }



    /**
     * Gets list of names of all cnns in DB that have trained on a given dataset
     * @param dataset   the dataset on which cnns should have been trained
     * @return - List of names of all cnns in DB that have trained on dataset
     *
     * @author Quinn Wyner
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
     * Gets a validation information on models trained on a given dataset
     * @param dataset   the dataset on which cnns should have been evaluated
     * @return - List of EvaluatedModels evaluated on dataset
     *
     * @author Quinn Wyner
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
     * Gets training information on a model trained on a given dataset
     * @param dataset   the dataset on which cnn's should have been trained
     * @param cnn       the cnn whose training data should be loaded
     * @return - lists of epochs, losses, and accuracies for training cnn on dataset
     *
     * @author Quinn Wyner
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
     *
     * @author Charles Davenport
     */
    private static void connectToDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Properties prop = new Properties();
            prop.put("user", "java");
            prop.put("password", "admin");
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
