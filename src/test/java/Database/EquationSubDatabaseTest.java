package Database;

import CAS.*;
import CAS.EquationObjects.MathInteger;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Jack Roper on 2/25/2017.
 */
public class EquationSubDatabaseTest {

    @Test
    public void testIntegerAddition() throws Exception {
        assertEquals(new Equation("3"), Simplifier.simplifyByOperator(new Equation("ADD(2, 1)")));
        assertEquals(new Equation("4"), Simplifier.simplifyByOperator(new Equation("ADD(2, 2)")));
        assertEquals(new Equation("10001"), Simplifier.simplifyByOperator(new Equation("ADD(10000, 1)")));
        assertEquals(new Equation("-8"), Simplifier.simplifyByOperator(new Equation("ADD(-5, -3)")));
        assertEquals(new Equation("18"), Simplifier.simplifyByOperator(new Equation("ADD(9, 3, 3, 3)")));
        assertEquals(new Equation("3"), Simplifier.simplifyByOperator(new Equation("ADD(1, 2)")));
    }

    @Test
    public void testIntegerMultiplication() throws Exception {
        assertEquals(new Equation("6"), Simplifier.simplifyByOperator(new Equation("TIMES(2, 3)")));
        assertEquals(new Equation("8"), Simplifier.simplifyByOperator(new Equation("TIMES(2, 2, 2)")));
        assertEquals(new Equation("25"), Simplifier.simplifyByOperator(new Equation("TIMES(5, 5, 1, 1, 1, 1)")));
        assertEquals(new Equation("-8"), Simplifier.simplifyByOperator(new Equation("TIMES(2, -4)")));
        assertEquals(new Equation("8"), Simplifier.simplifyByOperator(new Equation("TIMES(-2, -4)")));
    }

    @Test
    public void testFractionMultiplicaton() throws Exception {
        assertEquals(new Equation("2.5", 0), Simplifier.simplifyByOperator(new Equation("TIMES(5, .5)",0)));
        assertEquals(new Equation("-2", 0), Simplifier.simplifyByOperator(new Equation("TIMES(FRACTION(1, 2), -4)",0)));
    }

