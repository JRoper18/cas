package CAS;

import CAS.EquationObjects.MathObjects.MathObject;
import Database.SubDatabase;
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
        //Get newEq's root term.
        MathObject root = newEq.getRoot();
        //Find all the substitutions from the database that operate on that term.
        int minComplexity;
        int latestComplexity;
        Equation lastIteration;
        do{
            minComplexity = newEq.complexity();
            lastIteration = newEq;
            try{
                ResultSet results = SubDatabase.runQuery("select * from subs where (operator == '" + root.toString() + "')");
                while(results.next()){
                    EquationSub tempSub = SubSerializer.deserialize(results.getBytes("algorithm"));
                    newEq = tempSub.apply(newEq);
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
            latestComplexity = newEq.complexity();
        } while (!lastIteration.equals(newEq));
        return newEq;
    }
    /*
    public static Equation getTypeOf(Equation equation){
        EquationSub typeofSub = new EquationSub((eq -> {
            PatternMatcher matcher = new PatternMatcher();
            if(matcher.patternMatch(eq, new Equation("TYPEOF ( _v1 )"))){
                HashMap<String, Tree<MathObject>> vars = matcher.getLastMatchExpressions();
                Tree<MathObject> objectTree = vars.get("v1");
                if(objectTree.hasChildren() && objectTree.data.getOperator() != MathSymbol.FRACTION){
                    return new Equation("EXPRESSION");
                }
                else{
                    return new Equation(objectTree.data.getOperator().toString());
                }
            }
            else{
                return eq; //No change
            }
        }));
        return typeofSub.apply(equation);

    }
    public static Equation basicSimplify(Equation equation){
        List<EquationSub> subs = new ArrayList<>();
        EquationSub addSub = (new EquationSub((eq -> {
            if(eq.tree.data.equals(new MathObject(MathSymbol.ADD)) && eq.tree.getNumberOfChildren() == 2){
                if(eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger){
                    return new Equation(((MathInteger) eq.tree.getChild(0).data).add((MathInteger) eq.tree.getChild(1).data).toString());
                }
            }
            return eq; //No change
        })));
        EquationSub mulSub = new EquationSub((eq -> {
            if(eq.tree.data.equals(new MathObject(MathSymbol.MULTIPLY)) && eq.tree.getNumberOfChildren() == 2){
                if(eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger){
                    return new Equation(((MathInteger) eq.tree.getChild(0).data).mul((MathInteger) eq.tree.getChild(1).data).toString());
                }
            }
            return eq; //No change
        }));
        EquationSub fractionSub = new EquationSub(eq -> {

            return new Equation("FIX");
        });
        addSub.properties.assignedOperator = new MathObject(MathSymbol.ADD);
        mulSub.properties.assignedOperator = new MathObject(MathSymbol.MULTIPLY);

        subs.add(new EquationSub(new Equation("DIVIDE ( _v1 , 1 )"), new Equation("_v1")));
        subs.add(addSub);
        subs.add(mulSub);

        for(EquationSub sub : subs){
            equation = sub.apply(equation);
        }
        return equation;
    }
    */
}
