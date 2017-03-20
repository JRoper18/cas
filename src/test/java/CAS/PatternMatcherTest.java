package CAS;

import CAS.Equation;
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
        System.out.println(new Equation("DERIV(TIMES(_n_CONSTANT, _f), _#1)"));
        System.out.println(new Equation("DERIV(TIMES(2, _f), _x)"));
        System.out.println(matcher.patternMatch(new Equation("DERIV(TIMES(2, _f), _x)"), new Equation("DERIV(TIMES(_n_CONSTANT, _f), _#1)")));
        assertEquals(true, matcher.patternMatch(new Equation("DERIV((2 * (_x ^ 2)), _x)",0), new Equation("DERIV((_n_CONSTANT * _f_EXPRESSION), _#1)", 0)));
    }
}