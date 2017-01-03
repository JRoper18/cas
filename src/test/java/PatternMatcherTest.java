import static org.junit.Assert.assertEquals;

/**
 * Created by jack on 12/30/2016.
 */
public class PatternMatcherTest {
    EquationBuilder builder = new EquationBuilder();
    PatternMatcher matcher = new PatternMatcher();
    @org.junit.Test
    public void testPatternMatching() throws Exception {
        Equation pattern1 = builder.makeEquation("PLUS ( _ , _ )");
        Equation test1 = builder.makeEquation("PLUS ( 1 , 1 )");

        assertEquals(true, matcher.patternMatch(test1, pattern1));
    }
}