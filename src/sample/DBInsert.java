package sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;

/**
 * Class for inserting images, csv data into the mysql db
 *
 * @author Stone Daniel, Charles Davenport
 */
public class DBInsert {

    private static Connection conn;
    private static final String url = "jdbc:mysql://localhost:3306/cnn_util";

    private static final String imgDir = "D:\\cifar_images";
    private static final String classesFilePath = "D:\\Github\\CNNUtil\\store\\dataset\\CIFAR-10_classes.csv";
    private static final String trainCSVPath = "D:\\Github\\CNNUtil\\store\\train\\MNIST_CNN_small_2.csv";
    private static final String datasetName = "MNIST";
    private static final String cnnName = "MNIST_CNN_small_2";
    private static final String modelsDir = "D:\\Github\\CNNUtil\\store\\models\\";


    /* Check the class variables before uncommenting these functions and running.
     * These methods will make updates to the DB, so be careful
     */
    public static void main(String[] args) {

        connectToDB();
        //boolean success = insertDataFromDir(imgDir, classesFilePath, datasetName, 10);
        //System.out.println(success ? "Successfully inserted images into DB"
        //        : "Failed to insert images to DB");

        //boolean success = updateDataClasses(classesFilePath, datasetName, imgDir);
        //System.out.println(success ? "Successfully updated image classes in DB"
        //        : "Failed to update image classes in DB");

        //boolean success = insertTrain(trainCSVPath, datasetName, cnnName);
        //System.out.println(success ? "Successfully inserted train data into DB"
        //                : "Failed to insert train data into DB");

        //insertNewModel(modelsDir, "CIFAR_CNN_small");
        //insertNewModel(modelsDir, "CIFAR_CNN_small_2");

    }

    private static boolean insertNewModel(String dir, String modelName) {

        String modelPath = dir + modelName + ".csv";
        String layersPath = dir + modelName + "_layers.csv";

        String modelQuery = "INSERT INTO cnn (name, layers, trainable_params, hyper_params, size_kb, file_path, optimizer) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String modelIdQuery = "SELECT id from cnn WHERE name=?";
        String layerQuery = "INSERT INTO layer (cnn_id, depth, type, params, filters, kernel_x, kernel_y, stride_x, stride_y, input_x, input_y, input_z, output_x, output_y, output_Z) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,)";

        try {
            connectToDB();

            File modelFile = new File(modelPath);
            File layersFile = new File(layersPath);
            Scanner scn = new Scanner(modelFile);
            String line;
            if (scn.hasNextLine()) line = scn.nextLine();
            else return false;

            String [] items = line.split(",");
            System.out.println(Arrays.toString(items));

            PreparedStatement pstmt = conn.prepareStatement(modelQuery);
            pstmt.setString(1, modelName);                     // name
            pstmt.setInt(2, Integer.parseInt(items[2]));      // layers
            pstmt.setInt(3, Integer.parseInt(items[3]));      // trainable_params
            pstmt.setInt(4, Integer.parseInt(items[4]));      // hyper_params
            pstmt.setDouble(5, Double.parseDouble(items[5])); // size_kb
            pstmt.setString(6, items[6]);                     // file_path
            pstmt.setString(7, items[7]);                     // optimizer
            System.out.println(pstmt.toString());
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement(modelIdQuery);
            //pstmt.setString(1, modelName);
            //pstmt.execute();
            //ResultSet rs = pstmt.getResultSet();
            //rs.first();
            //int cnnId = rs.getInt("id");

            //(cnn_id, depth, type, params, filters, kernel_x, kernel_y, stride_x, stride_y, input_x, input_y, input_z, output_x, output_y, output_Z)
            scn = new Scanner(layersFile);
            while(scn.hasNextLine()) {
                line = scn.nextLine();
                String [] layer_items = line.split(",");
                //System.out.println(Arrays.toString(layer_items));
                pstmt = conn.prepareStatement(layerQuery);
                int depth = Integer.parseInt(layer_items[2]);
                String type = layer_items[3];
                int params = Integer.parseInt(layer_items[4]);
                System.out.println(layer_items[5]);
                int filters;
                String input_shape = items[6].replace('(', '\0').replace(')', '\0');
                System.out.println(input_shape);
                //pstmt.setInt(1, cnnId);

            }


        }
        catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }

        return true;
    }



    /**
     * I wrote this utility function, because I initially inserted all
     * the images to the DB without setting the class properly. Now the records
     * have to be updated ¯\_(ツ)_/¯
     * @param classFile - path to csv file
     * @param dataset - dataset name
     * @return true if successful, false otherwise
     *
     * @author Charles Davenport
     */
    private static boolean updateDataClasses(String classFile, String dataset, String dirPath) {
        final String UPDATE_CLASSES = "UPDATE data SET image=? WHERE dataset=1 AND id=?";

        //List<Integer> classValues = new ArrayList<Integer>(70000);
        File cFile = new File(classFile);
        File dir = new File(dirPath);
        String [] dirContents = dir.list();
        try {
            Scanner scn = new Scanner(cFile);
            while(scn.hasNextLine()) {
                String line = scn.nextLine();
                //System.out.println(line);
                String [] items = line.split(",");
                int imgId = Integer.parseInt(items[0]);
                String imgPath = dirPath + "\\" + imgId + ".png";
                int classNum = Integer.parseInt(items[1]);

                File img_file = new File(imgPath);
                FileInputStream inputs = new FileInputStream(img_file);

                System.out.println("Image " + imgId + ": class = " + classNum);

                PreparedStatement pstmt = conn.prepareStatement(UPDATE_CLASSES);
                pstmt.setBinaryStream(1, inputs);
                pstmt.setInt(2, imgId);

                //System.out.println(pstmt.toString());
                pstmt.executeUpdate();
            }
        }
        catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Static method for inserting data (images) into DB.
     * Dataset must already exist in DB.
     *
     * @param dirPath - directory to load images from
     * @param dataset - name of dataset we're adding to
     * @param folds - how many folds to split data into
     * @return true if successful, false if not
     *
     * @author Charles Davenport
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
                System.out.println(dirContents[i]);
                String img_path = dirPath + "\\" + i + ".png";
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

                System.out.println(pstmt);


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
     * @param csvPath - path to csv file
     * @param dataset - name of dataset used in training
     * @param modelName - name of CNN model, must match a record in cnn table
     * @return true if successful, false otherwise.
     *
     * @author Charles Davenport
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

    /**
     * Stores a new user's login information
     * @param username username to be stored for future login
     * @param hashedPWD hashed password to be stored for future login
     * @return true if user successfully inserted, false otherwise
     *
     * @author Stone Daniel
     */
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
