package Database;

import CAS.*;
import CAS.EquationObjects.MathInteger;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static CAS.EquationBuilder.makeUnprocessedEquation;
import static CAS.Simplifier.simplifyByOperator;


/**
 * Created by jack on 1/8/2017.
 */
public class EquationSubDatabase { //NOTE: I know, I know, this should be in the actual SQLite database. I WILL do that, but I want to keep these here in case something goes wrong or I want to reference them directly. Once every sub is in the database I can remove this, but UNTIL THEN, it's a good idea to keep these here just in case.
    private static final EquationSub[] subsArray = {
            new StructuralSub(makeUnprocessedEquation("MINUS ( _v1 , _v2 )"), makeUnprocessedEquation("PLUS ( _v1 , TIMES ( -1 , _v2 ) )")),
            new StructuralSub(makeUnprocessedEquation("DIVIDE( _v1 , _v2 )"), makeUnprocessedEquation("FRACTION ( _v1 , _v2 )"),makeUnprocessedEquation("AND ( EQUALS ( TYPEOF ( _v1 ) , NUMBER ) , EQUALS ( TYPEOF ( _v2 ) , NUMBER ) , NOT ( EQUALS ( _v2 , 0 ) ) )")),
            new StructuralSub(makeUnprocessedEquation("DIVIDE ( _v1 , _v2 )"), makeUnprocessedEquation("TIMES ( _v1 , FRACTION ( 1 , _v2 ) )"), makeUnprocessedEquation("AND ( EQUALS ( TYPEOF ( _v1 ) , EXPRESSION ) , EQUALS ( TYPEOF ( _v2 ) , NUMBER ) )")),
            new StructuralSub(makeUnprocessedEquation("DIVIDE ( _v1 , _v2 )"), makeUnprocessedEquation("TIMES ( _v1 , POWER ( _v2 , -1 ) )")),
            new StructuralSub(new Equation("PATTERN_OR ( OR ( TRUE , FALSE ) , OR ( FALSE , TRUE ) , OR ( TRUE , TRUE ) )"), new Equation("TRUE")),
            new StructuralSub(new Equation("OR ( FALSE , FALSE )"), new Equation("FALSE")),
            new StructuralSub(new Equation("AND ( TRUE , TRUE )"), new Equation("TRUE")),
            new StructuralSub(new Equation("PATTERN_OR ( AND ( TRUE , FALSE ) , AND ( FALSE , TRUE ) , AND ( FALSE , FALSE ) )"), new Equation("FALSE")),
            new StructuralSub(new Equation("EQUALS ( _v1 , _v1 )"), new Equation("TRUE")),
            new StructuralSub(new Equation("EQUALS ( _v1 , _v2 )"), new Equation("FALSE")),
            new StructuralSub(new Equation("NOT ( FALSE )"), new Equation("TRUE")),
            new StructuralSub(new Equation("NOT ( TRUE )"), new Equation("FALSE")),
            new StructuralSub(new Equation("TIMES( FRACTION (_v1, _v2), FRACTION(_v3, _v4))"), new Equation("FRACTION(TIMES(_v1, _v3),TIMES(_v2,_v4))")),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                return new Equation(new Tree<MathObject>(new MathInteger(eq.tree.getNumberOfChildren())));
            }), new MathObject(MathOperator.NUMBER_OF_OPERANDS)),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                Tree<MathObject> toEval = eq.tree.getChild(0);
                int index = ((MathInteger) eq.tree.getChild(1).data).num.intValue();
                return new Equation(toEval.getChild(index));
            }), new MathObject(MathOperator.OPERAND)),
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
            }),(new MathObject(MathOperator.TYPEOF))),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                if (eq.tree.data.equals(new MathObject(MathOperator.ADD)) && eq.tree.getNumberOfChildren() == 2) {
                    if (eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger) {
                        return new Equation(((MathInteger) eq.tree.getChild(0).data).add((MathInteger) eq.tree.getChild(1).data).toString());
                    }
                }
                return eq; //No change
            }), (new MathObject(MathOperator.ADD))),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                if (eq.tree.data.equals(new MathObject(MathOperator.MULTIPLY)) && eq.tree.getNumberOfChildren() == 2) {
                    if (eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger) {
                        return new Equation(((MathInteger) eq.tree.getChild(0).data).mul((MathInteger) eq.tree.getChild(1).data).toString());
                    }
                }
                return eq; //No change
            }), (new MathObject(MathOperator.MULTIPLY))),
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
            }),(new MathObject(MathOperator.FRACTION))),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                Equation newEq = eq.clone();
                if(eq.getRoot().equals(new MathObject(MathOperator.SIMPLIFY_RATIONAL_FRACTION))){
                    newEq = eq.getSubEquation(0).clone();
                }
                if(newEq.getRoot().getOperator() == MathOperator.FRACTION && new Equation(newEq.tree.getChild(0)).isType(SimplificationType.INTEGER) && new Equation(newEq.tree.getChild(1)).isType(SimplificationType.INTEGER)){
                    MathInteger numer = (MathInteger) newEq.getSubEquation(0).getRoot();
                    MathInteger denom = (MathInteger) newEq.getSubEquation(1).getRoot();
                    MathInteger gcd = new MathInteger(((MathInteger) newEq.tree.getChild(0).data).num.gcd(((MathInteger) newEq.tree.getChild(1).data).num));
                    return new Equation("FRACTION(" + numer.div(gcd) + "," + denom.div(gcd) + ")");
                }
                else{
                    return newEq;
                }
            }), new MathObject(MathOperator.SIMPLIFY_RATIONAL_FRACTION)),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                if (eq.isType(SimplificationType.RATIONAL_NUMBER_EXPRESSION)) {
                    return eq;
                }
                Equation newEq = eq.clone();
                if(eq.getRoot().equals(new MathObject(MathOperator.SIMPLIFY_RATIONAL_EXPRESSION))){
                    newEq = eq.getSubEquation(0).clone();
                }
                if(newEq.isType(SimplificationType.INTEGER)){
                    return newEq;
                }
                else if(newEq.isType(SimplificationType.FRACTION_STANDARD_FORM)){
                    if(new Equation("OPERAND(" + newEq + ",2").isUndefined()){
                        return new Equation("UNDEFINED");
                    }
                    else return newEq;
                }
                else if(newEq.isUndefined()){
                    return new Equation("UNDEFINED");
                }
                else if(newEq.getRoot().equals(new MathObject(MathOperator.POWER))){
                    Equation newBase = new Equation("SIMPLIFY_RATIONAL_EXPRESSION(OPERAND(" + newEq + ", 1))");
                    Equation power = newEq.getSubEquation(1);
                    if(newBase.isUndefined()){
                        return new Equation("UNDEFINED");
                    }
                    return simplifyByOperator(new Equation("POWER(" + newBase + ", " + power + ")"), new MathObject(MathOperator.POWER));
                }
                else if(newEq.tree.getNumberOfChildren() >= 2){
                    if(newEq.getSubEquation(0).isUndefined() || newEq.getSubEquation(1).isUndefined()){
                        return new Equation("UNDEFINED");
                    }
                    for(Tree<MathObject> child : newEq.tree.getChildren()){
                        Equation replace = new Equation("SIMPLIFY_RATIONAL_EXPRESSION(" + new Equation(child) + ")");
                        child.replaceWith(replace.tree);
                    }
                    return Simplifier.simplifyByOperator(newEq);
                }
                else{
                    return newEq;
                }
            }), new MathObject(MathOperator.SIMPLIFY_RATIONAL_EXPRESSION))
    };
    public static final HashSet<EquationSub> subs = new HashSet<EquationSub>(Arrays.asList(subsArray));
}
