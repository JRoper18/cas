package Database;

import CAS.*;
import CAS.EquationObjects.MathObjects.MathInteger;
import CAS.EquationObjects.MathObjects.MathObject;
import CAS.EquationObjects.MathObjects.MathSymbol;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jack on 1/8/2017.
 */
public class EquationSubDatabase { //NOTE: I know, I know, this should be in the actual SQLite database. I WILL do that, but I want to keep these here in case something goes wrong or I want to reference them directly. Once every sub is in the database I can remove this, but UNTIL THEN, it's a good idea to keep these here just in case.
    private static final EquationSub[] subsArray = {
            new EquationSub(new Equation("PATTERN_OR ( OR ( TRUE , FALSE ) , OR ( FALSE , TRUE ) , OR ( TRUE , TRUE ) )"), new Equation("TRUE")).setOp(new MathObject(MathSymbol.OR)),
            new EquationSub(new Equation("OR ( FALSE , FALSE )"), new Equation("FALSE")).setOp(new MathObject(MathSymbol.OR)),
            new EquationSub(new Equation("AND ( TRUE , TRUE )"), new Equation("TRUE")).setOp(new MathObject(MathSymbol.AND)),
            new EquationSub(new Equation("PATTERN_OR ( AND ( TRUE , FALSE ) , AND ( FALSE , TRUE ) , AND ( FALSE , FALSE ) )"), new Equation("FALSE")).setOp(new MathObject(MathSymbol.AND)),
            new EquationSub(new Equation("EQUALS ( _v1 , _v1 )"), new Equation("TRUE")).setOp(new MathObject(MathSymbol.EQUALS)),
            new EquationSub(new Equation("EQUALS ( _v1 , _v2 )"), new Equation("FALSE")).setOp(new MathObject(MathSymbol.EQUALS)),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                PatternMatcher matcher = new PatternMatcher();
                if (matcher.patternMatch(eq, new Equation("TYPEOF ( _v1 )"))) {
                    HashMap<String, Tree<MathObject>> vars = matcher.getLastMatchExpressions();
                    Tree<MathObject> objectTree = vars.get("v1");
                    if (objectTree.hasChildren() && objectTree.data.getOperator() != MathSymbol.FRACTION) {
                        return new Equation("EXPRESSION");
                    } else {
                        return new Equation(objectTree.data.getOperator().toString());
                    }
                } else {
                    return eq; //No change
                }
            })).setOp(new MathObject(MathSymbol.TYPEOF)),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                if (eq.tree.data.equals(new MathObject(MathSymbol.ADD)) && eq.tree.getNumberOfChildren() == 2) {
                    if (eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger) {
                        return new Equation(((MathInteger) eq.tree.getChild(0).data).add((MathInteger) eq.tree.getChild(1).data).toString());
                    }
                }
                return eq; //No change
            })).setOp(new MathObject(MathSymbol.ADD)),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                if (eq.tree.data.equals(new MathObject(MathSymbol.MULTIPLY)) && eq.tree.getNumberOfChildren() == 2) {
                    if (eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger) {
                        return new Equation(((MathInteger) eq.tree.getChild(0).data).mul((MathInteger) eq.tree.getChild(1).data).toString());
                    }
                }
                return eq; //No change
            })).setOp(new MathObject(MathSymbol.MULTIPLY))
    };
    public static final HashSet<EquationSub> subs = new HashSet<EquationSub>(Arrays.asList(subsArray));
}
