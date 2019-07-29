package sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * Class for inserting images, csv data into the mysql db
 *
 * @author Charles Davenport
 */
public class DBInsert {

    private static Connection conn;
    private static final String url = "jdbc:mysql://localhost:3306/cnn_util";

    private static final String imgDir = "D:\\Github\\ACM_ML\\Spring 2019\\MNIST_img";
    private static final String classesFilePath = "D:\\Github\\ACM_ML\\Spring 2019\\MNIST_classes.csv";
    private static final String trainCSVPath = "C:\\Users\\cdave\\IdeaProjects\\CNNUtil\\store\\train\\MNIST_CNN_01_train.csv";
    private static final String datasetName = "MNIST";
    private static final String cnnName = "MNIST_CNN_01";


    /* Check the class variables before uncommenting these functions and running.
     * These methods will make updates to the DB, so be careful
     */
    public static void main(String[] args) {

        connectToDB();
        //boolean success = insertDataFromDir(imgDir, classesFilePath, datasetName, 10);
        //System.out.println(success ? "Successfully inserted images into DB"
        //        : "Failed to insert images to DB");

        //boolean success = updateDataClasses(classesFilePath, datasetName);
        //System.out.println(success ? "Successfully updated image classes in DB"
        //        : "Failed to update image classes in DB");

        //boolean success = insertTrain(trainCSVPath, datasetName, cnnName);
        //System.out.println(success ? "Successfully inserted train data into DB"
        //                : "Failed to insert train data into DB");

    }

