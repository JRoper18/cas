import CAS.EquationObjects.MathObjects.MathObject;
import CAS.EquationSub;
import Database.EquationSubDatabase;

import java.io.*;
import java.sql.*;

/**
 * Created by jack on 1/8/2017.
 */
public class Main {
    public static void main(String[] args){
        connectDatabase();
    }
    private static byte[] serializeEquationSub(EquationSub sub) throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(sub);
            out.flush();
            return bos.toByteArray();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }
    private static EquationSub deserializeEquationSub(byte[] bytes) throws Exception{
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (EquationSub) in.readObject();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }
    private static void connectDatabase(){
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:subs.db");
            System.out.println("Opened database successfully");
            Statement statement = c.createStatement();

            statement.executeUpdate("drop table if exists subs");
            statement.executeUpdate("create table subs (algorithm blob, operator string)");
            for(EquationSub sub : EquationSubDatabase.subs){
                String toPrepare = "insert into subs values(?, ?)";
                PreparedStatement prepared = c.prepareStatement(toPrepare);
                prepared.setBytes(1, serializeEquationSub(sub));
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
