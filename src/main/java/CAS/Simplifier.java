package CAS;

import CAS.EquationObjects.MathObjects.MathInteger;
import CAS.EquationObjects.MathObjects.MathObject;
import CAS.EquationObjects.MathObjects.MathSymbol;
import com.rits.cloning.Cloner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jack on 1/5/2017.
 */
public class Simplifier {
    public static Equation booleanSimplify(Equation equation){
        List<EquationSub> subs = new ArrayList<>();
        Cloner cloner = new Cloner();
        subs.add(new EquationSub(new Equation("PATTERN_OR ( OR ( TRUE , FALSE ) , OR ( FALSE , TRUE ) , OR ( TRUE , TRUE ) )"), new Equation("TRUE")));
        subs.add(new EquationSub(new Equation("OR ( FALSE , FALSE )"), new Equation("FALSE")));
        subs.add(new EquationSub(new Equation("AND ( TRUE , TRUE )"), new Equation("TRUE")));
        subs.add(new EquationSub(new Equation("PATTERN_OR ( AND ( TRUE , FALSE ) , AND ( FALSE , TRUE ) , AND ( FALSE , FALSE ) )"), new Equation("FALSE")));
        subs.add(new EquationSub(new Equation("EQUALS ( _v1 , _v1 )"), new Equation ("TRUE")));
        subs.add(new EquationSub(new Equation("EQUALS ( _v1 , _v2 )"), new Equation ("FALSE")));
        Equation newEq = cloner.deepClone(equation);
        Equation last;
        do {
            last = cloner.deepClone(newEq);
            for(EquationSub sub : subs){
                newEq = sub.applyEverywhere(newEq);
            }
        } while(!last.equals(newEq));
        return newEq;
    }
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
    public static Equation fullSimplify(Equation equation){
        Cloner cloner = new Cloner();
        Equation newEq = cloner.deepClone(equation);
        newEq = getTypeOf(newEq);
        newEq = booleanSimplify(newEq);
        newEq = basicSimplify(newEq);
        return newEq;
    }
}
