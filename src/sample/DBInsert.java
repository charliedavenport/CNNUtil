package sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Properties;

/**
 * Class for inserting images, csv data into the mysql db
 *
 * @author Charles Davenport
 */
public class DBInsert {

    private static Connection conn;
    private static final String url = "jdbc:mysql://localhost:3306/cnn_util";

    private static final String imgDir = "D:\\Github\\ACM_ML\\Spring 2019\\MNIST_img";
    private static final String datasetName = "MNIST";


    public static void main(String[] args) {

        connectToDB();
        boolean success = insertDataFromDir(imgDir, datasetName, 10);
        System.out.println(success ? "Sucessfully inserted images into DB"
                : "Failed to insert images to DB");

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
    private static boolean insertDataFromDir(String dirPath, String dataset, int folds) {
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

            File dir = new File(dirPath);
            String [] dirContents = dir.list();
            // WARNING - this loop can take a long time to complete. Debug with smaller amt of images
            for (int i=100; i<dirContents.length; i++) {
                //System.out.println(dirContents[i]);
                String img_path = dirPath + "\\" + dirContents[i];
                File img_file = new File(img_path);
                FileInputStream inputs = new FileInputStream(img_file);

                pstmt = conn.prepareStatement(INSERT_QUERY);
                pstmt.setInt   (1, dataset_id);
                pstmt.setInt   (2, i); // TO DO: this should autoincrement
                pstmt.setString(3, img_path);
                pstmt.setInt   (4, 0); //TO DO: folds
                pstmt.setInt   (5, 0); //TO DO: class
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
