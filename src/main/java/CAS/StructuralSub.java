package CAS;

import CAS.EquationObjects.MathObjects.GenericExpression;
import CAS.EquationObjects.MathObjects.MathObject;
import Database.DatabaseConnection;
import com.rits.cloning.Cloner;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by jack on 1/9/2017.
 */
public class StructuralSub extends EquationSub implements Serializable {
    public Equation before;
    public Equation after;
    public Equation condition;
    public StructuralSub(String before, String after){
        this(new Equation(before), new Equation(after));
    }
    public StructuralSub(String before, String after, String condition){
        this(new Equation(before), new Equation(after), new Equation(condition));
    }
    public StructuralSub(Equation before, Equation after, Equation condition){
        super((DirectOperation & Serializable) ( equation -> {
            return substitute(before, after, condition, equation);
        }), getProbableAssignedOperator(before));
        this.before = before;
        this.after = after;
        this.condition = condition;
    }
    public StructuralSub(Equation before, Equation after){
        super((DirectOperation & Serializable) (eq -> {
            return substitute(before, after, null, eq);
        }), getProbableAssignedOperator(before));
        this.before = before;
        this.after = after;
        this.condition = null;
    }
    private static Equation substitute(Equation before, Equation after, Equation condition, Equation equation){
        PatternMatcher matcher = new PatternMatcher();
        if(matcher.patternMatch(equation, before)) {
            Equation newEquation = new Equation(after); //Quick clone
            HashMap<String, Tree<MathObject>> values = matcher.getLastMatchExpressions();
            //Go through conditions
            if(condition != null){
                Cloner cloner = new Cloner();
                Equation temp = cloner.deepClone(condition);
                //Replace generics
                for(String var : values.keySet()){
                    Tree<MathObject> substitution = values.get(var);
                    GenericExpression genExToLookFor = new GenericExpression(var);
                    temp.tree.replaceAll(new Tree(genExToLookFor), substitution);
                }
                Equation simplified = Simplifier.simplify(temp);
                if(simplified.equals(new Equation("FALSE"))){
                    return equation; //Again, do nothing to the equation.
                }
            }
            for (String var : values.keySet()) {
                Tree<MathObject> substitution = values.get(var);
                GenericExpression genExToLookFor = new GenericExpression(var);
                newEquation.tree.replaceAll(new Tree(genExToLookFor), substitution);
            }
            return newEquation;
        }
        else {
            //Can we change the equation so it fits the before pattern equation?
            Tree<MathObject> whatWeWanted = matcher.getExpectedTree();
            Tree<MathObject> whatWeGot = equation.tree.getChildThroughPath(matcher.getPathToFail());
            //Now search the database for that pattern
            try {
                Equation newEq = equation.clone();
                ResultSet results = DatabaseConnection.runQuery("select * from structurals where (before == '" + new Equation(whatWeGot).toString() + "' AND after == '" + new Equation(whatWeWanted).toString() + "')");
                while(results.next()){
                    newEq = new StructuralSub(results.getString("before"), results.getString("after"), results.getString("condition")).apply(newEq);
                }
                if(newEq.equals(equation)){
                    return equation; //Nothing we can do.
                }
                return substitute(before, after, condition, newEq);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    private static MathObject getProbableAssignedOperator(Equation equation){
        MathObject probableOperator = null;
        if(equation.tree.data.getOperator().toString().contains("PATTERN")){ //Note to future self: Make a way to identify pattern objects from mathobjects.
            //Don't check pattern objects. Check the children for a mathobject.
            for(Tree<MathObject> child : equation.tree.getChildren()){
                MathObject possible = getProbableAssignedOperator(new Equation(child));
                if(probableOperator == null){
                    probableOperator = possible;
                }
                else{
                    if(!probableOperator.equals(possible)){
                        return null; //There's a difference. We can't tell.
                    }
                }
            }
            //Made it to the end with all of the operators the same? Probably that operator.
            return probableOperator;
        }
        else{
            return equation.tree.data;
        }
    }
}
