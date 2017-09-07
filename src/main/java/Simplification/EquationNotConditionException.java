package Simplification;

import CAS.Equation;

/**
 * Created by Jack Roper on 9/6/2017.
 */
public class EquationNotConditionException extends Exception {
    public EquationNotConditionException(Equation notConditionEq){
        super("Equation: " + notConditionEq + " is not a condition. ");
    }
}
