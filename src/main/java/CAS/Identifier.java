package CAS;

import CAS.EquationObjects.MathInteger;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import CAS.EquationObjects.MathOperatorSubtype;

import static CAS.SimplificationType.*;

/**
 * Created by jack on 1/13/2017.
 */
public class Identifier {
    public static boolean isType(Equation equation, SimplificationType type){
        switch(type){
            case INTEGER:
                return equation.getRoot() instanceof MathInteger;
            case FRACTION_STANDARD_FORM: //Page 57
                return equation.getRoot().getOperator() == MathOperator.FRACTION && new Equation("GCD(OPERAND(" + equation + ", 1),OPERAND(" + equation + ",2))").equals(new Equation("1"));
            case EXPLICIT_ALGEBRAIC_NUMBER: //PDF page 76
                Equation typeofEq = new Equation("TYPEOF(" + equation.getRoot() + ")");
                if(equation.isType(INTEGER)|| typeofEq.equals("FRACTION")){
                    return true;
                }
                else if(typeofEq.equals(new Equation("PLUS")) || typeofEq.equals(new Equation("TIMES"))){
                    for(Tree<MathObject> child : equation.tree.getChildren()){
                        if(!new Equation(child).isType(EXPLICIT_ALGEBRAIC_NUMBER)){
                            return false;
                        }
                    }
                    return true;
                }
                break;
            case RATIONAL_NUMBER_EXPRESSION:
                MathOperator op = equation.getRoot().getOperator();
                if(equation.isType(INTEGER) || op == MathOperator.FRACTION){
                    return true;
                }
                if((op == MathOperator.ADD || op == MathOperator.SUBTRACT || op == MathOperator.MULTIPLY) && new Equation("NUMBER_OF_OPERANDS (" + equation + ")").equals(new Equation("2"))){
                    return equation.getSubEquation(0).isType(RATIONAL_NUMBER_EXPRESSION) && equation.getSubEquation(1).isType(RATIONAL_NUMBER_EXPRESSION);
                }
                if(op == MathOperator.POWER){
                    return equation.getSubEquation(0).isType(RATIONAL_NUMBER_EXPRESSION) && equation.getSubEquation(1).isType(INTEGER);
                }
                return false;
            case AUTOSIMPLIFIED_EXPRESSION: //See the 2nd computer algebra book, pdf page 101.
                MathObject root = equation.getRoot();
                if(equation.isType(INTEGER) || equation.isType(FRACTION_STANDARD_FORM) || (root.getOperator().getSubType()== MathOperatorSubtype.SYMBOL && root.getOperator() != MathOperator.UNDEFINED)) {
                    return true;
                }
                break;
        }
        return true;//DEFAULT
    }
}
