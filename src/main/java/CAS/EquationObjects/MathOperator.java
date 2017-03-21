package CAS.EquationObjects;

/**
 * Created by jack on 12/30/2016.
 */
public enum MathOperator {
    //Atomic/building block/unchanging values/symbols
    NUMBER(0, false, false, MathOperatorSubtype.SYMBOL, null),
    TRUE(0, false, false, MathOperatorSubtype.SYMBOL, null),
    FALSE(0, false, false, MathOperatorSubtype.SYMBOL, null),
    EXPRESSION(0, false, false, MathOperatorSubtype.SYMBOL, null),
    UNDEFINED(0, false, false, MathOperatorSubtype.SYMBOL, null),
    E(0, false, false, MathOperatorSubtype.SYMBOL, null),
    ONE(0, false, false, MathOperatorSubtype.SYMBOL, null),
    ZERO(0, false, false, MathOperatorSubtype.SYMBOL, null),
    PI(0, false, false, MathOperatorSubtype.SYMBOL, null),

    ADD(2, false, true, MathOperatorSubtype.MATH, ZERO),
    MULTIPLY(2, false, true, MathOperatorSubtype.MATH, ONE),
    SUBTRACT(2, true, false, MathOperatorSubtype.MATH, ZERO),
    DIVIDE(2, true, false, MathOperatorSubtype.MATH, ONE),
    FRACTION(2, true, false, MathOperatorSubtype.MATH, null),
    REMAINDER(2, true, false, MathOperatorSubtype.MATH, ONE),
    GREATEST_COMMON_DENOMINATOR(2, false, true, MathOperatorSubtype.META, ONE),
    POWER(2, true, false, MathOperatorSubtype.MATH, null),
    SINE(1, true, false, MathOperatorSubtype.MATH, null),
    COSINE(1, true, false, MathOperatorSubtype.MATH, null),
    TANGENT(1, true, false, MathOperatorSubtype.MATH, null),
    FACTORIAL(1, true, false, MathOperatorSubtype.MATH, null),
    DERIVATIVE(2, true, false, MathOperatorSubtype.MATH, null),
    CUSTOM_FUNCTION(0, false, false, MathOperatorSubtype.MATH, null), //Placeholder values - check customfunction class
    LIST(1, true, true, MathOperatorSubtype.MATH, null),
    //Pattern matching
    PATTERN_OR(2, false, true, MathOperatorSubtype.PATTERN, FALSE),
    PATTERN_AND(2, false, true, MathOperatorSubtype.PATTERN, TRUE),
    //Booleans and conditionals
    OR(2, false, true, MathOperatorSubtype.BOOLEAN, FALSE),
    AND(2, false, true, MathOperatorSubtype.BOOLEAN, TRUE),
    NOT(1, false, false, MathOperatorSubtype.BOOLEAN, null),
    EQUALS(2, false, true, MathOperatorSubtype.BOOLEAN, null),
    LESS_EQUAL(2, true, false, MathOperatorSubtype.BOOLEAN, null),
    LESS(2, true, false, MathOperatorSubtype.BOOLEAN, null),
    //Meta functions beyond here
    TYPEOF(1, false, false, MathOperatorSubtype.META, null),
    NUMBER_OF_OPERANDS(2, true, false, MathOperatorSubtype.META, null),
    OPERAND(2, true, false, MathOperatorSubtype.META, null),
    CONTAINS(1, false, false, MathOperatorSubtype.META, null),
    ADJOIN(2, false, false, MathOperatorSubtype.META, null),
    REST(1, false, false, MathOperatorSubtype.META, null),
    EXPAND(1, false, false, MathOperatorSubtype.META, null),
    SIMPLIFY_RATIONAL_FRACTION(1, false, false, MathOperatorSubtype.META, null),
    SIMPLIFY_RATIONAL_EXPRESSION(1, false, false, MathOperatorSubtype.META, null),
    AUTOSIMPLIFY(1, false, false, MathOperatorSubtype.META, null),
    SIMPLIFY_SUM(1, false, false, MathOperatorSubtype.META, null),
    SIMPLIFY_PRODUCT(1, false, false, MathOperatorSubtype.META, null),
    SIMPLIFY_POWER(1, false, false, MathOperatorSubtype.META, null),
    BASE(1, false, false, MathOperatorSubtype.META, null),
    EXPONENT(1, false, false, MathOperatorSubtype.META, null),
    TERM(1, false, false, MathOperatorSubtype.META, null),
    COEFFICIENT(1, false, false, MathOperatorSubtype.META, null);

    private final int arguments;
    private final boolean ordered;
    private final boolean associative;
    private final MathOperatorSubtype subType;
    private final MathOperator identity;
    private MathOperator(int arguments){
        this(arguments, false, false, MathOperatorSubtype.MATH, null);
    }
    private MathOperator(int arguments, boolean ordered, boolean associative){
        this(arguments, ordered, associative, MathOperatorSubtype.MATH, null);
    }
    private MathOperator(int arguments, boolean ordered, boolean associative, MathOperatorSubtype subType, MathOperator identity){
        this.arguments = arguments;
        this.ordered = ordered;
        this.associative = associative;
        this.subType = subType;
        this.identity = identity;

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
    public MathOperator identity(){
        return this.identity;
    }
}
