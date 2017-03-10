package CAS;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jack on 1/15/2017.
 */
public class EquationTest {
    @Test
    public void testCompareTo() throws Exception {
        assertEquals(true, new Equation("FRACTION ( 5 , 2)",0).compareTo(new Equation("2",0)) > 0);
        assertEquals(true, new Equation("1",0).compareTo(new Equation("2",0)) < 0);
        assertEquals(true, new Equation("TRUE",0).compareTo(new Equation("FALSE",0)) > 0);
        assertEquals(true, new Equation("6",0).compareTo(new Equation("_a",0)) > 0);
        assertEquals(true, new Equation("PLUS(_a, _b)",0).compareTo(new Equation("PLUS ( _a, _c)",0)) < 0);
        assertEquals(true, new Equation("PLUS(_a, _b, _c)",0).compareTo(new Equation("PLUS(_b,_c,_d)",0)) < 0);
        assertEquals(true, new Equation("PLUS(_c, _d)",0).compareTo(new Equation("PLUS(_b,_c,_d)",0)) < 0);
        assertEquals(true, new Equation("POWER(PLUS(1, _x),2)",0).compareTo(new Equation("POWER(PLUS(1, _y),2)",0)) < 0);
        assertEquals(true, new Equation("POWER(PLUS(1, _x),2)",0).compareTo(new Equation("POWER(PLUS(1, _x),3)",0)) < 0);
        assertEquals(true, new Equation("POWER(PLUS(1, _x),3)",0).compareTo(new Equation("POWER(PLUS(1, _y),2)",0)) < 0);
        assertEquals(true, new Equation("FACTORIAL(2)", 0).compareTo(new Equation("FACTORIAL(3)",0)) < 0);
    }
}