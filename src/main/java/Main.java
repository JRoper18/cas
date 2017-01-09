import java.sql.*;

/**
 * Created by jack on 1/8/2017.
 */
public class Main {
    public static void main(String[] args){
        connectToSQLite();
    }
    public static void connectToSQLite(){
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:subs.db");
            System.out.println("Opened database successfully");
            Statement statement = c.createStatement();

            statement.executeUpdate("DROP TABLE IF EXISTS SUBS");
            statement.executeUpdate("create table subs (id integer, operator string)");
            statement.executeUpdate("INSERT INTO SUBS VALUES(1 , 'OR')");
            ResultSet rs = statement.executeQuery("SELECT * FROM SUBS");
            while (rs.next()) {
                System.out.println(rs.getString("operator"));
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
