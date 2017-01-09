package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by jack on 1/9/2017.
 */
public class SubDatabase {
    private static Connection connection;
    public static ResultSet runQuery(String query) throws SQLException{
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }
    public static void setConnection(Connection c){
        connection = c;
    }
}
