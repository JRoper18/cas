package CAS;

import CAS.EquationObjects.MathObject;
import Database.DatabaseConnection;
import Database.SubSerializer;
import com.rits.cloning.Cloner;

import java.sql.ResultSet;

/**
 * Created by jack on 1/5/2017.
 */
public class Simplifier {
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
                ResultSet results = DatabaseConnection.runQuery("select * from subs where (operator == '" + root.toString() + "')");
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
}
