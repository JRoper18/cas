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
        StructuralSub sub3 = new StructuralSub("_x_INTEGER", "TRUE");
        assertEquals(new Equation("TRUE"), sub3.apply(new Equation("1")));
        assertEquals(new Equation("1.5"), sub3.apply(new Equation("1.5")));
        assertEquals(new Equation("TRUE"), sub3.apply(new Equation("_y_INTEGER")));
        assertEquals(new Equation("TRUE"), sub3.apply(new Equation("__y_INTEGER")));
        
        StructuralSub sub4 = new StructuralSub(new Equation("DERIV((_n_CONSTANT * _f_EXPRESSION), _#1)"), new Equation("2"));

        assertEquals(new Equation("2"), sub4.apply(new Equation("DERIV(TIMES(2, POWER(_x, 2)), _sd)")));

    }
}