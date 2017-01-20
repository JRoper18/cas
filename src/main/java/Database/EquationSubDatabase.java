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
                    if(new Equation("OPERAND(" + newEq + ",2").equals("0")){
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
                    if(newEq.getOperands().contains(new Equation("UNDEFINED"))){
                        return new Equation("UNDEFINED");
                    }
                    for(Tree<MathObject> child : newEq.tree.getChildren()){
                        Equation replace = new Equation("SIMPLIFY_RATIONAL_EXPRESSION(" + new Equation(child) + ")");
                        child.replaceWith(replace.tree);
                    }
                    return Simplifier.simplifyByOperator(newEq, newEq.getRoot());
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
                    else if(toEval.isType(SimplificationType.INTEGER) || toEval.isType(SimplificationType.FRACTION_STANDARD_FORM)){
                        return new Equation("UNDEFINED");
                    }
                    else{
                        return toEval; //Assume exponent 1
                    }
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
                    else if(toEval.isType(SimplificationType.INTEGER) || toEval.isType(SimplificationType.FRACTION_STANDARD_FORM)){
                        return new Equation("UNDEFINED");
                    }
                    else{
                        return new Equation("1"); //Assume exponent 1, base expression
                    }
                }
                return eq;
            }), new MathObject(MathOperator.EXPONENT)),
            new EquationSub((DirectOperation & Serializable) (eq -> {
              if(eq.getRoot().getOperator() == MathOperator.TERM){
                  Equation sub = eq.getSubEquation(0).clone();
                  MathOperator op = sub.getRoot().getOperator();
                  if(op.getSubType() == MathOperatorSubtype.SYMBOL || op == MathOperator.POWER || op == MathOperator.FACTORIAL || op == MathOperator.CUSTOM_FUNCTION){
                      return sub;
                  }
                  if(op == MathOperator.MULTIPLY){
                      if(sub.getSubEquation(0).isType(SimplificationType.INTEGER) || sub.getSubEquation(0).isType(SimplificationType.FRACTION_STANDARD_FORM)){
                          Equation temp = sub.clone();
                          temp.tree.removeChild(0);
                          return temp;
                      }
                      return sub;
                  }
                  return new Equation("UNDEFINED");
              }
              return eq;
            }), new MathObject(MathOperator.TERM)),
            new EquationSub((DirectOperation & Serializable) (eq -> {
                Equation newEq = eq.clone();
                if(newEq.tree.containsData(new MathObject(MathOperator.UNDEFINED))){
                    return new Equation("UNDEFINED");
                }
                if(newEq.isType(SimplificationType.INTEGER) || newEq.isType(MathOperatorSubtype.SYMBOL)){
                    return newEq;
                }
                else if(newEq.isType(MathOperator.FRACTION)){
                    return new Equation("SIMPLIFY_RATIONAL_FRACTION(" + newEq + ")");
                }
                else{
                    for(Tree<MathObject> child : newEq.tree.getChildren()){ //Recursivly get autosimplified expression.
                        child.replaceWith(new Equation("AUTOSIMPLIFY(" + child + ")").tree);
                    }
                    switch(newEq.getRoot().getOperator()){
                        case POWER:
                            return new Equation("SIMPLIFY_POWER(" + newEq + ")");
                        case ADD:
                            return new Equation("SIMPLIFY_SUM(" + newEq + ")");
                        case MULTIPLY:
                            return new Equation("SIMPLIFY_PRODUCT(" + newEq + ")");
                        case SUBTRACT:
                            return new Equation("SIMPLIFY_SUBTRACTION(" + newEq + ")");
                        case DIVIDE:
                            return new Equation("SIMPLIFY_DIVISION(" + newEq + ")");
                        case FACTORIAL:
                            return new Equation("SIMPLIFY_FACTORIAL(" + newEq + ")");
                        default:
                            return newEq;
                    }
                }
            }), new MathObject(MathOperator.AUTOSIMPLIFY)),
            new EquationSub((DirectOperation & Serializable) eq -> {
                Equation base = new Equation("BASE(" + eq + ")");
                Equation exponent = new Equation("EXPONENT(" + eq + ")");
                if(base.isType(MathOperator.UNDEFINED) || exponent.isType(MathOperator.UNDEFINED)){
                    return new Equation("UNDEFINED");
                }
                if(base.equals(new Equation("0"))){
                    boolean isPositive = false; //Can't have 0^-n, because that is 1/0. Check if it's positive
                    if(exponent.isType(SimplificationType.INTEGER)){
                        isPositive = (((MathInteger) exponent.getRoot()).num.signum() < 0);
                    }
                    else if(exponent.isType(MathOperator.FRACTION)){
                        isPositive = (((MathInteger) exponent.getSubEquation(0).getRoot()).num.signum() / (((MathInteger) exponent.getSubEquation(0).getRoot()).num.signum()) < 0);
                    }
                    if(!isPositive){
                        return new Equation("UNDEFINED");
                    }
                    return new Equation("0");
                }
                if(base.equals(new Equation("1"))){ //Duh. 1^anything = 1
                    return base;
                }
                if(exponent.isType(SimplificationType.INTEGER)){
                    return new Equation("SIMPLIFY_POWER_INT(" + eq + ")"); //Check for special rules for integer exponents
                }
                return eq; //Last resort;
            }, new MathObject(MathOperator.SIMPLIFY_POWER)),
            new EquationSub((DirectOperation & Serializable) eq -> {
                Equation exponentInt = eq.getSubEquation(1);
                Equation base = eq.getSubEquation(0);
                if(exponentInt.isType(SimplificationType.INTEGER) || exponentInt.isType(MathOperator.FRACTION)){ //Just concrete, constant numbers.
                    return new Equation("SIMPLIFY_RATIONAL_EXPRESSION(" + eq + ")");
                }
                if(exponentInt.equals(new Equation("0"))){ //Anything to the power of 0 is 1
                    return new Equation("1");
                }
                if(exponentInt.equals(new Equation("1"))){ //Anything to the power of 1 is itself
                    return base;
                }
                if(base.isType(MathOperator.POWER)){
                    Equation sub = new Equation("SIMPLIFY_PRODUCT(TIMES(" + base.getSubEquation(0) + "," + base.getSubEquation(1) + "))");
                    if(sub.isType(SimplificationType.INTEGER)){
                        return new Equation("SIMPLIFY_POWER_INT(POWER(" + base.getSubEquation(0) + "," + sub + "))");
                    }
                    else {
                        return new Equation("POWER(" + base.getSubEquation(0) + "," + sub + "))");
                    }
                }
                if(base.isType(MathOperator.MULTIPLY)){
                    Tree<MathObject> construct = new Tree<>();
                    for(int i = 0; i<base.getOperands().size(); i++){ //If we have a lot of thing multiplied by each other and then to a power, just apply the power seperately.
                        //AKA: (a * b * c)^2 => a^2 * b^2 * c^2
                        Equation operand = base.getSubEquation(i);
                        construct.addChild(new Equation("SIMPLIFY_POWER_INT(POWER(" + operand + "," + exponentInt + "))").tree);
                    }
                    return new Equation("SIMPLIFY_PRODUCT(" + new Equation(construct) + ")");
                }
                return eq; //Give up
            }, new MathObject(MathOperator.SIMPLIFY_POWER_INT)),
            new EquationSub((DirectOperation & Serializable) eq -> {
                List<Equation> operands = eq.getOperands();

                if(operands.contains(new Equation("UNDEFINED"))){
                    return new Equation("UNDEFINED");
                }
                if(operands.contains(new Equation("0"))){
                    return new Equation("0");
                }
                if(operands.size() == 1){
                    return operands.get(0);
                }
                else{
                    List<String> operandStr = new ArrayList<>();
                    for(Equation operand : operands){
                        operandStr.add(operand.toString());
                    }
                    Equation sub = new Equation("SIMPLIFY_PRODUCT_RECURSIVE(LIST(" + String.join(",", operandStr) + "))");
                }
                return eq;
            }, new MathObject(MathOperator.SIMPLIFY_PRODUCT)),
            new EquationSub((DirectOperation & Serializable) eq -> {
                List<Equation> operands;
                if(eq.isType(MathOperator.LIST)){
                    operands = eq.getOperands();
                }
                else{
                    return eq;
                }
                if(operands.size() == 2 && !operands.get(0).isType(MathOperator.MULTIPLY) && !operands.get(1).isType(MathOperator.MULTIPLY)){
                    Equation productSimp = new Equation("SIMPLIFY_RATIONAL_EXPRESSION(MULTIPLY(" + operands.get(0) + "," + operands.get(1) + "))");
                    if(operands.get(0).isType(SimplificationType.CONSTANT) && operands.get(1).isType(SimplificationType.CONSTANT)){
                        if(productSimp.equals("1")){
                            return new Equation("LIST()");
                        }
                        return new Equation("LIST(" + productSimp + ")");
                    }
                    if(operands.get(0).equals(new Equation("1"))){
                        return operands.get(1);
                    }
                    if(operands.get(1).equals(new Equation("1"))){
                        return operands.get(0);
                    }
                    if(new Equation("BASE(" + operands.get(1) + ")").equals(new Equation("BASE(" + operands.get(0) + ")"))){
                        Equation sum = new Equation("SIMPLIFY_SUM(ADD(" + new Equation("EXPONENT(" + operands.get(1)) + ", " + new Equation("EXPONENT(" + operands.get(0) + ")") + "))");
                        Equation power = new Equation("SIMPLIFY_POWER(POWER(" + new Equation("BASE(" + operands.get(1) + ")") + "," + sum + "))");
                        if(power.equals(new Equation("1"))){
                            return new Equation("LIST()");
                        }
                        return new Equation("LIST(" + power + ")");
                    }
                    if(operands.get(1).compareTo(operands.get(0)) < 0){ //If the order is wrong, switch it
                        return new Equation("SIMPLIFY_PRODUCT_RECURSIVE(" + operands.get(1) + "," + operands.get(0) + ")");
                    }
                }
                if(operands.size() == 2 && (operands.get(0).isType(MathOperator.MULTIPLY) || operands.get(1).isType(MathOperator.MULTIPLY))) {
                    if(operands.get(0).isType(MathOperator.MULTIPLY) && operands.get(1).isType(MathOperator.MULTIPLY)){
                        return new Equation("TIMES(" + )
                    }
                }
                return eq;//DEFAULT
            }, new MathObject(MathOperator.SIMPLIFY_PRODUCT_RECURSIVE))
    };
    public static final HashSet<EquationSub> subs = new HashSet<EquationSub>(Arrays.asList(subsArray));
}
