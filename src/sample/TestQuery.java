package sample;

import java.sql.*;
import java.util.Properties;

public class TestQuery {

    private static final String cnnSelectQuery = "SELECT * FROM cnn";

    public static ResultSet selectCNN() {
        ResultSet rs = null;
        Connection conn = null;
        String query = "SELECT * FROM cnn JOIN layer;";
        String url = "jdbc:mysql://localhost:3306/cnn_util";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Properties prop = new Properties();
            prop.put("user", "root");
            prop.put("password", "speedy123");
            prop.put("serverTimezone", "UTC");
            prop.put("allowMultiQueries", true);
            conn = DriverManager.getConnection(url, prop);

            CallableStatement cStmt = conn.prepareCall(query);
            boolean success = cStmt.execute();
            System.out.println(success);
            rs = cStmt.getResultSet();

        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException: " + ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

        return rs;
    }

}
