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
        final String query = "SELECT name FROM CNN";
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


    public static ResultSet selectCNN() {
        ResultSet rs = null;
        //Connection conn = null;
        String query = "SELECT * FROM cnn JOIN layer;";
        try {
            connectToDB();

            CallableStatement cStmt = conn.prepareCall(query);
            boolean success = cStmt.execute();
            System.out.println(success);
            rs = cStmt.getResultSet();

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

        return rs;
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
