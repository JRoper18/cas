package CAS.EquationObjects;

import CAS.Equation;

/**
 * Created by jack on 12/30/2016.
 */
public enum MathOperator {
    //Atomic/building block/unchanging values/symbols
    NUMBER(0, false, false, MathOperatorSubtype.SYMBOL, null, null, false),
    TRUE(0, false, false, MathOperatorSubtype.SYMBOL, null, null, false),
    FALSE(0, false, false, MathOperatorSubtype.SYMBOL, null, null, false),
    EXPRESSION(0, false, false, MathOperatorSubtype.SYMBOL, null, null, false),
    UNDEFINED(0, false, false, MathOperatorSubtype.SYMBOL, null, null, false),
    E(0, false, false, MathOperatorSubtype.SYMBOL, null, null, false),
    PI(0, false, false, MathOperatorSubtype.SYMBOL, null, null, false),
    GENERIC_FUNCTION(1, true, true, MathOperatorSubtype.SYMBOL, null, null, false),

    ADD(2, false, true, MathOperatorSubtype.MATH, "0", null, false),
    MULTIPLY(2, false, true, MathOperatorSubtype.MATH, "1", null, false),
    SUBTRACT(2, true, false, MathOperatorSubtype.MATH, "0", null, false),
    DIVIDE(2, true, false, MathOperatorSubtype.MATH, "1", null, false),
    FRACTION(2, true, false, MathOperatorSubtype.MATH, null, null, false),
    REMAINDER(2, true, false, MathOperatorSubtype.MATH, "1", null, false),
    POWER(2, true, false, MathOperatorSubtype.MATH, null, null, false),
    GREATEST_COMMON_DENOMINATOR(2, false, true, MathOperatorSubtype.META, "1", null, false),
    SINE(1, true, false, MathOperatorSubtype.MATH, null, null, false),
    COSINE(1, true, false, MathOperatorSubtype.MATH, null, null, false),
    TANGENT(1, true, false, MathOperatorSubtype.MATH, null, null, false),
    FACTORIAL(1, true, false, MathOperatorSubtype.MATH, null, null, false),
    DERIVATIVE(2, true, false, MathOperatorSubtype.MATH, null, null, true),
    CUSTOM_FUNCTION(0, false, false, MathOperatorSubtype.MATH, null, null, false), //Placeholder values - check customfunction class,
    NATURAL_LOG(1, true, false, MathOperatorSubtype.MATH, null, null, true),
    LIST(1, true, true, MathOperatorSubtype.MATH, null, null, false),
    //Pattern matching
    PATTERN_OR(2, false, true, MathOperatorSubtype.PATTERN, "FALSE", null, false),
    PATTERN_AND(2, false, true, MathOperatorSubtype.PATTERN, "TRUE", null, false),
    //Booleans and conditionals
    OR(2, false, true, MathOperatorSubtype.BOOLEAN, "FALSE", null, false),
    AND(2, false, true, MathOperatorSubtype.BOOLEAN, "TRUE", null, false),
    NOT(1, false, false, MathOperatorSubtype.BOOLEAN, null, null, false),
    EQUALS(2, false, true, MathOperatorSubtype.BOOLEAN, null, null, false),
    LESS_EQUAL(2, true, false, MathOperatorSubtype.BOOLEAN, null, null, false),
    LESS(2, true, false, MathOperatorSubtype.BOOLEAN, null, null, false),
    //Meta functions beyond here
    TYPEOF(1, false, false, MathOperatorSubtype.META, null, null, false),
    NUMBER_OF_OPERANDS(2, true, false, MathOperatorSubtype.META, null, null, false),
    OPERAND(2, true, false, MathOperatorSubtype.META, null, null, false),
    CONTAINS(1, false, false, MathOperatorSubtype.META, null, null, false),
    ADJOIN(2, false, false, MathOperatorSubtype.META, null, null, false),
    REST(1, false, false, MathOperatorSubtype.META, null, null, false),
    GCD_META(2, false, true, MathOperatorSubtype.META, "1", null, false),
    EXPAND(1, false, false, MathOperatorSubtype.META, null, null, false),
    SIMPLIFY_RATIONAL_FRACTION(1, false, false, MathOperatorSubtype.META, null, null, false),
    SIMPLIFY_RATIONAL_EXPRESSION(1, false, false, MathOperatorSubtype.META, null, null, false),
    AUTOSIMPLIFY(1, false, false, MathOperatorSubtype.META, null, null, false),
    SIMPLIFY_SUM(1, false, false, MathOperatorSubtype.META, null, null, false),
    SIMPLIFY_PRODUCT(1, false, false, MathOperatorSubtype.META, null, null, false),
    SIMPLIFY_POWER(1, false, false, MathOperatorSubtype.META, null, null, false),
    SIMPLIFY_CONSTANT(1, false, false, MathOperatorSubtype.META, null, null, false),
    BASE(1, false, false, MathOperatorSubtype.META, null, null, false),
    EXPONENT(1, false, false, MathOperatorSubtype.META, null, null, false),
    TERM(1, false, false, MathOperatorSubtype.META, null, null, false),
    COEFFICIENT(1, false, false, MathOperatorSubtype.META, null, null, false);

    private final int arguments;
    private final boolean ordered;
    private final boolean associative;
    private final MathOperatorSubtype subType;
    private final String identity;
    private final String inverse;
    private final boolean transcendental;
    private MathOperator(int arguments, boolean ordered, boolean associative, MathOperatorSubtype subType, String identity, String inverse, boolean trans){
        this.arguments = arguments;
        this.ordered = ordered;
        this.associative = associative;
        this.subType = subType;
        this.identity = identity;
        this.inverse = inverse;
        this.transcendental = trans;
    }
    public MathOperatorSubtype getSubType(){
        return this.subType;
    }
    public int getArguments(){
        return this.arguments;
    }
    public boolean isOrdered(){
        return this.ordered;
    }
    public boolean isDistributive() {
        return !this.ordered;
    }
    public boolean isAssociative(){
        return this.associative;
    }
    public Equation identity(){
        if(this.identity != null){
            return new Equation(this.identity, 0);
        }
        return null;
    }
    public boolean isTranscendental(){
        return this.transcendental;
    }
    public boolean hasIdentity(){
        return this.identity != null;
    }
}
