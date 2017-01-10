package CAS;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jack on 1/9/2017.
 */
public class SimplifierTest {

    @Test
    public void testSimplifier() throws Exception {
        Simplifier s = new Simplifier();
        assertEquals(new Equation("false"), s.simplify(new Equation("not ( not ( not ( not ( false ) ) ) )"))); //!!!!false == false
        assertEquals(new Equation("14"), s.simplify(new Equation("plus ( 5 , 9 )")));
        assertEquals(new Equation("-304"), s.simplify(new Equation("minus ( 1 , 305 )")));
        assertEquals(new Equation("10"), s.simplify(new Equation("plus ( 1 , 2 , 3 , 4 )")));
    }
}