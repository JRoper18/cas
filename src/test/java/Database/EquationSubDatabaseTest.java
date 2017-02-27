package Database;

import CAS.Equation;
import CAS.EquationBuilder;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import CAS.Simplifier;
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
        assertEquals(new Equation("2.5"), Simplifier.simplifyByOperator(new Equation("TIMES(5, .5)",0)));
        assertEquals(new Equation("-2"), Simplifier.simplifyByOperator(new Equation("TIMES(FRACTION(1, 2), -4)",0)));
    }
}