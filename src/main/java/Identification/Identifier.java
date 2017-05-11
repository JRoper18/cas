package Identification;

import CAS.Equation;
import CAS.EquationObjects.*;
import CAS.Tree;

import java.math.BigInteger;

/**
 * Created by jack on 1/13/2017.
 */
public class Identifier {
    public static boolean isType(Equation equation, IdentificationType type){
        MathOperator op = equation.getRoot().getOperator();
        switch(type){
            case EXPRESSION:
                return true;
            case VARIABLE:
                return equation.isType(MathOperator.EXPRESSION);
            case INTEGER:
                if(equation.isType(MathOperator.EXPRESSION)){
                    return (((GenericExpression) equation.getRoot()).type == IdentificationType.INTEGER);
                }
                return (equation.getRoot() instanceof MathInteger);
            case IRRATIONAL:
                for(Equation operand: equation.getOperands()){
                    if(operand.isType(IdentificationType.IRRATIONAL)){
                        return true;
                    }
                }
                return op.isTranscendental();
            case VARCONSTANT:
                if(equation.getRoot() instanceof GenericExpression){
                    return ((GenericExpression) equation.getRoot()).type == IdentificationType.VARCONSTANT;
                }
                return !equation.tree.containsClass(GenericExpression.class);
            case CONSTANT:
                return !equation.tree.containsClass(GenericExpression.class);
            case NEGATIVE_CONSTANT:
                if(equation.isType(IdentificationType.INTEGER)){
                    BigInteger num = ((MathInteger) equation.getRoot()).num;
                    return num.signum() == -1;
                }
                if(equation.isType(IdentificationType.FRACTION_STANDARD_FORM)){
                    BigInteger top = ((MathInteger) equation.getSubEquation(0).getRoot()).num;
                    return top.signum() == -1;
                }
                else{
                    return false; //Evaluate all rational expressions later.
                }
            case FRACTION_STANDARD_FORM: //Page 57
                return equation.getRoot().getOperator() == MathOperator.FRACTION && new Equation("GCD_META(OPERAND(" + equation + ", 0),OPERAND(" + equation + ",1))", 1).equals(new Equation("1", 0));
            case EXPLICIT_ALGEBRAIC_NUMBER: //PDF page 76
                Equation typeofEq = new Equation("TYPEOF(" + equation.getRoot() + ")");
                if(equation.isType(IdentificationType.INTEGER)|| typeofEq.equals("FRACTION")){
                    return true;
                }
                else if(typeofEq.equals(new Equation("PLUS", 0)) || typeofEq.equals(new Equation("TIMES", 0))){
                    for(Tree<MathObject> child : equation.tree.getChildren()){
                        if(!new Equation(child).isType(IdentificationType.EXPLICIT_ALGEBRAIC_NUMBER)){
                            return false;
                        }
                    }
                    return true;
                }
                break;
            case RATIONAL_NUMBER_EXPRESSION:
                if(!equation.isType(IdentificationType.CONSTANT)){
                    return false;
                }
                if(equation.isType(IdentificationType.INTEGER) || op == MathOperator.FRACTION){
                    return true;
                }
                if((op == MathOperator.ADD || op == MathOperator.SUBTRACT || op == MathOperator.MULTIPLY)){
                    return equation.getSubEquation(0).isType(IdentificationType.RATIONAL_NUMBER_EXPRESSION) && equation.getSubEquation(1).isType(IdentificationType.RATIONAL_NUMBER_EXPRESSION);
                }
                if(op == MathOperator.POWER){
                    return equation.getSubEquation(0).isType(IdentificationType.RATIONAL_NUMBER_EXPRESSION) && equation.getSubEquation(1).isType(IdentificationType.INTEGER);
                }
                return false;
            case AUTOSIMPLIFIED_EXPRESSION: //See the 2nd computer algebra book, pdf page 101.
                //All expressions should follow these rules by default.
                if(equation.isType(IdentificationType.INTEGER) || equation.isType(IdentificationType.FRACTION_STANDARD_FORM) || (op.getSubType()== MathOperatorSubtype.SYMBOL && op != MathOperator.UNDEFINED)) {
                    //If it's a constant, then it's already simplified (unless it's undefined)
                    return true;
                }
                else if(op == MathOperator.MULTIPLY){
                    boolean foundConstant = false; //At most we should have only one constant inside of a product, because they can be collected into a single term.
                    for(int i = 0; i<equation.tree.getNumberOfChildren(); i++){ //For every operand of the product
                        Equation sub = equation.getSubEquation(i);
                        if(!sub.isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION)){
                            return false;
                        }
                        MathOperator subOp = sub.getRoot().getOperator();
                        if(subOp == MathOperator.UNDEFINED){ //No undefined
                            return false;
                        }
                        else if(sub.isType(IdentificationType.INTEGER)){
                            if(((MathInteger) sub.getRoot()).num.intValue() == 0 || ((MathInteger) sub.getRoot()).num.intValue() == 1){ //Multiplication by 1 or 0 can be simplified.
                                return false;
                            }
                            if(foundConstant){ //Again, only 1 constant
                                return false;
                            }
                            foundConstant = true;
                        }
                        else if(subOp == MathOperator.FRACTION){
                            if(foundConstant){
                                return false;
                            }
                            foundConstant = true;
                        }
                        else if(!(subOp == MathOperator.ADD || subOp == MathOperator.POWER || subOp == MathOperator.FACTORIAL || subOp == MathOperator.CUSTOM_FUNCTION || subOp.getSubType() == MathOperatorSubtype.SYMBOL)){ //If we have another product inside of this,
                            //It's not good
                            return false;
                        }
                        for(int j = 0; j<equation.tree.getNumberOfChildren(); j++){
                            Equation temp = equation.getSubEquation(j);
                            if(i != j && new Equation("BASE(" + sub + ")", 1).equals(new Equation("BASE(" + temp + ")", 1))){ //Make sure we share no like terms that we can compress.
                                return false;
                            }
                            if(i < j){
                                if(!(sub.compareTo(temp) < 0)){ //Check the ordering.
                                    return false;
                                }
                            }
                        }

                    }
                    //No like terms or unneccesary constants.
                    return true;
                }
                else if(op == MathOperator.ADD){
                    boolean foundConstant = false; //At most we should have only one constant inside of a sum, because they can be collected into a single term.
                    for(int i = 0; i<equation.tree.getNumberOfChildren(); i++){ //For every operand
                        Equation sub = equation.getSubEquation(i);
                        if(!sub.isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION)){
                            return false;
                        }
                        MathOperator subOp = sub.getRoot().getOperator();
                        if(subOp == MathOperator.UNDEFINED){ //No undefined
                            return false;
                        }
                        if(sub.isType(IdentificationType.INTEGER)){
                            if(((MathInteger) sub.getRoot()).num.intValue() == 0){ //No addition by 0
                                return false;
                            }
                            if(foundConstant){ //Again, only 1 constant
                                return false;
                            }
                            foundConstant = true;
                        }
                        else if(subOp == MathOperator.FRACTION){
                            if(foundConstant){
                                return false;
                            }
                            foundConstant = true;
                            return true;
                        }
                        else if(!(subOp == MathOperator.MULTIPLY || subOp == MathOperator.POWER || subOp == MathOperator.FACTORIAL || subOp == MathOperator.CUSTOM_FUNCTION || subOp.getSubType() == MathOperatorSubtype.SYMBOL)){
                            return false;
                        }
                        for(int j = 0; j<equation.tree.getNumberOfChildren(); j++){
                            Equation temp = equation.getSubEquation(j);
                            if(i != j && new Equation("TERM(" + sub + ")", 1).equals(new Equation("TERM(" + temp + ")",1))){ //Make sure we share no like terms that we can compress.
                                return false;
                            }
                            if(i < j){
                                if(!(sub.compareTo(temp) < 0)){ //Check the ordering.
                                    return false;
                                }
                            }
                        }

                    }
                    //No like terms or unneccesary constants.
                    return true;
                }
                else if(op == MathOperator.POWER){
                    Equation power = equation.getSubEquation(1);
                    Equation base = equation.getSubEquation(0);
                    if(!base.isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION) || !power.isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION)){
                        return false;
                    }
                    if(power.equals(new Equation("1")) || power.equals(new Equation("0"))){
                        return false;
                    }
                    if (!power.isType(IdentificationType.INTEGER)) {
                        if(base.equals(new Equation("0")) || base.equals(new Equation("1"))){
                            return false;
                        }
                    }
                    return true;
                }
                else if(op == MathOperator.FACTORIAL){
                    if(equation.getSubEquation(0).isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION)){
                        if(equation.getSubEquation(0).isType(IdentificationType.INTEGER)){
                            return ((MathInteger) equation.getSubEquation(0).getRoot()).num.signum() < 0; //Only negative integer factorials can't be simplified
                        }
                        return true;
                    }
                    return false;
                }
                else if(op == MathOperator.CUSTOM_FUNCTION){
                    for(int i = 0; i<equation.tree.getNumberOfChildren(); i++){
                        if(!equation.getSubEquation(i).isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION)){
                            return false;
                        }
                    }
                    return true;
                }
                return true;
        }
        return true;//DEFAULT
    }
}
