package CAS.EquationObjects;

import java.util.HashMap;

/**
 * Created by jack on 1/2/2017.
 */
public class MathObjectAbbriviations {
    public static final HashMap<String, AbbriviationData> abbriviations = new HashMap<>();
    static {
        abbriviations.put("+", new AbbriviationData(new MathObject(MathOperator.ADD), AbbriviationType.INFIX));
        abbriviations.put("PLUS", new AbbriviationData(new MathObject(MathOperator.ADD), AbbriviationType.PREFIX));
        abbriviations.put("*", new AbbriviationData(new MathObject(MathOperator.MULTIPLY), AbbriviationType.INFIX));
        abbriviations.put("TIMES", new AbbriviationData(new MathObject(MathOperator.MULTIPLY), AbbriviationType.PREFIX));
        abbriviations.put("-", new AbbriviationData(new MathObject(MathOperator.SUBTRACT), AbbriviationType.INFIX));
        abbriviations.put("MINUS", new AbbriviationData(new MathObject(MathOperator.SUBTRACT), AbbriviationType.PREFIX));
        abbriviations.put("/", new AbbriviationData(new MathObject(MathOperator.DIVIDE), AbbriviationType.INFIX));
        abbriviations.put("SIN", new AbbriviationData(new MathObject(MathOperator.SINE), AbbriviationType.PREFIX));
        abbriviations.put("^", new AbbriviationData(new MathObject(MathOperator.POWER), AbbriviationType.INFIX));
        abbriviations.put("GCD", new AbbriviationData(new MathObject(MathOperator.GREATEST_COMMON_DENOMINATOR), AbbriviationType.PREFIX));
        abbriviations.put("DERIV", new AbbriviationData(new MathObject(MathOperator.DERIVATIVE), AbbriviationType.PREFIX));
        abbriviations.put("DERIV", new AbbriviationData(new MathObject(MathOperator.DERIVATIVE), AbbriviationType.PREFIX));
        abbriviations.put("LN", new AbbriviationData(new MathObject(MathOperator.NATURAL_LOG), AbbriviationType.PREFIX));
    }
}
