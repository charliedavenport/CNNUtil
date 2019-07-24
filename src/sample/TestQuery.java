package sample;

import java.sql.*;
import java.util.Properties;

public class TestQuery {

    public static ResultSet selectCNN() {
        ResultSet rs = null;
        Connection conn = null;
        String query = "SELECT * FROM cnn;";
        String url = "jdbc:mysql://localhost:3306/cnn_util";

        try {
            Class.forName("com.mysql.jdbc.Driver");

            Properties prop = new Properties();
            prop.put("user", "java");
            prop.put("password", "admin");
            prop.put("serverTimezone", "UTC");
            prop.put("allowMultiQueries", true);
            conn = DriverManager.getConnection(url, prop);

            CallableStatement cStmt = conn.prepareCall(query);
            boolean success = cStmt.execute();
            rs = cStmt.getResultSet();

        } catch (ClassNotFoundException ex) {

        } catch (SQLException ex) {

        }

        return rs;
    }

}