    /**
     * I wrote this utility function, because I initially inserted all
     * the images to the DB without setting the class properly. Now the records
     * have to be updated ¯\_(ツ)_/¯
     *
     * @author Charles Davenport
     * @param classFile - path to csv file
     * @param dataset - dataset name
     * @return true if successful, false otherwise
     */
    private static boolean updateDataClasses(String classFile, String dataset) {
        final String UPDATE_CLASSES = "UPDATE data SET class=? WHERE dataset=1 AND id=?";

        //List<Integer> classValues = new ArrayList<Integer>(70000);
        File cFile = new File(classFile);
        try {
            Scanner scn = new Scanner(cFile);
            while(scn.hasNextLine()) {
                String line = scn.nextLine();
                System.out.println(line);
                String [] items = line.split(",");
                int imgId = Integer.parseInt(items[0]);
                int classNum = Integer.parseInt(items[1]);

                PreparedStatement pstmt = conn.prepareStatement(UPDATE_CLASSES);
                pstmt.setInt(1, classNum);
                pstmt.setInt(2,imgId);
                //System.out.println(pstmt.toString());
                pstmt.executeUpdate();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Static method for inserting data (images) into DB.
     * Dataset must already exist in DB.
     *
     * @author Charles Davenport
     * @param dirPath - directory to load images from
     * @param dataset - name of dataset we're adding to
     * @param folds - how many folds to split data into
     * @return true if successful, false if not
     */
    private static boolean insertDataFromDir(String dirPath, String classFile, String dataset, int folds) {
        final String INSERT_QUERY = "INSERT INTO data VALUES (?, ?, ?, ?, ?, ?)";
        final String SELECT_DATA_ID = "SELECT id FROM dataset WHERE name=?";

        int dataset_id;
        try {
            // first, get the int id of the specified dataset from the DB
            PreparedStatement pstmt = conn.prepareStatement(SELECT_DATA_ID);
            pstmt.setString(1, dataset);
            boolean success = pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            if (rs == null) {
                System.out.println("insertDataFromDir(): could not find dataset: "
                        + dataset);
                return false;
            }
            rs.first();
            dataset_id = rs.getInt(1);
            System.out.println("Inserting into Dataset with id = "
                    + dataset_id);

            // open file containing classes
            File cFile = new File(classFile);
            Scanner scn = new Scanner(cFile);
            String line;
            // open image directory file
            File dir = new File(dirPath);
            String [] dirContents = dir.list();
            // WARNING - this loop can take a long time to complete. Debug with smaller amt of images
            for (int i=0; i<dirContents.length; i++) {
                //System.out.println(dirContents[i]);
                String img_path = dirPath + "\\" + dirContents[i];
                // open image file - convert to binary stream
                File img_file = new File(img_path);
                FileInputStream inputs = new FileInputStream(img_file);
                // get next line from class file
                int classNum;
                if (scn.hasNextLine()) {
                    line = scn.nextLine();
                    classNum = Integer.parseInt(line.split(",")[1]);
                }
                else classNum = -1;

                pstmt = conn.prepareStatement(INSERT_QUERY);
                pstmt.setInt   (1, dataset_id);
                pstmt.setInt   (2, i); // TO DO: this should autoincrement
                pstmt.setString(3, img_path);
                pstmt.setInt   (4, 0); //TO DO: folds
                pstmt.setInt   (5, classNum);
                pstmt.setBinaryStream(6, inputs);

                pstmt.executeUpdate();


            }

        }
        catch(SQLException ex) {
            System.out.println("DBInsert.insertDataFromDir(): SQLException: "
                    + ex.getMessage());
        } catch (FileNotFoundException ex) {
            System.out.println("DBInsert.insertDataFromDir(): FileNotFoundException: "
                    + ex.getMessage());
        }

        return true;
    }// insertDataFromDir()

    /**
     * Inserts data into the training table from a csv text file.
     *
     * @author Charles Davenport
     * @param csvPath - path to csv file
     * @param dataset - name of dataset used in training
     * @param modelName - name of CNN model, must match a record in cnn table
     * @return true if successful, false otherwise.
     */
    private static boolean insertTrain(String csvPath, String dataset, String modelName) {
        final String SELECT_DATA_ID = "SELECT id FROM dataset WHERE name=?";
        final String SELECT_CNN_ID  = "SELECT id FROM cnn WHERE name=?";
        final String INSERT_TRAIN_QUERY = "INSERT INTO train (cnn_id, data_id, epoch, loss, acc) "
            + " VALUES(?, ?, ?, ?, ?)";

        Integer cnn_id     = null;
        Integer dataset_id = null;
        ResultSet rs;

        try {
            // get cnn_id from DB
            PreparedStatement pstmt = conn.prepareStatement(SELECT_CNN_ID);
            pstmt.setString(1, modelName);
            pstmt.execute();
            rs = pstmt.getResultSet();
            if (rs == null) {
                System.out.println("DBInsert.insertTrain(): Could not find cnn model: "
                    + modelName);
                return false;
            }
            rs.first();
            cnn_id = rs.getInt(1);

            // get dataset_id from DB
            pstmt = conn.prepareStatement(SELECT_DATA_ID);
            pstmt.setString(1, dataset);
            pstmt.execute();
            rs = pstmt.getResultSet();
            if (rs == null) {
                System.out.println("DBInsert.insertTrain(): Could not find dataset: "
                        + modelName);
                return false;
            }
            rs.first();
            dataset_id = rs.getInt(1);

            // open csv file containing training data
            File tFile = new File(csvPath);
            Scanner scn = new Scanner(tFile);
            String line;
            while (scn.hasNextLine()) {
                line = scn.nextLine();
                String [] items = line.split(",");
                //System.out.println(items[0] + ", " + items[1] + ", " + items[2]);
                int epoch = Integer.parseInt(items[0]);
                double loss = Double.parseDouble(items[1]);
                double acc = Double.parseDouble(items[2]);
                pstmt = conn.prepareStatement(INSERT_TRAIN_QUERY);
                pstmt.setInt(1, cnn_id);
                pstmt.setInt(2, dataset_id);
                pstmt.setInt(3, epoch);
                pstmt.setFloat(4, (float)loss);
                pstmt.setFloat(5, (float)acc);

                pstmt.executeUpdate();
            }



        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }


        return true;
    }

    public static boolean insertUser(String username, String hashedPWD){
        String query = "insert into users values (?,?)";
        try {
            connectToDB();
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, username);
            pStmt.setString(2, hashedPWD);
            pStmt.executeUpdate();
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Connect to mysql database.
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
            System.out.println("Connected to DB: " + conn.getCatalog());
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
