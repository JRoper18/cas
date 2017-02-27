package CAS;

import CAS.EquationObjects.MathInteger;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by jack on 12/30/2016.
 */
public class EquationBuilderTest {
    EquationBuilder builder = new EquationBuilder();

    @Test
    public void testStringTokenizer() throws Exception {
        assertEquals(new MathInteger(300), builder.parseString("300"));
    }

    @Test
    public void testTreeCreation() throws Exception {
        Equation test1 = builder.makeEquation("ADD ( 4 , 5 )", 0);
        Tree<MathObject> expectedTree1 = new Tree<>(new MathObject(MathOperator.ADD));
        expectedTree1.addChildWithData(new MathInteger(4));
        expectedTree1.addChildWithData(new MathInteger(5));
        assertEquals(new Equation(expectedTree1), test1);
    }

    @Test
    public void testDecimalToFraction() throws Exception {
        assertEquals(new Equation("FRACTION(1, 2)", 0), new Equation(".5"));
        assertEquals(new Equation("FRACTION(-1, 2)", 0), new Equation("-.5"));
        assertEquals(new Equation("FRACTION(-1, 2)", 0), new Equation("-0.5"));
        assertEquals(new Equation("TIMES(1, FRACTION(1, 2))", 0), new Equation("TIMES(1, .5)",0));
    }
}