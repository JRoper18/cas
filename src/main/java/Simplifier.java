import EquationObjects.MathObjects.MathInteger;
import EquationObjects.MathObjects.MathObject;
import EquationObjects.MathObjects.MathSymbol;
import com.rits.cloning.Cloner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 1/5/2017.
 */
public class Simplifier {
    public static Equation booleanSimplify(Equation equation){
        List<EquationSub> subs = new ArrayList<>();
        subs.add(new EquationSub(new Equation("PATTERN_OR ( OR ( TRUE , FALSE ) , OR ( FALSE , TRUE ) , OR ( TRUE , TRUE ) )"), new Equation("TRUE")));
        subs.add(new EquationSub(new Equation("OR ( FALSE , FALSE )"), new Equation("FALSE")));
        subs.add(new EquationSub(new Equation("AND ( TRUE , TRUE )"), new Equation("TRUE")));
        subs.add(new EquationSub(new Equation("PATTERN_OR ( AND ( TRUE , FALSE ) , AND ( FALSE , TRUE ) , AND ( FALSE , FALSE ) )"), new Equation("FALSE")));
        subs.add(new EquationSub(new Equation("EQUALS ( _v1 , _v1 )"), new Equation ("TRUE")));
        subs.add(new EquationSub(new Equation("EQUALS ( _v1 , _v2 )"), new Equation ("FALSE")));
        Cloner cloner = new Cloner();
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
    public static Equation basicSimplify(Equation equation){
        List<EquationSub> subs = new ArrayList<>();
        subs.add(new EquationSub(new Equation("DIVIDE ( _v1 , 1 )"), new Equation("_v1")));
        subs.add(new EquationSub((eq -> {
            if(eq.tree.data.equals(new MathObject(MathSymbol.ADD)) && eq.tree.getNumberOfChildren() >= 2){
                if(eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger){
                    return new Equation(((MathInteger) eq.tree.getChild(0).data).add((MathInteger) eq.tree.getChild(1).data).toString());
                }
            }
            return eq; //No change
        })));
        subs.add(new EquationSub((eq -> {
            if(eq.tree.data.equals(new MathObject(MathSymbol.MULTIPLY)) && eq.tree.getNumberOfChildren() >= 2){
                if(eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger){
                    return new Equation(((MathInteger) eq.tree.getChild(0).data).mul((MathInteger) eq.tree.getChild(1).data).toString());
                }
            }
            return eq; //No change
        })));
        subs.add(new EquationSub((eq -> {
            if(eq.tree.data.equals(new MathObject(MathSymbol.SUBTRACT)) && eq.tree.getNumberOfChildren() >= 2){
                if(eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger){
                    return new Equation(((MathInteger) eq.tree.getChild(0).data).sub((MathInteger) eq.tree.getChild(1).data).toString());
                }
            }
            return eq; //No change
        })));
        subs.add(new EquationSub(new Equation("DIVIDE ( _v1 , _v2 ) "), new Equation("TEST")));
        for(EquationSub sub : subs){
            equation = sub.apply(equation);
        }
        return equation;
    }
}
