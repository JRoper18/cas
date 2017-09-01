package Database;

import CAS.Equation;
import CAS.EquationObjects.GenericExpression;
import CAS.EquationObjects.GenericFunction;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import Identification.IdentificationType;
import Util.Tree;
import Substitution.EquationSub;
import Substitution.StructuralSub;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jack on 1/9/2017.
 */
public class DatabaseConnection {
    private static final int MAX_FUNCTION_OPERATORS = 3;
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
            statement.executeUpdate("create table subs (id int primary key not null, algorithm blob not null, operator string, subtype string, operatorcost int)");
            int idCount = 0;
            for(EquationSub sub : EquationSubDatabase.subs){

                String toPrepare = "insert into subs values(?, ?, ?, ?, ?)";
                PreparedStatement prepared = connection.prepareStatement(toPrepare);
                prepared.setInt(1, idCount);
                prepared.setBytes(2, SubSerializer.serialize(sub));
                MathObject op = sub.rootOperator;
                if(op != null){
                    prepared.setString(3, op.toString());
                    prepared.setString(4, op.getOperator().getSubType().toString());
                    prepared.setInt(5, 0);
                }
                prepared.executeUpdate();
                idCount++;
            }
            for(String ruleStr : SubstitutionRuleDatabase.rules){
                if(ruleStr.isEmpty()){
                    continue;
                }
                idCount = addStructuralSubs(ruleStr, idCount);
            }
            System.out.println("Database initialized");
        } catch(SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static int addStructuralSubs(String subStr, int idCount) throws SQLException{
        StructuralSub sub = new StructuralSub(subStr);
        List<LinkedList<Integer>> paths = sub.before.tree.findPaths((tree -> {
            return (((MathObject) tree.data).getOperator() == MathOperator.GENERIC_FUNCTION && ((MathObject) tree.getChild(0).data).getOperator() == MathOperator.EXPRESSION && tree.getNumberOfChildren() == 1);
        }));
        List<StructuralSub> subsToAdd = new ArrayList<>();
        if(paths.size() != 0){
            for(LinkedList<Integer> path : paths){
                for(int i = 1; i<=MAX_FUNCTION_OPERATORS; i++){
                    for(int j = 0; j<i; j++){
                        Equation newBeforeSub = sub.before.clone();
                        Tree<MathObject> toBeReplaced = newBeforeSub.tree.getChildThroughPath(path);
                        MathObject expression = toBeReplaced.getChild(0).data;
                        Tree<MathObject> replacement = new Tree<>(toBeReplaced.data);
                        for(int k = 0; k<i; k++){
                            if(k== j){
                                replacement.addChildWithData(expression);
                            }
                            else{
                                replacement.addChildWithData(new GenericExpression(((GenericFunction) toBeReplaced.data).getName() + "v" + k, false, IdentificationType.EXPRESSION));
                            }
                        }
                        toBeReplaced.replaceWith(replacement);
                        subsToAdd.add(new StructuralSub(newBeforeSub, sub.after));
                    }
                }
            }
        }
        else{
            subsToAdd.add(sub);
        }
        for(StructuralSub addSub: subsToAdd){
            String toPrepare = "insert into subs values(?, ?, ?, ?, ?)";
            PreparedStatement prepared = connection.prepareStatement(toPrepare);
            prepared.setInt(1, idCount);
            try{
                prepared.setBytes(2, SubSerializer.serialize(addSub));
            } catch (Exception e){
                return idCount;
            }
            MathObject op = sub.rootOperator;
            if(op != null){
                prepared.setString(3, op.toString());
                prepared.setString(4, op.getOperator().getSubType().toString());
                prepared.setInt(5, addSub.operatorCost);
            }
            prepared.executeUpdate();
            idCount++;
        }
        return idCount;
    }
}
