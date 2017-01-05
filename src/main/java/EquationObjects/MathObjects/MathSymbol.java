package EquationObjects.MathObjects;

/**
 * Created by jack on 12/30/2016.
 */
public enum MathSymbol {
    ADD(2, false, true),
    MULTIPLY(2, false, true),
    SUBTRACT(2, true, false),
    DIVIDE(2, true, false),
    POWER(2, true, false),
    SINE(1, true, true),
    COSINE(1, true, true),
    TANGENT(1, true, true),
    NUMBER(0, false, false),
    EXPRESSION(0, false, false),
    OR(2, false, true),
    AND(2, false, true),
    IFEQUAL(2, false, true);

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
