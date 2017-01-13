package Database;

import CAS.*;
import CAS.EquationObjects.MathInteger;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathSymbol;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;



/**
 * Created by jack on 1/8/2017.
 */
public class EquationSubDatabase { //NOTE: I know, I know, this should be in the actual SQLite database. I WILL do that, but I want to keep these here in case something goes wrong or I want to reference them directly. Once every sub is in the database I can remove this, but UNTIL THEN, it's a good idea to keep these here just in case.
    private static final EquationSub[] subsArray = {
            new StructuralSub(new Equation("PATTERN_OR ( OR ( TRUE , FALSE ) , OR ( FALSE , TRUE ) , OR ( TRUE , TRUE ) )"), new Equation("TRUE")),
            new StructuralSub(new Equation("OR ( FALSE , FALSE )"), new Equation("FALSE")),
            new StructuralSub(new Equation("AND ( TRUE , TRUE )"), new Equation("TRUE")),
            new StructuralSub(new Equation("PATTERN_OR ( AND ( TRUE , FALSE ) , AND ( FALSE , TRUE ) , AND ( FALSE , FALSE ) )"), new Equation("FALSE")),
            new StructuralSub(new Equation("EQUALS ( _v1 , _v1 )"), new Equation("TRUE")),
            new StructuralSub(new Equation("EQUALS ( _v1 , _v2 )"), new Equation("FALSE")),
            new StructuralSub(new Equation("NOT ( FALSE )"), new Equation("TRUE")),
            new StructuralSub(new Equation("NOT ( TRUE )"), new Equation("FALSE")),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                PatternMatcher matcher = new PatternMatcher();
                if (matcher.patternMatch(eq, new Equation("TYPEOF ( _v1 )"))) {
                    HashMap<String, Tree<MathObject>> vars = matcher.getLastMatchExpressions();
                    Tree<MathObject> objectTree = vars.get("v1");
                    if (objectTree.hasChildren()) {
                        return new Equation("EXPRESSION");
                    } else {
                        return new Equation(objectTree.data.getOperator().toString());
                    }
                } else {
                    return eq; //No change
                }
            }),(new MathObject(MathSymbol.TYPEOF))),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                if (eq.tree.data.equals(new MathObject(MathSymbol.ADD)) && eq.tree.getNumberOfChildren() == 2) {
                    if (eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger) {
                        return new Equation(((MathInteger) eq.tree.getChild(0).data).add((MathInteger) eq.tree.getChild(1).data).toString());
                    }
                }
                return eq; //No change
            }), (new MathObject(MathSymbol.ADD))),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                if (eq.tree.data.equals(new MathObject(MathSymbol.MULTIPLY)) && eq.tree.getNumberOfChildren() == 2) {
                    if (eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger) {
                        return new Equation(((MathInteger) eq.tree.getChild(0).data).mul((MathInteger) eq.tree.getChild(1).data).toString());
                    }
                }
                return eq; //No change
            }), (new MathObject(MathSymbol.MULTIPLY))),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                PatternMatcher matcher = new PatternMatcher();
                if(matcher.patternMatch(eq, new Equation("FRACTION ( _numer , _denom )"))) {
                    MathInteger numer = (MathInteger) matcher.getLastMatchExpressions().get("numer").data;
                    MathInteger denom = (MathInteger) matcher.getLastMatchExpressions().get("denom").data;
                    if(denom.equals(new MathInteger(0))){
                        throw new ArithmeticException();
                    }
                    BigInteger gcd = numer.num.gcd(denom.num);
                    BigInteger newNumer = numer.num.divide(gcd);
                    BigInteger newDenom = denom.num.divide(gcd);
                    return new Equation("FRACTION ( " + newNumer + " , " + newDenom + " )");
                }
                return eq; //CHANGE THIS
            }),(new MathObject(MathSymbol.FRACTION)))
    };
    public static final HashSet<EquationSub> subs = new HashSet<EquationSub>(Arrays.asList(subsArray));
}
