package CAS.EquationObjects;

import CAS.EquationObjects.MathObjects.MathObject;
import CAS.EquationObjects.MathObjects.MathSymbol;

import java.util.HashMap;

/**
 * Created by jack on 1/2/2017.
 */
public class EquationObjectAbbriviations {
    public static final HashMap<String, EquationObject> abbriviations = new HashMap<>();
    static {
        abbriviations.put(")", new SyntaxObject(SyntaxObjectType.CLOSE_PAREN));
        abbriviations.put("(", new SyntaxObject(SyntaxObjectType.OPEN_PAREN));
        abbriviations.put(",", new SyntaxObject(SyntaxObjectType.COMMA));
        abbriviations.put("+", new MathObject(MathSymbol.ADD));
        abbriviations.put("PLUS", new MathObject(MathSymbol.ADD));
        abbriviations.put("*", new MathObject(MathSymbol.MULTIPLY));
        abbriviations.put("TIMES", new MathObject(MathSymbol.MULTIPLY));
        abbriviations.put("-", new MathObject(MathSymbol.SUBTRACT));
        abbriviations.put("MINUS", new MathObject(MathSymbol.SUBTRACT));
        abbriviations.put("/", new MathObject(MathSymbol.DIVIDE));
        abbriviations.put("SIN", new MathObject(MathSymbol.SINE));
        abbriviations.put("||", new MathObject(MathSymbol.OR));
        abbriviations.put("<=", new MathObject(MathSymbol.LESS_EQUAL));
        abbriviations.put("==", new MathObject(MathSymbol.EQUALS));
        abbriviations.put("!", new MathObject(MathSymbol.NOT));
        abbriviations.put("GCD", new MathObject(MathSymbol.GREATEST_COMMON_DENOMINATOR));
    }
}