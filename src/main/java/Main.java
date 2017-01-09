import CAS.Equation;
import CAS.EquationObjects.MathObjects.MathObject;
import CAS.EquationSub;
import CAS.Simplifier;
import Database.EquationSubDatabase;
import Database.SubDatabase;
import Database.SubSerializer;

import java.sql.*;

/**
 * Created by jack on 1/8/2017.
 */
public class Main {
    public static void main(String[] args){
        connectDatabase();
        Equation test1 = new Equation ("OR ( FALSE , TRUE , TRUE )");
        Simplifier.simplify(test1).tree.print();
    }
    private static void connectDatabase(){
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:subs.db");
            SubDatabase.setConnection(c);
            System.out.println("Opened database successfully");
            Statement statement = c.createStatement();

            statement.executeUpdate("drop table if exists subs");
            statement.executeUpdate("create table subs (algorithm blob, operator string)");

            for(EquationSub sub : EquationSubDatabase.subs){
                String toPrepare = "insert into subs values(?, ?)";
                PreparedStatement prepared = c.prepareStatement(toPrepare);
                prepared.setBytes(1, SubSerializer.serialize(sub));
                MathObject op = sub.properties.assignedOperator;
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
