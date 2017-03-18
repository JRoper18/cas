package CAS;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ulysses Howard Smith on 3/17/2017.
 */
public class StructuralSubTest {

    @Test
    public void testSubstitution() throws Exception {
        StructuralSub sub1 = new StructuralSub(new Equation("1"), new Equation("2"));
        assertEquals(new Equation("2"), sub1.apply(new Equation("1")));
        assertEquals(new Equation("2"), sub1.apply(new Equation("TIMES(1, 1)")));
        assertEquals(new Equation("_x"), sub1.apply(new Equation("_x")));
        assertEquals(new Equation("_x"), sub1.apply(new Equation("TIMES(1, _x)")));
        StructuralSub sub2 = new StructuralSub("_x + _y", "1 + _x");
        assertEquals(new Equation("1 + _x"), sub2.apply(new Equation("_x + _y")));
        assertEquals(new Equation("1 + _x"), sub2.apply(new Equation("_x + TIMES(2, _z)")));
        assertEquals(new Equation("4"), sub2.apply(new Equation("3 + _x")));
        assertEquals(new Equation("9"), sub2.apply(new Equation("3 + TIMES(2, 3)"))); //Just a test to see if it will only look at simplified form (it should)

        StructuralSub sub3 = new StructuralSub("_x_INTEGER", "TRUE");
        assertEquals(new Equation("TRUE"), sub3.apply(new Equation("1")));
        assertEquals(new Equation("1.5"), sub3.apply(new Equation("1.5")));
        assertEquals(new Equation("TRUE"), sub3.apply(new Equation("_y_INTEGER")));
        assertEquals(new Equation("TRUE"), sub3.apply(new Equation("__y_INTEGER")));

        StructuralSub subDer = new StructuralSub(new Equation("DERIVATIVE(POWER(_x, _n))"), new Equation("TIMES(_n, POWER(_x, MINUS(_n, 1)))"));
    }
}