package CAS;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jack on 1/15/2017.
 */
public class IdentifierTest {

    @Test
    public void testStandardFractionIdentification() throws Exception {
        assertEquals(true, new Equation("FRACTION(2, 3)").isType(IdentificationType.FRACTION_STANDARD_FORM));
        assertEquals(false, new Equation("FRACTION(4, 6)", 0).isType(IdentificationType.FRACTION_STANDARD_FORM));
    }

    @Test
    public void testAutoSimplifiedRecognition() throws Exception {
        assertEquals(false, new Equation("PLUS(2, 3)",0).isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION)); //Could be simplified to 5
        assertEquals(true, new Equation("5",0).isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION));
        assertEquals(false, new Equation("TIMES(1, _x)",0).isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION)); //No 1* anything
        assertEquals(true, new Equation("_x",0).isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION));
        assertEquals(false, new Equation("PLUS ( _x , TIMES ( 2, _x))",0).isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION));
        assertEquals(false, new Equation("PLUS ( _x , TIMES ( _x, 2))", 0).isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION)); //x + 2x could be simplified
        //assertEquals(true, new Equation("PLUS(_y, 3)").isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION));
        //assertEquals(false, new Equation("PLUS(3, _y)").isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION)); //Wrong order
        //assertEquals(true, new Equation("TIMES(POWER(_x, 2)), _x)").isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION)); //x^2*x => x^3
        //assertEquals(true, new Equation("PLUS(POWER(_x, 2), POWER(_x, 3))").isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION));//x^2 + x^3 cant be simplified
        //assertEquals(false, new Equation("PLUS(POWER(_x, 3), POWER(_x, 2))").isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION)); //Lower powers in front of higher: x^3 + x^2 should be x^2 + x^3
        //assertEquals(true, new Equation("PLUS(POWER(_x, 3), POWER(_y, 2))").isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION));
        //assertEquals(false, new Equation("PLUS(POWER(_y, 2), POWER(_x, 3))").isType(IdentificationType.AUTOSIMPLIFIED_EXPRESSION)); //Variable name matters in ordering

    }
}