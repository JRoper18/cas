package Database;

import CAS.*;
import CAS.EquationObjects.*;
import org.javatuples.Pair;
import org.javatuples.Tuple;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
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
                        arg1 = new Equation("FRACTION(" + arg1 + ", 1)", 1);
                    }
                    if(arg2.isType(SimplificationType.INTEGER)){
                        arg2 = new Equation("FRACTION(" + arg2 + ", 1)", 1);
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
                        return new Equation("UNDEFINED", 0);
                    }
                    if (eq.tree.getChild(0).data instanceof MathInteger && eq.tree.getChild(1).data instanceof MathInteger) {
                        return new Equation("SIMPLIFY_RATIONAL_FRACTION(FRACTION(" + eq.getSubEquation(0) + "," + eq.getSubEquation(1) + "))", 1);
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
                        return new Equation("UNDEFINED", 0);
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
                        return new Equation("FRACTION(1," + base.pow(-1 * expo.intValueExact()) + ")", 0);
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
                Equation newEq = eq.getSubEquation(0).clone(); //Autosimplify is the root term
                EquationSub sub = new EquationSub((DirectOperation) (equation -> {
                    if(equation.isType(MathOperator.DIVIDE)){
                        return new Equation("MULTIPLY(" + equation.getSubEquation(0) + "," + "POWER(" + equation.getSubEquation(1) + ", -1))", 0);
                    }
                    if(equation.isType(MathOperator.SUBTRACT)){
                        return new Equation("PLUS(" + equation.getSubEquation(0) + "," + "MULTIPLY(" + equation.getSubEquation(1) + ", -1))", 0);
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
                if(newEq.isType(MathOperator.FRACTION)){
                    return new Equation("SIMPLIFY_RATIONAL_FRACTION(" + newEq + ")", 1);
                }
                if(newEq.isType(SimplificationType.RATIONAL_NUMBER_EXPRESSION)){
                    return new Equation("SIMPLIFY_RATIONAL_EXPRESSION(" + newEq + ")", 1);
                }
                else{
                    for(Tree<MathObject> child : newEq.tree.getChildren()){ //Recursivly get autosimplified expression.
                        child.replaceWith(Simplifier.simplifyWithMetaFunction(new Equation(child, 0), MathOperator.AUTOSIMPLIFY).tree);
                    }
                    Equation toReturn;
                    switch(newEq.getRoot().getOperator()){
                        case POWER:
                            //toReturn = new Equation("SIMPLIFY_POWER(" + newEq + ")", 1); Get this later
                            toReturn = newEq;
                            break;
                        case ADD:
                            toReturn = new Equation("SIMPLIFY_SUM(" + newEq + ")", 1);
                            break;
                        case MULTIPLY:
                            toReturn = new Equation("SIMPLIFY_PRODUCT(" + newEq + ")", 1);
                            break;
                        case FACTORIAL:
                            toReturn = new Equation("SIMPLIFY_FACTORIAL(" + newEq + ")", 1);
                            break;
                        default:
                            toReturn = newEq;
                    }
                    return toReturn;
                }
            }), new MathObject(MathOperator.AUTOSIMPLIFY)),
            new EquationSub((DirectOperation & Serializable) eq -> {
                Equation sum = eq.getSubEquation(0).clone();
                List<Equation> operands = sum.getOperands();
                HashMap<String, String> terms = new HashMap<>(); //term equation in string form, coefficient equation (also string form)
                List<Equation> constantTerms = new ArrayList<>();
                for(int i = 0; i<operands.size(); i++){
                    Equation operand = operands.get(i);
                    Equation coeffEq = new Equation("1");
                    String term = "";
                    if(operand.isType(SimplificationType.CONSTANT)){
                        constantTerms.add(operand);
                        continue;
                    }
                    if(operand.isType(MathOperator.MULTIPLY)){ //The constant is always the first term
                        coeffEq = new Equation("COEFFICIENT(" + operand + ")",1);
                        term = new Equation("TERM(" + operand + ")",1).toString();
                        //Now we know our coefficient, power, and varname. Add it into the hashmap.
                    }
                    else if(operand.isType(MathOperator.ADD)){
                        operands.addAll(i+1, operand.getOperands());
                        continue;
                    }
                    else{
                        term = operand.toString();
                    }
                    String coeff = coeffEq.toString();
                    if(terms.containsKey(term)){
                        terms.put(term, "ADD(" + terms.get(term) + "," + coeff + ")");
                    }
                    else{
                        terms.put(term, coeff);
                    }
                }
                //Now turn the terms and constant terms into an equation.
                Equation constant;
                if(constantTerms.size() == 0){
                    constant = new Equation("0");
                }
                else if(constantTerms.size() == 1){
                    constant = constantTerms.get(0);
                }
                else{
                      constant = Simplifier.simplifyWithMetaFunction(Equation.fromList(constantTerms, MathOperator.ADD), MathOperator.SIMPLIFY_RATIONAL_EXPRESSION);
                }
                List<Equation> newTerms = new ArrayList<>();
                if(!constant.equals(new Equation("0"))){
                    newTerms.add(constant);
                }
                for(String key : terms.keySet()){
                    Equation termEq = new Equation(key, 0);
                    Equation power = new Equation("EXPONENT(" + key + ")", 1);
                    Equation coefficient = new Equation(terms.get(key), 2);
                    if(coefficient.equals(new Equation("0", 0))){
                        //Do nothing
                    }
                    else if(coefficient.equals(new Equation("0", 0)) && power.equals(new Equation("1", 0))){
                        newTerms.add(termEq);
                    }
                    else if(coefficient.equals(new Equation("1", 0))){
                        newTerms.add(termEq);
                    }
                    else if(power.equals(new Equation("1"))){
                        newTerms.add(new Equation("TIMES(" + coefficient + ", " + key + ")", 0));
                    }
                    else{
                        newTerms.add(new Equation("TIMES(" + coefficient + "," + key + ")", 0));
                    }
                }

                if(newTerms.size() > 1){
                    return Equation.fromList(newTerms, MathOperator.ADD);
                }
                else if(newTerms.size() == 0){
                    return new Equation("0", 0);
                }
                else{
                    return newTerms.get(0);
                }
            }, new MathObject(MathOperator.SIMPLIFY_SUM)),
            new EquationSub((DirectOperation & Serializable) eq -> {
                Equation newEq = eq.getSubEquation(0);
                //Now we're collecting like powers
                if(!newEq.isType(MathOperator.MULTIPLY)){
                    return newEq;
                }
                List<Equation> operands = newEq.getOperands();
                List<Equation> constantList = new ArrayList<>();
                constantList.add(new Equation("1",0));
                HashMap<String, String> powers = new HashMap<>(); //Base, exponent
                for(int i = 0; i<operands.size(); i++){
                    Equation operand = operands.get(i);
                    if(operand.isType(SimplificationType.CONSTANT)){
                        if(operand.equals(new Equation("0"))){
                            return new Equation("0");
                        }
                        constantList.add(operand);
                        continue;
                    }
                    else if(operand.isType(MathOperator.MULTIPLY)){
                        operands.addAll(i+1, operand.getOperands());
                        continue;
                    }
                    Equation base = new Equation("BASE(" + operand + ")",1);
                    Equation exponent = new Equation("EXPONENT(" + operand + ")",1);
                    if(powers.containsKey(base.toString())){
                        powers.put(base.toString(), "ADD(" + powers.get(base.toString()) + "," + exponent + ")");
                    }
                    else{
                        powers.put(base.toString(), exponent.toString());
                    }
                }
                //Turn our base-exponent pairs into an equation
                List<Equation> newOperands = new ArrayList<>();
                Equation constant;
                if(constantList.size() == 1){
                    constant = constantList.get(0);
                }
                else{
                    constant = Simplifier.simplifyWithMetaFunction(Equation.fromList(constantList, MathOperator.MULTIPLY), MathOperator.SIMPLIFY_RATIONAL_EXPRESSION);
                }
                if(!constant.equals(new Equation("1"))){
                    newOperands.add(constant);
                }
                for(String key : powers.keySet()){
                    Equation base = new Equation(key, 2);
                    Equation exponent = new Equation(powers.get(key), 2);
                    if(exponent.equals(new Equation("0", 0))){
                        //If we don't have a constant, set it to 0
                        if(newOperands.size() == 0){
                            newOperands.add(0, new Equation("1", 0));
                        }
                        else if(!newOperands.get(0).isType(SimplificationType.CONSTANT)){
                            newOperands.add(0, new Equation("1", 0));
                        }
                    }
                    else if(exponent.equals(new Equation("1", 0))){
                        newOperands.add(base);
                    }
                    else{
                        newOperands.add(new Equation("POWER(" + base + "," + exponent + ")", 0));
                    }
                }
                if(newOperands.size() == 1){
                    return newOperands.get(0);
                }
                Equation finalEq = Equation.fromList(newOperands, MathOperator.MULTIPLY);
                return finalEq;
            }, new MathObject(MathOperator.SIMPLIFY_PRODUCT)),
            new EquationSub((DirectOperation & Serializable) eq -> {
                Equation newEq = eq.getSubEquation(0);
                if(newEq.isType(MathOperator.MULTIPLY)){
                    //Simplify the first constants you see.
                    List<Equation> totalList = new ArrayList<>();
                    for(Equation sub : newEq.getOperands()){
                        if(sub.isType(SimplificationType.CONSTANT)){
                            totalList.add(sub);
                        }
                    }
                    if(totalList.size() == 1){
                        return totalList.get(0);
                    }
                    if(totalList.size() == 0){
                        return new Equation("1");
                    }
                    Equation total = Equation.fromList(totalList);
                    total.tree.data = new MathObject(MathOperator.MULTIPLY);
                    total = Simplifier.simplifyWithMetaFunction(total, MathOperator.AUTOSIMPLIFY);
                    return total;
                }
                else if(newEq.isType(MathOperator.EXPRESSION)){
                    return new Equation("1");
                }
                else{
                    return new Equation("UNDEFINED");
                }
            }, new MathObject(MathOperator.COEFFICIENT)),
            new EquationSub((DirectOperation & Serializable) eq -> {
                Equation newEq = eq.getSubEquation(0);
                if(newEq.isType(MathOperator.MULTIPLY)) {
                    Equation sorted = Simplifier.orderEquation(eq);
                    //Now find the first non-number (variable)
                    int firstVarIndex = 0;
                    for (int i = 0; i < newEq.getOperands().size(); i++) {
                        if (!newEq.getSubEquation(i).isType(SimplificationType.CONSTANT)) {
                            firstVarIndex = i;
                            break;
                        }
                    }
                    List<Equation> terms = newEq.getOperands().subList(firstVarIndex, newEq.getOperands().size());
                    if (terms.size() == 1) {
                        return terms.get(0);
                    }
                    Equation termList = Equation.fromList(terms);
                    termList.tree.data = new MathObject(MathOperator.MULTIPLY);
                    return termList;
                }
                else if(newEq.isType(MathOperator.POWER)){
                    return newEq;
                }
                else if(newEq.isType(MathOperator.EXPRESSION)){
                    return newEq;
                }
                else {
                    return new Equation("UNDEFINED");
                }
            }, new MathObject(MathOperator.TERM)),
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
