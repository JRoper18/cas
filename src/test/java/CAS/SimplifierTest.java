package CAS;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jack on 1/9/2017.
 */
public class SimplifierTest {
    Simplifier s = new Simplifier();

    /*
    @Test
    public void testGeneralSimplifier() throws Exception {
        assertEquals(new Equation("false"), s.simplify(new Equation("not ( not ( not ( not ( false ) ) ) )"))); //!!!!false == false
        assertEquals(new Equation("14"), s.simplify(new Equation("plus ( 5 , 9 )")));
        assertEquals(new Equation("-304"), s.simplify(new Equation("minus ( 1 , 305 )")));
        assertEquals(new Equation("10"), s.simplify(new Equation("plus ( 1 , 2 , 3 , 4 )")));
    }
    */

    @Test
    public void testMetaFunctions() throws Exception {
        assertEquals(EquationBuilder.makeUnprocessedEquation("2"), s.simplifyMetaFunctions(EquationBuilder.makeUnprocessedEquation("OPERAND(PLUS(4,2),1)")));
    }

    @Test
    public void testFractionSimplification() throws Exception {
        assertEquals(new Equation("FRACTION(2,3)"), new Equation("SIMPLIFY_RATIONAL_FRACTION(FRACTION(4,6))"));
        assertEquals(new Equation("FRACTION(1, 2)"), new Equation("SIMPLIFY_RATIONAL_FRACTION(FRACTION(10000, 20000))"));
    }

    @Test
    public void testRationalSimplification() throws Exception {
        assertEquals(new Equation("3"), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(TIMES ( 1 , PLUS(1, 2)))"));
        //assertEquals(new Equation("FRACTION(1,2)"), new Equation("SIMPLIFY_RATIONAL_EXPRESSION(DIVIDE(MINUS(1, 2), 2))"));
    }
}