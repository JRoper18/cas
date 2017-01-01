package EquationObjects.MathObjects;

/**
 * Created by jack on 12/30/2016.
 */
public enum MathOperators {
    ADD(2, false, true),
    MULTIPLY(2, false, true),
    SUBTRACT(2, true, false),
    DIVIDE(2, true, false);

    private final int arguments;
    private final boolean ordered;
    private final boolean associative;
    private MathOperators(int arguments, boolean ordered, boolean associative){
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
