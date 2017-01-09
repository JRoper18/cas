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
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

}
