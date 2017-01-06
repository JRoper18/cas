package EquationObjects.MathObjects;

/**
 * Created by jack on 12/30/2016.
 */
public enum MathSymbol {
    ADD(2, false, true),
    MULTIPLY(2, false, true),
    SUBTRACT(2, true, false),
    DIVIDE(2, true, false),
    FRACTION(2, true, false),
    REMAINDER(2, true, false),
    GREATEST_COMMON_DENOMINATOR(2, false, true),
    POWER(2, true, false),
    SINE(1, true, true),
    COSINE(1, true, true),
    TANGENT(1, true, true),
    NUMBER(0, false, false),
    EXPRESSION(0, false, false),
    PATTERN_OR(2, false, true),
    PATTERN_AND(2, false, true),
    ANYWHERE(1, true, true),
    EVERYWHERE(1, true, false),
    TYPEOF(1, false, false),
    OR(2, false, true),
    AND(2, false, true),
    NOT(1, false, true),
    TRUE(0, false, false),
    FALSE(0, false, false),
    EQUALS(2, false, true),
    LESS_EQUAL(2, true, false),
    LESS(2, true, false);

    private final int arguments;
    private final boolean ordered;
    private final boolean associative;
    private MathSymbol(int arguments, boolean ordered, boolean associative){
        this.arguments = arguments;
        this.ordered = ordered;
        this.associative = associative;
    }
    public int getArguments(){
        return this.arguments;
    }
    public boolean isOrdered(){
        return this.ordered;
    }
    public boolean isAssociative(){
        return this.associative;
    }
}
