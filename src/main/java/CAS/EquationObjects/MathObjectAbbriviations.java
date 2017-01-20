package CAS.EquationObjects;

import java.util.HashMap;

/**
 * Created by jack on 1/2/2017.
 */
public class MathObjectAbbriviations {
    public static final HashMap<String, MathObject> abbriviations = new HashMap<>();
    static {
        abbriviations.put("+", new MathObject(MathOperator.ADD));
        abbriviations.put("PLUS", new MathObject(MathOperator.ADD));
        abbriviations.put("*", new MathObject(MathOperator.MULTIPLY));
        abbriviations.put("TIMES", new MathObject(MathOperator.MULTIPLY));
        abbriviations.put("-", new MathObject(MathOperator.SUBTRACT));
        abbriviations.put("MINUS", new MathObject(MathOperator.SUBTRACT));
        abbriviations.put("/", new MathObject(MathOperator.DIVIDE));
        abbriviations.put("SIN", new MathObject(MathOperator.SINE));
        abbriviations.put("||", new MathObject(MathOperator.OR));
        abbriviations.put("<=", new MathObject(MathOperator.LESS_EQUAL));
        abbriviations.put("==", new MathObject(MathOperator.EQUALS));
        abbriviations.put("!", new MathObject(MathOperator.NOT));
        abbriviations.put("GCD", new MathObject(MathOperator.GREATEST_COMMON_DENOMINATOR));
    }
}
