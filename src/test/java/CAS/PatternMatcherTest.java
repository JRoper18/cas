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
        assertEquals(true, matcher.patternMatch(new Equation("POWER(_x, 2)", 0), new Equation("POWER(_x, _n)",0)));
        assertEquals(true, matcher.patternMatch(new Equation("POWER(_x, 2)", 0), new Equation("POWER(__x, _n)",0)));
        assertEquals(true, matcher.patternMatch(new Equation("POWER(_x, 2)", 0), new Equation("POWER(_y, _n)",0)));
        assertEquals(false, matcher.patternMatch(new Equation("POWER(_x, 2)", 0), new Equation("POWER(__y, _n)", 0)));
        assertEquals(true, matcher.patternMatch(new Equation("POWER(TIMES(1, 2), 2), ",0), new Equation("POWER(_dave, _n)",0)));

    }
}