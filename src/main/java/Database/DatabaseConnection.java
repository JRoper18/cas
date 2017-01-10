package Database;

import CAS.EquationObjects.MathObjects.MathObject;
import CAS.EquationSub;

import java.sql.*;

/**
 * Created by jack on 1/9/2017.
 */
public class DatabaseConnection {
    private static Connection connection = null;
    public static ResultSet runQuery(String query) throws SQLException{
        if(connection == null){
            makeConnection();
        }
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }
    public static void makeConnection(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:subs.db");
            Statement statement = connection.createStatement();

            statement.executeUpdate("drop table if exists subs");
            statement.executeUpdate("drop table if exists structurals");
            statement.executeUpdate("create table subs (algorithm blob, operator string)");
            statement.executeUpdate("create table structurals (before string, after string, condition string)");
            for(EquationSub sub : EquationSubDatabase.subs){
                String toPrepare = "insert into subs values(?, ?)";
                PreparedStatement prepared = connection.prepareStatement(toPrepare);
                prepared.setBytes(1, SubSerializer.serialize(sub));
                MathObject op = sub.rootOperator;
                if(op != null){
                    prepared.setString(2, op.toString());
                }
                else{
                    prepared.setString(2, "NONE");
                }
                prepared.executeUpdate();
            }
        } catch(SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}
