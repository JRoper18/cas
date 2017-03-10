package Database;

import CAS.*;
import CAS.EquationObjects.MathInteger;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import CAS.EquationObjects.MathOperatorSubtype;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

import static CAS.EquationBuilder.makeUnprocessedEquation;
import static CAS.Simplifier.simplifyByOperator;


/**
 * Created by jack on 1/8/2017.
 */
public class EquationSubDatabase { //NOTE: I know, I know, this should be in the actual SQLite database. I WILL do that, but I want to keep these here in case something goes wrong or I want to reference them directly. Once every sub is in the database I can remove this, but UNTIL THEN, it's a good idea to keep these here, in memory just in case.
    private static final EquationSub[] subsArray = {

            new EquationSub((Serializable & DirectOperation) (eq -> {
                BigInteger gcd = ((MathInteger) Simplifier.simplifyMetaFunctions(eq.getSubEquation(0)).getRoot()).num.gcd(((MathInteger) Simplifier.simplifyMetaFunctions(eq.getSubEquation(1)).getRoot()).num);
                if(eq.getOperands().size() > 1){
                    for(int i = 2; i<eq.getOperands().size(); i++){
                        Equation currentEq = eq.getSubEquation(i);
                        gcd = ((MathInteger) currentEq.getRoot()).num.gcd(gcd);
                    }
                }
                return new Equation(new Tree<>(new MathInteger(gcd)));
            }), new MathObject(MathOperator.GREATEST_COMMON_DENOMINATOR)),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                return new Equation(new Tree<MathObject>(new MathInteger(eq.getSubEquation(0).tree.getNumberOfChildren())));
            }), new MathObject(MathOperator.NUMBER_OF_OPERANDS)),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                Tree<MathObject> toEval = eq.tree.getChild(0);
                int index = ((MathInteger) eq.tree.getChild(1).data).num.intValue();
                return new Equation(toEval.getChild(index));
            }), new MathObject(MathOperator.OPERAND)),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                PatternMatcher matcher = new PatternMatcher();
                if (matcher.patternMatch(eq, EquationBuilder.makeUnprocessedEquation("TYPEOF ( _v1 )"))) {
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
                    Equation arg1 = eq.getSubEquation(0);
                    Equation arg2 = eq.getSubEquation(1);
                    if(arg1.isType(SimplificationType.INTEGER)){
                        arg1 = new Equation("FRACTION(" + arg1 + ", 1)", 0);
                    }
                    if(arg2.isType(SimplificationType.INTEGER)){
                        arg2 = new Equation("FRACTION(" + arg2 + ", 1)", 0);
                    }
                    if(arg1.isType(MathOperator.FRACTION) && arg2.isType(MathOperator.FRACTION)){
                        //FInd any common denominator of both fractions. Don't worry, we'll simplify it later.
                        MathInteger newDenom = ((MathInteger) arg1.getSubEquation(1).getRoot()).mul((MathInteger) arg2.getSubEquation(1).getRoot());
                        MathInteger arg1Scale = newDenom.div((MathInteger) arg1.getSubEquation(1).getRoot());
                        MathInteger arg2Scale = newDenom.div((MathInteger) arg2.getSubEquation(1).getRoot());
                        MathInteger newNumer = ((MathInteger) arg1.getSubEquation(0).getRoot()).mul(arg1Scale).add(((MathInteger) arg2.getSubEquation(0).getRoot()).mul(arg2Scale));
                        return new Equation("SIMPLIFY_RATIONAL_FRACTION(FRACTION(" + newNumer + "," + newDenom + "))", 1);
                    }
                }
                return eq; //No change
            }), (new MathObject(MathOperator.ADD))),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                if (eq.tree.data.equals(new MathObject(MathOperator.MULTIPLY)) && eq.tree.getNumberOfChildren() == 2) {
                    if (eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger) {
                        BigInteger num1 = ((MathInteger) eq.tree.getChild(0).data).num;
                        BigInteger num2 = ((MathInteger) eq.tree.getChild(1).data).num;
                        return new Equation(num1.multiply(num2).toString());
                    }
                    Equation arg1 = eq.getSubEquation(0);
                    Equation arg2 = eq.getSubEquation(1);
                    if(arg1.isType(SimplificationType.INTEGER)){
                        arg1 = new Equation("FRACTION(" + arg1 + ", 1)");
                    }
                    if(arg2.isType(SimplificationType.INTEGER)){
                        arg2 = new Equation("FRACTION(" + arg2 + ", 1)");
                    }
                    if(arg1.isType(MathOperator.FRACTION) && arg2.isType(MathOperator.FRACTION)){
                        MathInteger newNumer = ((MathInteger) arg1.getSubEquation(0).getRoot()).mul((MathInteger) arg2.getSubEquation(0).getRoot());
                        MathInteger newDenom = ((MathInteger) arg1.getSubEquation(1).getRoot()).mul((MathInteger) arg2.getSubEquation(1).getRoot());
                        return new Equation("SIMPLIFY_RATIONAL_FRACTION(FRACTION(" + newNumer + "," + newDenom + "))", 1);
                    }
                }
                return eq; //No change
            }), (new MathObject(MathOperator.MULTIPLY))),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                if (eq.tree.data.equals(new MathObject(MathOperator.SUBTRACT)) && eq.tree.getNumberOfChildren() == 2) {
                    if (eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger) {
                        return new Equation(((MathInteger) eq.tree.getChild(0).data).sub((MathInteger) eq.tree.getChild(1).data).toString());
                    }
                }
                return eq; //No change
            }), (new MathObject(MathOperator.SUBTRACT))),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                if (eq.tree.data.equals(new MathObject(MathOperator.DIVIDE)) && eq.tree.getNumberOfChildren() == 2) {
                    if(eq.getSubEquation(1).equals(new Equation("0"))){
                        return new Equation("UNDEFINED");
                    }
                    if (eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger) {
                        return new Equation("SIMPLIFY_RATIONAL_FRACTION(FRACTION(" + eq.getSubEquation(0) + "," + eq.getSubEquation(1) + "))");
                    }
                }
                return eq; //No change
            }), (new MathObject(MathOperator.DIVIDE))),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                PatternMatcher matcher = new PatternMatcher();
                if(matcher.patternMatch(eq, new Equation("FRACTION ( _numer , _denom )", 1))) {
                    MathInteger numer = (MathInteger) matcher.getLastMatchExpressions().get("numer").data;
                    MathInteger denom = (MathInteger) matcher.getLastMatchExpressions().get("denom").data;
                    if(denom.equals(new MathInteger(0))){
                        throw new ArithmeticException();
                    }
                    BigInteger gcd = numer.num.gcd(denom.num);
                    BigInteger newNumer = numer.num.divide(gcd);
                    BigInteger newDenom = denom.num.divide(gcd);
                    return new Equation("FRACTION ( " + newNumer + " , " + newDenom + " )", 1);
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
                    if(denom.num.signum() == -1){ //Either: they're both divisible by negative 1, so just get rid of that, or the denominator only is negative, so we make the numerator negative (for consistency)
                        numer = new MathInteger(numer.num.negate());
                        denom = new MathInteger(denom.num.negate());
                    }
                    MathInteger gcd = new MathInteger(((MathInteger) newEq.tree.getChild(0).data).num.gcd(((MathInteger) newEq.tree.getChild(1).data).num));
                    MathInteger newDenom = denom.div(gcd);
                    if(newDenom.num.compareTo(new BigInteger("1")) == 0){
                        return new Equation(numer.div(gcd).num.toString());
                    }
                    if(newDenom.num.signum() == 0){ //If it's 0
                        return new Equation("UNDEFINED");
                    }
                    return new Equation("FRACTION(" + numer.div(gcd) + "," + newDenom + ")", 1);
                }
                else{
                    return newEq;
                }
            }), new MathObject(MathOperator.SIMPLIFY_RATIONAL_FRACTION)),
            new EquationSub((Serializable & DirectOperation) (eq -> {
                if(eq.isType(MathOperator.POWER)){
                    for(Equation child: eq.getOperands()){
                        if(!child.isType(SimplificationType.INTEGER)){
                            return eq; //DO this later
                        }
                    }
                    BigInteger base = ((MathInteger) eq.getSubEquation(0).getRoot()).num;
                    BigInteger expo = ((MathInteger) eq.getSubEquation(1).getRoot()).num;
                    if(expo.intValue() < 0){
                        return new Equation("FRACTION(1," + base.pow(-1 * expo.intValueExact()) + ")");
                    }
                    return new Equation(base.pow(expo.intValueExact()).toString(), 0);
                }
                return eq;
            }), new MathObject(MathOperator.POWER)),
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
                    if(new Equation("OPERAND(" + newEq + ",1)", 1).equals("0")){

                        return new Equation("UNDEFINED");
                    }
                    else return newEq;
                }
                else if(newEq.isUndefined()){
                    return new Equation("UNDEFINED");
                }
                else if(newEq.getRoot().equals(new MathObject(MathOperator.POWER))){
                    Equation newBase = new Equation("SIMPLIFY_RATIONAL_EXPRESSION(OPERAND(" + newEq + ", 0))", 1);
                    Equation power = newEq.getSubEquation(1);
                    if(newBase.isUndefined()){
                        return new Equation("UNDEFINED");
                    }
                    Equation toReturn = simplifyByOperator(new Equation("POWER(" + newBase + ", " + power + ")", 0));
                    return toReturn;
                }
                else if(newEq.tree.getNumberOfChildren() >= 2){
                    if(newEq.getOperands().contains(new Equation("UNDEFINED"))){
                        return new Equation("UNDEFINED");
                    }
                    for(Tree<MathObject> child : newEq.tree.getChildren()){
                        Equation replace = new Equation("SIMPLIFY_RATIONAL_EXPRESSION(" + new Equation(child) + ")", 1);
                        child.replaceWith(replace.tree);
                    }
                    Equation operatorSimplified =  Simplifier.simplifyByOperator(newEq);
                    return Simplifier.simplifyWithMetaFunction(operatorSimplified, MathOperator.SIMPLIFY_RATIONAL_FRACTION);
                }
                else{
                    return newEq;
                }
            }), new MathObject(MathOperator.SIMPLIFY_RATIONAL_EXPRESSION)),
            new EquationSub((DirectOperation & Serializable) (eq -> {
                if(eq.getRoot().getOperator() == MathOperator.BASE){
                    Equation toEval = eq.getSubEquation(0);
                    MathOperator op = toEval.getRoot().getOperator();
                    if(op == MathOperator.POWER){
                        return toEval.getSubEquation(0);
                    }
                    return toEval; //Assume exponent 1
                }
                return eq;
            }), new MathObject(MathOperator.BASE)),
            new EquationSub((DirectOperation & Serializable) (eq -> {
                if(eq.getRoot().getOperator() == MathOperator.EXPONENT){
                    Equation toEval = eq.getSubEquation(0);
                    MathOperator op = toEval.getRoot().getOperator();
                    if(op == MathOperator.POWER){
                        return toEval.getSubEquation(1);
                    }
                    return new Equation("1"); //Assume exponent 1, base expression
                }
                return eq;
            }), new MathObject(MathOperator.EXPONENT)),
            new EquationSub((DirectOperation & Serializable) (eq -> {
                Equation newEq = eq.getSubEquation(0); //Autosimplify is the root term
                EquationBuilder.setLevel(1);
                EquationSub sub = new EquationSub((DirectOperation) (equation -> {
                    if(equation.isType(MathOperator.DIVIDE)){
                        return new Equation("MULTIPLY(" + equation.getSubEquation(0) + "," + "POWER(" + equation.getSubEquation(1) + ", -1))");
                    }
                    if(equation.isType(MathOperator.SUBTRACT)){
                        return new Equation("PLUS(" + equation.getSubEquation(0) + "," + "MULTIPLY(" + equation.getSubEquation(1) + ", -1))");
                    }
                    return equation;
                }));
                newEq = sub.applyEverywhere(newEq);
                if(newEq.tree.containsData(new MathObject(MathOperator.UNDEFINED))){
                    return new Equation("UNDEFINED");
                }
                if(newEq.isType(SimplificationType.INTEGER) || newEq.isType(MathOperatorSubtype.SYMBOL)){
                    return newEq;
                }
                else if(newEq.isType(MathOperator.FRACTION)){
                    return new Equation("SIMPLIFY_RATIONAL_FRACTION(" + newEq + ")");
                }
                else if(newEq.isType(SimplificationType.RATIONAL_NUMBER_EXPRESSION)){
                    return new Equation("SIMPLIFY_RATIONAL_EXPRESSION(" + newEq + ")");
                }
                else{
                    for(Tree<MathObject> child : newEq.tree.getChildren()){ //Recursivly get autosimplified expression.
                        child.replaceWith(new Equation(child, 1).tree);
                    }
                    Equation toReturn;
                    switch(newEq.getRoot().getOperator()){
                        case POWER:
                            toReturn = new Equation("SIMPLIFY_POWER(" + newEq + ")");
                            break;
                        case ADD:
                            toReturn = new Equation("SIMPLIFY_SUM(" + newEq + ")");
                            break;
                        case MULTIPLY:
                            toReturn = new Equation("SIMPLIFY_PRODUCT(" + newEq + ")");
                            break;
                        case FACTORIAL:
                            toReturn = new Equation("SIMPLIFY_FACTORIAL(" + newEq + ")");
                            break;
                        default:
                            toReturn = newEq;
                    }
                    return toReturn;
                }
            }), new MathObject(MathOperator.AUTOSIMPLIFY)),
            new EquationSub((DirectOperation & Serializable) eq -> {
                Equation product = eq.getSubEquation(0);

                return product; //DEFAULT
            }, new MathObject(MathOperator.SIMPLIFY_PRODUCT)),
            new EquationSub((DirectOperation & Serializable) eq -> {
                if(eq.isType(MathOperator.ADJOIN)){
                    Equation list = eq.getSubEquation(1);
                    List<Equation> newList = list.toList();
                    newList.add(0, eq.getSubEquation(0));
                    return Equation.fromList(newList);
                }
                return eq;
            }, new MathObject(MathOperator.ADJOIN)),
            new EquationSub((DirectOperation & Serializable) eq -> {
                if(eq.isType(MathOperator.REST)){
                    List<Equation> list = eq.getSubEquation(0).toList();
                    list.remove(0);
                    return Equation.fromList(list);
                }
                return eq;
            }, new MathObject(MathOperator.REST))
    };
    public static final HashSet<EquationSub> subs = new HashSet<EquationSub>(Arrays.asList(subsArray));
}
