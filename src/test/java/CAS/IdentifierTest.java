package CAS;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jack on 1/15/2017.
 */
public class IdentifierTest {

    @Test
    public void testAutoSimplifiedRecognition() throws Exception {
        assertEquals(false, EquationBuilder.makeUnprocessedEquation("PLUS(2, 3)").isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION)); //Could be simplified to 5
        assertEquals(true, EquationBuilder.makeUnprocessedEquation("5").isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION));
        assertEquals(false, EquationBuilder.makeUnprocessedEquation("TIMES(1, _x)").isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION)); //No 1* anything
        assertEquals(true, EquationBuilder.makeUnprocessedEquation("_x").isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION));
        assertEquals(false, EquationBuilder.makeUnprocessedEquation("PLUS ( _x , TIMES ( 2, _x))").isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION));
        assertEquals(false, EquationBuilder.makeUnprocessedEquation("PLUS ( _x , TIMES ( _x, _2))").isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION)); //x + 2x could be simplified
        assertEquals(true, EquationBuilder.makeUnprocessedEquation("PLUS(_y, 3)").isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION));
        assertEquals(false, EquationBuilder.makeUnprocessedEquation("PLUS(3, _y)").isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION)); //Wrong order
        assertEquals(true, EquationBuilder.makeUnprocessedEquation("TIMES(POWER(_x, 2)), _x)").isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION)); //x^2*x => x^3
        assertEquals(true, EquationBuilder.makeUnprocessedEquation("PLUS(POWER(_x, 2), POWER(_x, 3))").isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION));//x^2 + x^3 cant be simplified
        assertEquals(false, EquationBuilder.makeUnprocessedEquation("PLUS(POWER(_x, 3), POWER(_x, 2))").isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION)); //Lower powers in front of higher: x^3 + x^2 should be x^2 + x^3
        assertEquals(true, EquationBuilder.makeUnprocessedEquation("PLUS(POWER(_x, 3), POWER(_y, 2))").isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION));
        assertEquals(false, EquationBuilder.makeUnprocessedEquation("PLUS(POWER(_y, 2), POWER(_x, 3))").isType(SimplificationType.AUTOSIMPLIFIED_EXPRESSION)); //Variable name matters in ordering

    }
}