import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jack on 1/7/2017.
 */
public class SimplifierTest {

    @Test
    public void testBooleanSimplify() throws Exception {
        Equation test1 = new Equation ("OR ( FALSE , FALSE , TRUE , FALSE , TRUE )");
        assertEquals(new Equation("TRUE"), Simplifier.booleanSimplify(test1));

        Equation test2 = new Equation("EQUALS ( TRUE , FALSE )");
        assertEquals(new Equation("FALSE"), Simplifier.booleanSimplify(test2));

        Equation test3 = new Equation("EQUALS ( PLUS ( _X , 3 ) , PLUS ( _X , 3 )"); //x+3 == x + 3
        assertEquals(new Equation("TRUE"), Simplifier.booleanSimplify(test3));

        Equation test4 = new Equation("EQUALS ( PLUS ( _X , 3 ) , PLUS ( _Y , 3 )"); //x+3 !== y + 3
        assertEquals(new Equation("FALSE"), Simplifier.booleanSimplify(test4));
    }

    @Test
    public void testTypeof() throws Exception {
        Equation test1 = new Equation("TYPEOF ( 3 )");
        assertEquals(new Equation("NUMBER"), Simplifier.getTypeOf(test1));

        Equation test2 = new Equation("TYPEOF ( POWER ( _X , 2 )");
        assertEquals(new Equation("EXPRESSION"), Simplifier.getTypeOf(test2));

        Equation test3 = new Equation("TYPEOF ( DIVIDE ( 1 , 3 ) )");
        assertEquals(new Equation("FRACTION"), Simplifier.getTypeOf(test3));
    }
}