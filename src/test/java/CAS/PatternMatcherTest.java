package CAS;

import CAS.Equation;
import CAS.EquationObjects.MathOperator;
import CAS.PatternMatcher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jack on 12/30/2016.
 */
public class PatternMatcherTest {
    PatternMatcher matcher = new PatternMatcher();

    @Test
    public void testPatternMatching() throws Exception {
        assertEquals(true, matcher.patternMatch(new Equation("POWER(_x, 2)", 0), new Equation("POWER(_x_EXPRESSION, _n_CONSTANT)",0)));
        assertEquals(true, matcher.patternMatch(new Equation("POWER(_x, 2)", 0), new Equation("POWER(__x, _n_EXPRESSION)",0)));
        assertEquals(true, matcher.patternMatch(new Equation("POWER(_x, 2)", 0), new Equation("POWER(_y_EXPRESSION, _n_EXPRESSION)",0)));
        assertEquals(false, matcher.patternMatch(new Equation("POWER(_x, 2)", 0), new Equation("POWER(__y, _n)", 0)));
        assertEquals(true, matcher.patternMatch(new Equation("POWER(TIMES(1, 2), 2), ",0), new Equation("POWER(_dave_EXPRESSION, _n_CONSTANT)",0)));
        assertEquals(true, matcher.patternMatch(new Equation("1",0), new Equation("_x_INTEGER",0)));
        assertEquals(false, matcher.patternMatch(new Equation("_x",0), new Equation("_x_INTEGER",0)));
        assertEquals(false, matcher.patternMatch(new Equation("_y",0), new Equation("_x_INTEGER",0)));
        assertEquals(true, matcher.patternMatch(new Equation("ADD(1, 2)",2), new Equation("_x_INTEGER",0)));
        assertEquals(true, matcher.patternMatch(new Equation("_x_INTEGER",0), new Equation("_x_INTEGER",0)));
        assertEquals(true, matcher.patternMatch(new Equation("_y_INTEGER",0), new Equation("_x_INTEGER",0)));
        assertEquals(true, matcher.patternMatch(new Equation("PLUS(_x_VARIABLE, _x_VARIABLE)", 0), new Equation("PLUS(_#2, _#2)",0)));
        assertEquals(false, matcher.patternMatch(new Equation("PLUS(_x_VARIABLE, _y_VARIABLE)",0), new Equation("PLUS(_#2, _#2)",0)));
        assertEquals(true, matcher.patternMatch(new Equation("PLUS(_y_VARIABLE, _y_VARIABLE)",0), new Equation("PLUS(_#2, _#2)",0)));
        assertEquals(true, matcher.patternMatch(new Equation("PLUS(_x, _y)"), new Equation("PLUS(_#1, _#2)")));
        assertEquals(false, matcher.patternMatch(new Equation("DERIV(_x, _x)", 0), new Equation("DERIV(_#1, _#2)", 0)));
        assertEquals(false, matcher.patternMatch(new Equation("PLUS(_y_VARIABLE, _y_VARIABLE)",0), new Equation("PLUS(_#2, _#1)",0)));
        assertEquals(true, matcher.patternMatch(new Equation("PLUS(_y_VARIABLE, _x_VARIABLE)",0), new Equation("PLUS(_#2, _#1)",0)));

    }

    @Test
    public void testTooManyOperands() throws Exception {
        assertEquals(true, matcher.patternMatch(new Equation("PLUS(1, 2, 3, 4, 5)",0), new Equation("PLUS(1, _x_EXPRESSION)",0)));
        assertEquals(new Equation("PLUS(2, 3, 4, 5)", 0).tree , matcher.getLastMatchExpressions().get("x"));
        assertEquals(true, matcher.patternMatch(new Equation("PLUS(1, 2)",0), new Equation("PLUS(1, 2, _x_EXPRESSION)",0)));
        assertEquals(new Equation("0", 0).tree , matcher.getLastMatchExpressions().get("x"));
        assertEquals(false, matcher.patternMatch(new Equation("POWER(1, 2, 3, 4, 5)",0), new Equation("POWER(1, _x_EXPRESSION)",0)));
        assertEquals(true, matcher.patternMatch(new Equation("POWER(1, 2)",0), new Equation("POWER(1, _x_EXPRESSION)",0)));
    }
}