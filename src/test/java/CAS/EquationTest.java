package CAS;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jack on 1/15/2017.
 */
public class EquationTest {
    @Test
    public void testCompareTo() throws Exception {
        assertEquals(true, new Equation("FRACTION ( 5 , 2)").compareTo(new Equation("2")) > 0);
        assertEquals(true, new Equation("1").compareTo(new Equation("2")) < 0);
        assertEquals(true, new Equation("TRUE").compareTo(new Equation("FALSE")) > 0);
        assertEquals(true, new Equation("PLUS(_a, _b)").compareTo(new Equation("PLUS ( _a, _c)")) < 0);
        assertEquals(true, new Equation("PLUS(_a, _b, _c)").compareTo(new Equation("PLUS(_b,_c,_d)")) < 0);
        assertEquals(true, new Equation("PLUS(_c, _d)").compareTo(new Equation("PLUS(_b,_c,_d)")) < 0);
        assertEquals(true, new Equation("POWER(PLUS(1, _x),2)").compareTo(new Equation("POWER(PLUS(1, _y),2)")) < 0);
        assertEquals(true, new Equation("POWER(PLUS(1, _x),2)").compareTo(new Equation("POWER(PLUS(1, _x),3)")) < 0);
        assertEquals(true, new Equation("POWER(PLUS(1, _x),3)").compareTo(new Equation("POWER(PLUS(1, _y),2)")) < 0);
        assertEquals(true, new Equation("FACTORIAL(2)").compareTo(new Equation("FACTORIAL(3)")) < 0);
    }
}