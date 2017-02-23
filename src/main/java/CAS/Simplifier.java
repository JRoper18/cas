package CAS;

import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import CAS.EquationObjects.MathOperatorSubtype;
import Database.DatabaseConnection;
import Database.SubSerializer;
import com.rits.cloning.Cloner;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.ResultSet;

/**
 * Created by jack on 1/5/2017.
 */
public class Simplifier {
    public static Equation simplifyByOperator(Equation eq, MathObject operation){
        Equation newEq = eq.clone();
        try{
            ResultSet results = DatabaseConnection.runQuery("select algorithm from subs where (operator == '" + operation + "')");
            while(results.next()){
                EquationSub tempSub = SubSerializer.deserialize(results.getBytes("algorithm"));
                newEq = tempSub.apply(newEq);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return newEq;
    }
    public static Equation simplifyWithMetaFunction(Equation eq, MathOperator metaFunction){
        if(metaFunction.getSubType() != MathOperatorSubtype.META){
            throw new UncheckedIOException(new IOException("Function provided is not a meta function!"));
        }
        Tree<MathObject> newTree = new Tree<>(new MathObject(metaFunction));
        newTree.addChild(eq.tree);
        return simplifyMetaFunctions(new Equation(newTree));
    }
    public static Equation simplifyByOperator(Equation eq){
        return simplifyByOperator(eq, eq.getRoot());
    }
    public static Equation simplify(Equation eq){
        Cloner cloner = new Cloner();
        Equation newEq = cloner.deepClone(eq);
        //Start from the bottom of the tree to the top.
        if(newEq.tree.hasChildren()){
            for(Tree<MathObject> child : newEq.tree.getChildren()){
                Equation simplified = simplify(new Equation(child));
                child.replaceWith(simplified.tree);
            }
        }
        //Get newEq's root term.
        MathObject root = newEq.getRoot();
        //Find all the substitutions from the database that operate on that term.
        int minComplexity;
        Equation lastIteration;
        do{
            minComplexity = newEq.complexity();
            lastIteration = newEq;
            try{
                ResultSet results = DatabaseConnection.runQuery("select algorithm from subs where (operator == '" + root.toString() + "')");
                while(results.next()){
                    EquationSub tempSub = SubSerializer.deserialize(results.getBytes("algorithm"));
                    newEq = tempSub.apply(newEq);
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
        } while (!lastIteration.equals(newEq) && newEq.complexity() < minComplexity);
        return newEq;
    }
    public static Equation simplifyMetaFunctions(Equation equation){
        Equation newEq = equation.clone();
        Equation last = null;
        while (newEq.getRoot().getOperator().getSubType() == MathOperatorSubtype.META && !newEq.equals(last)){
            last = newEq.clone();
            try{
                String sql = "select algorithm from subs where (subtype =='META' and operator == '" + equation.getRoot().toString() + "')";
                ResultSet results = DatabaseConnection.runQuery(sql);
                while(results.next()){
                    EquationSub tempSub = SubSerializer.deserialize(results.getBytes("algorithm"));
                    newEq = tempSub.apply(newEq);
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return newEq;
    }
}
