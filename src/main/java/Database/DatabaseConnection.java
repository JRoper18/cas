package Database;

import CAS.EquationObjects.MathObject;
import CAS.EquationSub;
import CAS.StructuralSub;

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
            System.out.println("Opened database successfully");
            Statement statement = connection.createStatement();

            statement.executeUpdate("drop table if exists subs");
            statement.executeUpdate("create table subs (id int primary key not null, algorithm blob not null, operator string, subtype string)");
            int idCount = 0;
            for(EquationSub sub : EquationSubDatabase.subs){
                String toPrepare = "insert into subs values(?, ?, ?, ?)";
                PreparedStatement prepared = connection.prepareStatement(toPrepare);
                prepared.setInt(1, idCount);
                prepared.setBytes(2, SubSerializer.serialize(sub));
                MathObject op = sub.rootOperator;
                if(op != null){
                    prepared.setString(3, op.toString());
                    prepared.setString(4, op.getOperator().getSubType().toString());
                }
                prepared.executeUpdate();
                idCount++;
            }
            for(String ruleStr : SubstitutionRuleDatabase.rules){
                int equalsIndex = ruleStr.indexOf('=');
                StructuralSub sub = new StructuralSub(ruleStr.substring(0, equalsIndex), ruleStr.substring(equalsIndex + 1, ruleStr.length()));
                String toPrepare = "insert into subs values(?, ?, ?, ?)";
                PreparedStatement prepared = connection.prepareStatement(toPrepare);
                prepared.setInt(1, idCount);
                prepared.setBytes(2, SubSerializer.serialize(sub));
                MathObject op = sub.rootOperator;
                if(op != null){
                    prepared.setString(3, op.toString());
                    prepared.setString(4, op.getOperator().getSubType().toString());
                }
                prepared.executeUpdate();
                idCount++;
            }
            System.out.println("Database initialized");
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
