package CAS.EquationObjects;

import java.util.HashMap;

/**
 * Created by jack on 1/2/2017.
 */
public class MathObjectAbbriviations {
    public static final HashMap<String, AbbriviationData> abbriviations = new HashMap<>();
    static {
        abbriviations.put("+", new AbbriviationData(new MathObject(MathOperator.ADD), true));
        abbriviations.put("PLUS", new AbbriviationData(new MathObject(MathOperator.ADD), false));
        abbriviations.put("*", new AbbriviationData(new MathObject(MathOperator.MULTIPLY), true));
        abbriviations.put("TIMES", new AbbriviationData(new MathObject(MathOperator.MULTIPLY), false));
        abbriviations.put("-", new AbbriviationData(new MathObject(MathOperator.SUBTRACT), true));
        abbriviations.put("MINUS", new AbbriviationData(new MathObject(MathOperator.SUBTRACT), false));
        abbriviations.put("/", new AbbriviationData(new MathObject(MathOperator.DIVIDE), true));
        abbriviations.put("SIN", new AbbriviationData(new MathObject(MathOperator.SINE), false));
        abbriviations.put("||", new AbbriviationData(new MathObject(MathOperator.OR), true));
        abbriviations.put("<=", new AbbriviationData(new MathObject(MathOperator.LESS_EQUAL), true));
        abbriviations.put("==", new AbbriviationData(new MathObject(MathOperator.EQUALS), true));
        abbriviations.put("!", new AbbriviationData(new MathObject(MathOperator.NOT), false));
        abbriviations.put("GCD", new AbbriviationData(new MathObject(MathOperator.GREATEST_COMMON_DENOMINATOR), false));
    }
}
