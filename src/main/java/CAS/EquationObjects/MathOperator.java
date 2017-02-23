package CAS.EquationObjects;

/**
 * Created by jack on 12/30/2016.
 */
public enum MathOperator {
    ADD(2, false, true, MathOperatorSubtype.MATH),
    MULTIPLY(2, false, true, MathOperatorSubtype.MATH),
    SUBTRACT(2, true, false, MathOperatorSubtype.MATH),
    DIVIDE(2, true, false, MathOperatorSubtype.MATH),
    FRACTION(2, true, false, MathOperatorSubtype.MATH),
    REMAINDER(2, true, false, MathOperatorSubtype.MATH),
    GREATEST_COMMON_DENOMINATOR(2, false, true, MathOperatorSubtype.META),
    POWER(2, true, false, MathOperatorSubtype.MATH),
    SINE(1, true, false, MathOperatorSubtype.MATH),
    COSINE(1, true, false, MathOperatorSubtype.MATH),
    TANGENT(1, true, false, MathOperatorSubtype.MATH),
    FACTORIAL(1, true, false, MathOperatorSubtype.MATH),
    CUSTOM_FUNCTION(0, false, false, MathOperatorSubtype.MATH), //Placeholder values - check customfunction class
    LIST(1, true, true, MathOperatorSubtype.MATH),
    //Pattern matching
    PATTERN_OR(2, false, true, MathOperatorSubtype.PATTERN),
    PATTERN_AND(2, false, true, MathOperatorSubtype.PATTERN),
    //Booleans and conditionals
    OR(2, false, true, MathOperatorSubtype.BOOLEAN),
    AND(2, false, true, MathOperatorSubtype.BOOLEAN),
    NOT(1, false, false, MathOperatorSubtype.BOOLEAN),
    EQUALS(2, false, true, MathOperatorSubtype.BOOLEAN),
    LESS_EQUAL(2, true, false, MathOperatorSubtype.BOOLEAN),
    LESS(2, true, false, MathOperatorSubtype.BOOLEAN),
    //Meta functions beyond here
    TYPEOF(1, false, false, MathOperatorSubtype.META),
    NUMBER_OF_OPERANDS(2, true, false, MathOperatorSubtype.META),
    OPERAND(2, true, false, MathOperatorSubtype.META),
    CONTAINS(1, false, false, MathOperatorSubtype.META),
    SIMPLIFY_RATIONAL_FRACTION(1, false, false, MathOperatorSubtype.META),
    SIMPLIFY_RATIONAL_EXPRESSION(1, false, false, MathOperatorSubtype.META),
    AUTOSIMPLIFY(1, false, false, MathOperatorSubtype.META),
    SIMPLIFY_POWER(1, false, false, MathOperatorSubtype.META),
    SIMPLIFY_POWER_INT(1, false, false, MathOperatorSubtype.META),
    SIMPLIFY_SUM(1, false, false, MathOperatorSubtype.META),
    SIMPLIFY_PRODUCT(1, false, false, MathOperatorSubtype.META),
    SIMPLIFY_PRODUCT_RECURSIVE(1, false, false, MathOperatorSubtype.META),
    SIMPLIFY_SUBTRACTION(1, false, false, MathOperatorSubtype.META),
    SIMPLIFY_DIVIDE(1, false, false, MathOperatorSubtype.META),
    SIMPLIFY_FACTORIAL(1, false, false, MathOperatorSubtype.META),
    BASE(1, false, false, MathOperatorSubtype.META),
    EXPONENT(1, false, false, MathOperatorSubtype.META),
    TERM(1, false, false, MathOperatorSubtype.META),
    //Atomic/building block/unchanging values/symbols
    NUMBER(0, false, false, MathOperatorSubtype.SYMBOL),
    TRUE(0, false, false, MathOperatorSubtype.SYMBOL),
    FALSE(0, false, false, MathOperatorSubtype.SYMBOL),
    EXPRESSION(0, false, false, MathOperatorSubtype.SYMBOL),
    UNDEFINED(0, false, false, MathOperatorSubtype.SYMBOL),
    E(0, false, false, MathOperatorSubtype.SYMBOL),
    PI(0, false, false, MathOperatorSubtype.SYMBOL);

    private final int arguments;
    private final boolean ordered;
    private final boolean associative;
    private final MathOperatorSubtype subType;
    private MathOperator(int arguments){
        this(arguments, false, false, MathOperatorSubtype.MATH);
    }
    private MathOperator(int arguments, boolean ordered, boolean associative){
        this(arguments, ordered, associative, MathOperatorSubtype.MATH);
    }
    private MathOperator(int arguments, boolean ordered, boolean associative, MathOperatorSubtype subType){
        this.arguments = arguments;
        this.ordered = ordered;
        this.associative = associative;
        this.subType = subType;
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
    public boolean isAssociative(){
        return this.associative;
    }
}