    @Test
    public void testRationalSimplification() throws Exception {
        assertEquals(new Equation("UNDEFINED",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(DIVIDE(PLUS(2, 1), 0))",1));
        assertEquals(new Equation("3",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(PLUS(TIMES(1, 1), 2))",1));
        assertEquals(new Equation("4",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(PLUS(TIMES(1, 2), 2))",1));
        assertEquals(new Equation("4",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(PLUS(TIMES(2, 1), 2))",1));
        assertEquals(new Equation("10",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(PLUS(TIMES(4, 2), 2))",1));
        assertEquals(new Equation("10",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(PLUS(2, TIMES(4, 2)))",1));
        assertEquals(new Equation("2",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(MINUS(3, 1))",1));
        assertEquals(new Equation("0",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(MINUS(1, 1))",1));
        assertEquals(new Equation("-4",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(MINUS(2, 6))",1));
        assertEquals(new Equation("1",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(MINUS(-3, -4))",1));
        assertEquals(new Equation("5",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(DIVIDE(25, 5))",1));
        assertEquals(new Equation("-3",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(DIVIDE(-3, 1))",1));
        assertEquals(new Equation("3",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(DIVIDE(-3, -1))",1));
        assertEquals(new Equation("FRACTION(-1, 3)",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(FRACTION(2, -6))",1));
        assertEquals(new Equation("5",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(PLUS(3, DIVIDE(4, 2)))",1));
        assertEquals(new Equation("4",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(PLUS(DIVIDE(4, 2), DIVIDE(4, 2)))",1));
        assertEquals(new Equation("4",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(PLUS(DIVIDE(4, 2), 2))",1));
        assertEquals(new Equation("1",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(DIVIDE(PLUS(2, 1), 3))",1));
        assertEquals(new Equation("FRACTION(5, 6)",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(PLUS(DIVIDE(1, 3), DIVIDE(1, 2)))",1));
        assertEquals(new Equation("3.5",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(PLUS(3, DIVIDE(1, 2)))",1));
        assertEquals(new Equation("1",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(TIMES(DIVIDE(1, 3), 3)))",1));
        assertEquals(new Equation("1",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(TIMES(3, DIVIDE(1, 3))))",1));
        assertEquals(new Equation("FRACTION(2, 9)",1), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(TIMES(DIVIDE(1, 3), DIVIDE(2, 3))))",1));
    }

    @Test
    public void testListOperations() throws Exception {
        assertEquals(new Equation("LIST(1,2,3,4)", 0), new Equation("ADJOIN(1, LIST(2,3,4))", 1));
        assertEquals(new Equation("LIST(1,2,3,4)", 0), new Equation("REST(LIST(0, 1, 2, 3, 4))", 1));
        assertEquals(new Equation("LIST(1,2,3,4)", 0), new Equation("ADJOIN(1, LIST(2,3,4))", 1));
    }

    @Test
    public void testBase() throws Exception {
        assertEquals(new Equation("_x", 0), new Equation("BASE(POWER(_x, 2))", 1));
        assertEquals(new Equation("_x", 0), new Equation("BASE(POWER(_x, 3))", 1));
        assertEquals(new Equation("3", 0), new Equation("BASE(POWER(3, 2))", 1));
        assertEquals(new Equation("3", 0), new Equation("BASE(3)", 1));
        assertEquals(new Equation("ADD(2, 3)", 0), new Equation("BASE(ADD(2, 3))", 1));
    }

    @Test
    public void testExponent() throws Exception {
        assertEquals(new Equation("2", 0), new Equation("EXPONENT(POWER(3, 2))", 1));
        assertEquals(new Equation("_x", 0), new Equation("EXPONENT(POWER(3, _x))", 1));
        assertEquals(new Equation("1", 0), new Equation("EXPONENT(POWER(3, 1))", 1));
        assertEquals(new Equation("1", 0), new Equation("EXPONENT(3)", 1));
        assertEquals(new Equation("ADD(2, 3)", 0), new Equation("EXPONENT(POWER(1, ADD(2, 3)))", 1));
    }

    @Test
    public void testTerm() throws Exception {
        assertEquals(new Equation("_x"), new Equation("TERM(TIMES(2, _x))", 1));
        assertEquals(new Equation("POWER(_x, 1)", 0), new Equation("TERM(TIMES(2, POWER(_x, 1)))", 1));
        assertEquals(new Equation("TIMES(POWER(_x, 2), _y)", 0), new Equation("TERM(TIMES(2, TIMES(POWER(_x, 2), _y)))", 1));
    }

    @Test
    public void testCoefficient() throws Exception {
        assertEquals(new Equation("3", 0), new Equation("COEFFICIENT(TIMES(3, _x))", 1));
        assertEquals(new Equation("1", 0), new Equation("COEFFICIENT(_x)", 1));
        assertEquals(new Equation("4", 0), new Equation("COEFFICIENT(TIMES(2, 2, _x))", 1));
        assertEquals(new Equation("4", 0), new Equation("COEFFICIENT(TIMES(2, 2, _x))", 1));
        assertEquals(new Equation("FRACTION(1, 2)", 0), new Equation("COEFFICIENT(TIMES(FRACTION(1, 2), _x))", 1));
    }

    @Test
    public void testSimplifySum() throws Exception {
        assertEquals(new Equation("TIMES(2, _x)", 0), new Equation("SIMPLIFY_SUM(PLUS(TIMES(1, _x), TIMES(1, _x)))",1));
        assertEquals(new Equation("TIMES(2, _x)", 0), new Equation("SIMPLIFY_SUM(PLUS(TIMES(FRACTION(3, 2), _x), TIMES(FRACTION(1, 2), _x)))",1));
        assertEquals(new Equation("4", 0), new Equation("SIMPLIFY_SUM(PLUS(2, 2))",1));
        assertEquals(new Equation("5", 0), new Equation("SIMPLIFY_SUM(PLUS(2, 3, 0))",1));
        assertEquals(new Equation("TIMES(2, _x)", 0), new Equation("SIMPLIFY_SUM(PLUS(TIMES(1, _x), _x))",1));
        assertEquals(new Equation("ADD(4, _x)", 0), new Equation("SIMPLIFY_SUM(PLUS(PLUS(2, _x), 2))",1));
        assertEquals(new Equation("ADD(6, _x)", 0), new Equation("SIMPLIFY_SUM(PLUS(2, PLUS(2, _x), 2))",1));
        assertEquals(new Equation("TIMES(2, _x)", 0), new Equation("SIMPLIFY_SUM(PLUS(_x, _x))",1));
        assertEquals(new Equation("TIMES(3, _x)", 0), new Equation("SIMPLIFY_SUM(PLUS(TIMES(2, _x), TIMES(1, _x)))",1));
        assertEquals(new Equation("TIMES(6, _x)", 0), new Equation("SIMPLIFY_SUM(PLUS(TIMES(10, _x), TIMES(-4, _x)))",1));
        assertEquals(new Equation("TIMES(2, _x)", 0), new Equation("SIMPLIFY_SUM(PLUS(TIMES(2, _x), 3, -3))",1));
        assertEquals(new Equation("0", 0), new Equation("SIMPLIFY_SUM(PLUS(TIMES(3, _x), TIMES(-2, _x), TIMES(-1, _x)))",1));
        assertEquals(new Equation("1", 0), new Equation("SIMPLIFY_SUM(PLUS(1, TIMES(3, _x), TIMES(-2, _x), TIMES(-1, _x)))",1));
        assertEquals(new Equation("PLUS(TIMES(3, _x), TIMES(-3, _y))", 0), new Equation("SIMPLIFY_SUM(PLUS(TIMES(3, _x), TIMES(-2, _y), TIMES(-1, _y)))",1));
        assertEquals(new Equation("PLUS(TIMES(3, _x), TIMES(-3, _y))", 0), new Equation("SIMPLIFY_SUM(PLUS(TIMES(-2, _y), TIMES(3, _x), TIMES(-1, _y)))",1));
        assertEquals(new Equation("TIMES(3, POWER(_x, 2))", 0), new Equation("SIMPLIFY_SUM(PLUS(POWER(_x, 2), TIMES(2, POWER(_x, 2))))",1));
        assertEquals(new Equation("TIMES(2, TIMES(_y, POWER(_x, 2)))", 0), new Equation("SIMPLIFY_SUM(PLUS(TIMES(_y, POWER(_x, 2)), TIMES(_y, POWER(_x, 2))))",1));
    }

    @Test
    public void testSimplifyProduct() throws Exception {
        assertEquals(new Equation("POWER(_x, 2)", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(_x, _x))", 1));
        assertEquals(new Equation("POWER(_x, 4)", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(_x, _x, _x, _x))", 1));
        assertEquals(new Equation("TIMES(POWER(_x, 2), POWER(_y, 2))", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(_x, _x, _y, _y))", 1));
        assertEquals(new Equation("TIMES(POWER(_x, 2), POWER(_y, 2))", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(_x, _x, _y, _y))", 1));
        assertEquals(new Equation("TIMES(POWER(_x, 2), POWER(_y, 2))", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(_x, _y, _x, _y))", 1));
        assertEquals(new Equation("TIMES(POWER(_x, 2), POWER(_y, 2))", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(_x, _y, _x, _y))", 1));
        assertEquals(new Equation("TIMES(POWER(_x, 2), POWER(_y, 2))", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(_x, _y, _x, _y))", 1));
        assertEquals(new Equation("0", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(0, _x, _y, _x, _y, 1231,1213,62347653,_asdasd))", 1));
        assertEquals(new Equation("0", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(_x, _y, _x, _y, 1231,1213,62347653,_asdasd, 0))", 1));
        assertEquals(new Equation("_x", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(1, _x))", 1));
        assertEquals(new Equation("1", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(POWER(_x, -1), _x))", 1));
        assertEquals(new Equation("1", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(_x, _x, POWER(_x, -2)))", 1));
        assertEquals(new Equation("_x", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(_x, _x, POWER(_x, -1)))", 1));
        assertEquals(new Equation("-1", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(TIMES(-1, POWER(_x, -1)), _x))", 1));
        assertEquals(new Equation("1", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(TIMES(-1, -1, POWER(_x, -1)), _x))", 1));
        assertEquals(new Equation("4", 0), new Equation("SIMPLIFY_PRODUCT(TIMES(2, 2))", 1));
    }

    @Test
    public void testSimplifyPower() throws Exception {
        assertEquals(new Equation("UNDEFINED"), new Equation("SIMPLIFY_POWER(POWER(0, -1))", 1));
        assertEquals(new Equation("_x"), new Equation("SIMPLIFY_POWER(POWER(_x, 1))", 1));
        assertEquals(new Equation("PLUS(2, _x)"), new Equation("SIMPLIFY_POWER(POWER(PLUS(2, _x), 1))", 1));
        assertEquals(new Equation("1"), new Equation("SIMPLIFY_POWER(POWER(PLUS(2, _x), 0))", 1));
    }

    @Test
    public void testAutoSimplify() throws Exception {
        assertEquals(new Equation("2", 2), new Equation("PLUS(1, 1)", 2));
        assertEquals(new Equation("13", 2), new Equation("PLUS(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)", 2));
        assertEquals(new Equation("1", 2), new Equation("PLUS(0, 1)", 2));
        assertEquals(new Equation("PLUS(1, _x)", 2), new Equation("PLUS(_x, 1)", 2));
        assertEquals(new Equation("PLUS(1, _x)", 2), new Equation("PLUS(_x, 1)", 2));
        assertEquals(new Equation("PLUS(2, TIMES(3,_x))", 2), new Equation("PLUS(TIMES(2, _x), PLUS(_x, PLUS(1, 1)))", 2));
        assertEquals(new Equation("PLUS(2, _x, TIMES(2, POWER(_x, 2), POWER(_y, 2)))", 2), new Equation("PLUS(TIMES(2, _x, POWER(_y, 2), POWER(_x, ADD(3, -2))), PLUS(_x, PLUS(1, 1)))", 2));
        assertEquals(new Equation("POWER(_x, TIMES(3, _y))", 2), new Equation("TIMES(POWER(_x, _y), POWER(_x, TIMES(2, _y))))", 2));
    }
//All functions past this point should deal with only autosimplified equations

    @Test
    public void testExpand() throws Exception {
        assertEquals(new Equation("PLUS(TIMES(2,_x) , TIMES(2, _y)))"), new Equation("EXPAND(TIMES(2, PLUS(_x, _y)))"));
        assertEquals(new Equation("PLUS(TIMES(2,_x) , 2)"), new Equation("EXPAND(TIMES(2, PLUS(_x, 1)))"));
        assertEquals(new Equation("PLUS(POWER(_x, 2), TIMES(2, _x, _y), POWER(_y, 2))"), new Equation("EXPAND(POWER(PLUS(_x, _y), 2))"));
        assertEquals(new Equation("PLUS(POWER(_x, 3), TIMES(3, POWER(_x, 2)) , TIMES(3, _x), 1)"), new Equation("EXPAND(POWER(PLUS(_x, 1), 3))"));
    }

    @Test
    public void testDerivative() throws Exception {
        assertEquals(new Equation("1"), Simplifier.simplifyByOperator(new Equation("DERIV(_y, _y)"), true));
        assertEquals(new Equation("0"), Simplifier.simplifyByOperator(new Equation("DERIV(_y, _x)"), true));
        assertEquals(new Equation("2 * _x"), Simplifier.simplifyByOperator(new Equation("DERIV(POWER(_x, 2), _x)"), true));
        assertEquals(new Equation("5"), Simplifier.simplifyByOperator(new Equation("DERIV(TIMES(5, _x), _x)"), true));
        assertEquals(new Equation("TIMES(4, _x)"), Simplifier.simplifyByOperator(new Equation("DERIV(TIMES(2, POWER(_x, 2)), _x)"), true));
        assertEquals(new Equation("_y"), Simplifier.simplifyByOperator(new Equation("DERIV((_x * _y), _x)"), true));
        assertEquals(new Equation("_y"), Simplifier.simplifyByOperator(new Equation("DERIV(TIMES(_x, _y, 2), _x)"), true));
    }
}