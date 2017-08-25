package CAS;

import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import PatternMatching.PatternMatchResult;
import PatternMatching.PatternMatcher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by jack on 12/30/2016.
 */
public class PatternMatcherTest {
    PatternMatcher matcher = new PatternMatcher();

    @Test
    public void testPatternMatching() throws Exception {
        assertEquals(true, matcher.doesMatchPattern(new Equation("POWER(_x, 2)", 0), new Equation("POWER(_x_EXPRESSION, _n_CONSTANT)",0)));
        assertEquals(true, matcher.doesMatchPattern(new Equation("POWER(_x, 2)", 0), new Equation("POWER(__x, _n_EXPRESSION)",0)));
        assertEquals(true, matcher.doesMatchPattern(new Equation("POWER(_x, 2)", 0), new Equation("POWER(_y_EXPRESSION, _n_EXPRESSION)",0)));
        assertEquals(false, matcher.doesMatchPattern(new Equation("POWER(_x, 2)", 0), new Equation("POWER(__y, _n)", 0)));
        assertEquals(true, matcher.doesMatchPattern(new Equation("POWER(TIMES(1, 2), 2), ",0), new Equation("POWER(_dave_EXPRESSION, _n_CONSTANT)",0)));
        assertEquals(true, matcher.doesMatchPattern(new Equation("1",0), new Equation("_x_INTEGER",0)));
        assertEquals(false, matcher.doesMatchPattern(new Equation("_x",0), new Equation("_x_INTEGER",0)));
        assertEquals(false, matcher.doesMatchPattern(new Equation("_y",0), new Equation("_x_INTEGER",0)));
        assertEquals(true, matcher.doesMatchPattern(new Equation("ADD(1, 2)",2), new Equation("_x_INTEGER",0)));
        assertEquals(true, matcher.doesMatchPattern(new Equation("_x_INTEGER",0), new Equation("_x_INTEGER",0)));
        assertEquals(true, matcher.doesMatchPattern(new Equation("_y_INTEGER",0), new Equation("_x_INTEGER",0)));
        assertEquals(true, matcher.doesMatchPattern(new Equation("PLUS(_x_VARIABLE, _x_VARIABLE)", 0), new Equation("PLUS(_#2, _#2)",0)));
        assertEquals(false, matcher.doesMatchPattern(new Equation("PLUS(_x_VARIABLE, _y_VARIABLE)",0), new Equation("PLUS(_#2, _#2)",0)));
        assertEquals(true, matcher.doesMatchPattern(new Equation("PLUS(_y_VARIABLE, _y_VARIABLE)",0), new Equation("PLUS(_#2, _#2)",0)));
        assertEquals(true, matcher.doesMatchPattern(new Equation("PLUS(_x, _y)"), new Equation("PLUS(_#1, _#2)")));
        assertEquals(false, matcher.doesMatchPattern(new Equation("DERIV(_x, _x)", 0), new Equation("DERIV(_#1, _#2)", 0)));
        assertEquals(false, matcher.doesMatchPattern(new Equation("PLUS(_y_VARIABLE, _y_VARIABLE)",0), new Equation("PLUS(_#2, _#1)",0)));
        assertEquals(true, matcher.doesMatchPattern(new Equation("PLUS(_y_VARIABLE, _x_VARIABLE)",0), new Equation("PLUS(_#2, _#1)",0)));
    }

    @Test
    public void testFunctionMatching() throws Exception {
        PatternMatchResult res1 = matcher.patternMatch(new Equation("PLUS(1, 2)", 0), new Equation("_x_GENERICFUNCTION(1, 2)"));
        assertTrue(res1.match);
        assertEquals(new MathObject(MathOperator.ADD), res1.variableValues.get("_x_GENERICFUNCTION").tree.data);
    }

    @Test
    public void testTooManyOperands() throws Exception {
        PatternMatchResult data = matcher.patternMatch(new Equation("PLUS(1, 2, 3, 4, 5)",0), new Equation("PLUS(1, _x_EXPRESSION)",0));
        assertTrue(data.match);
        assertEquals(new Equation("PLUS(2, 3, 4, 5)", 0) , data.variableValues.get("x"));
        data = matcher.patternMatch(new Equation("PLUS(1, 2)",0), new Equation("PLUS(1, 2, _x_EXPRESSION)",0));
        assertTrue(data.match);
        assertEquals(new Equation("0", 0), data.variableValues.get("x"));
        assertEquals(false, matcher.doesMatchPattern(new Equation("POWER(1, 2, 3, 4, 5)",0), new Equation("POWER(1, _x_EXPRESSION)",0)));
        assertEquals(true, matcher.doesMatchPattern(new Equation("POWER(1, 2)",0), new Equation("POWER(1, _x_EXPRESSION)",0)));
    }

    @Test
    public void testVariableSaving() throws Exception {
        PatternMatchResult data = matcher.patternMatch(new Equation("TIMES(2, PLUS(1, _x))", 0), new Equation("TIMES(2, _x_EXPRESSION)",0));
        assertTrue(data.match);
        assertEquals(new Equation("PLUS(1, _x)", 0), data.variableValues.get("x"));
        data = matcher.patternMatch(new Equation("DERIV(TIMES(5, _x), _x)", 0), new Equation("DERIV(TIMES(_n_VARCONSTANT, _f_EXPRESSION), _#1)",0));
        assertTrue(data.match);
        assertEquals(new Equation("_x", 0), data.variableValues.get("f"));
        data = matcher.patternMatch(new Equation("DERIV(TIMES(2, _x, _y), _x)"), new Equation("DERIV(TIMES(_f_EXPRESSION, _g_EXPRESSION), _d)"));
        assertEquals(new Equation("2", 0), data.variableValues.get("f"));
        assertEquals(new Equation("TIMES(_x, _y)", 0), data.variableValues.get("g"));

    }

    @Test
    public void testErrorPath() throws Exception {
        PatternMatchResult data = matcher.patternMatch(new Equation("PLUS(1, 2)", 0), new Equation("PLUS(1, 3)", 0));
        assertEquals(new Equation("3"), data.expected());
        assertEquals(new Equation("2"), data.actual());
        data = matcher.patternMatch(new Equation("PLUS(_x_VARIABLE, _y_VARIABLE)",0), new Equation("PLUS(_#2, _#2)",0));
        assertEquals(new Equation("_x"), data.expected());
        assertEquals(new Equation("_y"), data.actual());
    }
}