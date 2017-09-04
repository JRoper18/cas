package CAS;

import Simplification.Simplifier;
import Simplification.SimplifierResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by jack on 1/9/2017.
 */
public class SimplifierTest {
    Simplifier s = new Simplifier();

    /*
    @Test
    public void testGeneralSimplifier() throws Exception {
        assertEquals(new Equation("false"), s.getResult(new Equation("not ( not ( not ( not ( false ) ) ) )"))); //!!!!false == false
        assertEquals(new Equation("14"), s.getResult(new Equation("plus ( 5 , 9 )")));
        assertEquals(new Equation("-304"), s.getResult(new Equation("minus ( 1 , 305 )")));
        assertEquals(new Equation("10"), s.getResult(new Equation("plus ( 1 , 2 , 3 , 4 )")));
    }
    */

    @Test
    public void testMetaFunctions() throws Exception {
        assertEquals(new Equation("2", 0), new Equation("OPERAND(PLUS(4, 2), 1)", 1));
        assertEquals(new Equation("ADJOIN(OPERAND(LIST(23, 4), 1), LIST(2,3,4))", 1), new Equation("LIST(4,2,3,4)"));
    }

    @Test
    public void testFractionSimplification() throws Exception {
        assertEquals(new Equation("FRACTION(2,3)"), new Equation("SIMPLIFY_RATIONAL_FRACTION(FRACTION(4,6))"));
        assertEquals(new Equation("FRACTION(1, 2)"), new Equation("SIMPLIFY_RATIONAL_FRACTION(FRACTION(10000, 20000))"));
    }

    @Test
    public void testSimplifyByOperation() throws Exception {
        assertEquals(new Equation("2"), Simplifier.removeSingleRootOperator.getResult(new Equation("TIMES(1, 2)", 0)));
        assertEquals(new Equation("3"), Simplifier.removeSingleRootOperator.getResult(new Equation("ADD(1, 2)", 0)));
        assertEquals(new Equation("2"), Simplifier.removeSingleRootOperator.getResult(new Equation("DIVIDE(4, 2)", 0)));
    }

    @Test
    public void testRationalSimplification() throws Exception {
        assertEquals(new Equation("3"), new Equation("TIMES ( 1 , PLUS(1, 2))", 2));
        assertEquals(new Equation("FRACTION(-1,2)"), new Equation("(DIVIDE(MINUS(1, 2), 2))", 2));
        assertEquals(new Equation("4.5"), new Equation("DIVIDE(PLUS(8, 1), 2)", 2));
        assertEquals(new Equation("UNDEFINED"), new Equation("(DIVIDE(1, 0))", 2));
    }

    @Test
    public void testSortEquation() throws Exception {
        assertEquals(new Equation("PLUS(_a, _b)", 0), Simplifier.orderEquation.getResult(new Equation("PLUS(_b, _a)", 0)));
        assertEquals(new Equation("PLUS(6, _a)", 0), Simplifier.orderEquation.getResult(new Equation("PLUS(_a, 6)", 0)));
        assertEquals(new Equation("PLUS(TIMES(1, 2), _b)", 0), Simplifier.orderEquation.getResult(new Equation("PLUS(_b, TIMES(1, 2))", 0)));
        assertEquals(new Equation("PLUS(_a, _b)", 0), Simplifier.orderEquation.getResult(new Equation("PLUS(_b, _a)", 0)));
        assertEquals(new Equation("POWER(_y, 2)", 0), Simplifier.orderEquation.getResult(new Equation("POWER(_y, 2)", 0)));
        assertEquals(new Equation("POWER(PLUS(1, _x), 2)", 0), Simplifier.orderEquation.getResult(new Equation("POWER(PLUS(_x, 1), 2)", 0)));
        assertEquals(new Equation("DERIV(PLUS(1, _x), _x)", 0), Simplifier.orderEquation.getResult(new Equation("DERIV(PLUS(_x, 1), _x)", 0)));
        assertEquals(new Equation("DERIV(PLUS(_n_VARCONSTANT, _f), _x)", 0), Simplifier.orderEquation.getResult(new Equation("DERIV(PLUS(_f, _n_VARCONSTANT), _x)", 0)));
        assertEquals(new Equation("TIMES(LN(2), _x)", 0), Simplifier.orderEquation.getResult(new Equation("TIMES(_x, LN(2))", 0)));
    }

    @Test
    public void testStepCollection() throws Exception {
        SimplifierResult test1 = Simplifier.bruteForceRemoveOperator.simplify(new Equation("DERIV(_x^2, _x)"));
        assertEquals(2, test1.stepsTaken.size());
    }

}