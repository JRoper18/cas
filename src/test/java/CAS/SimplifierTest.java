package CAS;

import Simplification.Simplifier;
import Simplification.SimplifierObjective;
import Simplification.SimplifierResult;
import Substitution.StructuralSub;
import org.junit.Test;

import java.util.ArrayList;

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
        assertEquals(new Equation("false"), s.simplify(new Equation("not ( not ( not ( not ( false ) ) ) )"))); //!!!!false == false
        assertEquals(new Equation("14"), s.simplify(new Equation("plus ( 5 , 9 )")));
        assertEquals(new Equation("-304"), s.simplify(new Equation("minus ( 1 , 305 )")));
        assertEquals(new Equation("10"), s.simplify(new Equation("plus ( 1 , 2 , 3 , 4 )")));
    }
    */

    @Test
    public void testMetaFunctions() throws Exception {
        assertEquals(EquationBuilder.makeUnprocessedEquation("2"), s.directSimplify(EquationBuilder.makeUnprocessedEquation("OPERAND(PLUS(4,2),1)"), SimplifierObjective.REMOVE_META));
        assertEquals(new Equation("ADJOIN(OPERAND(LIST(23, 4), 1), LIST(2,3,4))", 1), new Equation("LIST(4,2,3,4)"));
    }

    @Test
    public void testFractionSimplification() throws Exception {
        assertEquals(new Equation("FRACTION(2,3)"), new Equation("SIMPLIFY_RATIONAL_FRACTION(FRACTION(4,6))"));
        assertEquals(new Equation("FRACTION(1, 2)"), new Equation("SIMPLIFY_RATIONAL_FRACTION(FRACTION(10000, 20000))"));
    }

    @Test
    public void testSimplifyByOperation() throws Exception {
        assertEquals(new Equation("2"), Simplifier.directSimplify(new Equation("TIMES(1, 2)", 0), SimplifierObjective.SIMPLIFY_TOP_OPERATOR));
        assertEquals(new Equation("3"), Simplifier.directSimplify(new Equation("ADD(1, 2)", 0), SimplifierObjective.SIMPLIFY_TOP_OPERATOR));
        assertEquals(new Equation("2"), Simplifier.directSimplify(new Equation("DIVIDE(4, 2)", 0), SimplifierObjective.SIMPLIFY_TOP_OPERATOR));
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
        assertEquals(new Equation("PLUS(_a, _b)", 0), Simplifier.orderEquation(new Equation("PLUS(_b, _a)", 0)));
        assertEquals(new Equation("PLUS(6, _a)", 0), Simplifier.orderEquation(new Equation("PLUS(_a, 6)", 0)));
        assertEquals(new Equation("PLUS(TIMES(1, 2), _b)", 0), Simplifier.orderEquation(new Equation("PLUS(_b, TIMES(1, 2))", 0)));
        assertEquals(new Equation("PLUS(_a, _b)", 0), Simplifier.orderEquation(new Equation("PLUS(_b, _a)", 0)));
        assertEquals(new Equation("POWER(_y, 2)", 0), Simplifier.orderEquation(new Equation("POWER(_y, 2)", 0)));
        assertEquals(new Equation("POWER(PLUS(1, _x), 2)", 0), Simplifier.orderEquation(new Equation("POWER(PLUS(_x, 1), 2)", 0)));
        assertEquals(new Equation("DERIV(PLUS(1, _x), _x)", 0), Simplifier.orderEquation(new Equation("DERIV(PLUS(_x, 1), _x)", 0)));
        assertEquals(new Equation("DERIV(PLUS(_n_VARCONSTANT, _f), _x)", 0), Simplifier.orderEquation(new Equation("DERIV(PLUS(_f, _n_VARCONSTANT), _x)", 0)));
        assertEquals(new Equation("TIMES(LN(2), _x)", 0), Simplifier.orderEquation(new Equation("TIMES(_x, LN(2))", 0)));
    }

    @Test
    public void testStepCollection() throws Exception {
        SimplifierResult test1 = Simplifier.simplify(new Equation("DERIV(_x^2, _x)"), SimplifierObjective.SIMPLIFY_TOP_OPERATOR);
        assertEquals(0, test1.changes.size());
        assertEquals(1, test1.subsUsed.size());
    }

    @Test
    public void testResultCombination() throws Exception {
        SimplifierResult res1 = new SimplifierResult(new Equation("1"));
        SimplifierResult res2 = new SimplifierResult(new Equation("1"));
        res2.subsUsed.add(new StructuralSub("1 -> 2"));
        res2.result = new Equation("2");
        res1.combine(res2);
        assertEquals(new Equation("2"), res1.result);
        assertTrue(res1.changes.isEmpty());
        assertEquals(1, res1.subsUsed.size());
    }
}