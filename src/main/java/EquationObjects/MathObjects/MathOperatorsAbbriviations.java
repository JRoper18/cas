package EquationObjects.MathObjects;

import java.util.HashMap;

/**
 * Created by jack on 12/30/2016.
 */
public class MathOperatorsAbbriviations {
    public static HashMap<String, MathOperators> abbriviations;
    public MathOperatorsAbbriviations(){
        abbriviations = new HashMap<>();
        abbriviations.put("+", MathOperators.ADD);
        abbriviations.put("*", MathOperators.MULTIPLY);
        abbriviations.put("-", MathOperators.SUBTRACT);
        abbriviations.put("/", MathOperators.DIVIDE);
    }
}
