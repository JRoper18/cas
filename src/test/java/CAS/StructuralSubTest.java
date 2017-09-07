package CAS;

import Substitution.StructuralSub;
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
        StructuralSub sub5 = new StructuralSub("PLUS(1, _f_EXPRESSION) -> TIMES(2, _f_EXPRESSION)");
        assertEquals(new Equation("TIMES(2, _x)"), sub5.apply(new Equation("PLUS(1, _x)")));
    }

    @Test
    public void testConstructors() throws Exception {
        assertEquals(new StructuralSub("1", "0"), new StructuralSub("1 -> 0"));
    }
}