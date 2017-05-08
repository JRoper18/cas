package CAS;

import CAS.EquationObjects.GenericExpression;
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
        assertEquals(new GenericExpression("y", false, IdentificationType.INTEGER), builder.parseString("_y_INTEGER"));
        assertEquals(new GenericExpression("y", false, IdentificationType.EXPRESSION), builder.parseString("_y_EXPRESSION"));
        assertEquals(new GenericExpression("y", false, IdentificationType.CONSTANT), builder.parseString("_y_CONSTANT"));
        assertEquals(new GenericExpression("y", false, IdentificationType.VARIABLE), builder.parseString("_y"));
        assertEquals(new GenericExpression("y", true, IdentificationType.VARIABLE), builder.parseString("__y"));
    }

    @Test
    public void testTreeCreation() throws Exception {
        Equation test1 = builder.makeEquation("ADD ( 4 , 5 )", 0);
        test1.tree.print();
        Tree<MathObject> expectedTree1 = new Tree<>(new MathObject(MathOperator.ADD));
        expectedTree1.addChildWithData(new MathInteger(4));
        expectedTree1.addChildWithData(new MathInteger(5));
        assertEquals(new Equation(expectedTree1), test1);

        Equation test2 = new Equation("ADD(1, TIMES(3, 4))", 0);
    }

    @Test
    public void testDecimalToFraction() throws Exception {
        new Equation("SIN(-1)",0);
        assertEquals(new Equation("FRACTION(5, 10)", 0), new Equation(".5",0));
        assertEquals(new Equation("FRACTION(-5, 10)", 0), new Equation("-0.5",0));
        assertEquals(new Equation("TIMES(1, FRACTION(5, 10))", 0), new Equation("TIMES(1, .5)",0));
    }

    @Test
    public void testInfixToPrefix() throws Exception {
        assertEquals(new Equation("PLUS(1, 2)", 0), new Equation("1 + 2", 0));
        assertEquals(new Equation("PLUS(1, 2)", 0), new Equation("1 + 2", 0));
        assertEquals(new Equation("TIMES(1, 2)", 0), new Equation("1 * 2", 0));
        assertEquals(new Equation("PLUS(PLUS(1, 2), 3)", 0), new Equation("PLUS(1, 2) + 3", 0));
        assertEquals(new Equation("PLUS(PLUS(1, 2), 3)", 0), new Equation("(1 + 2) + 3", 0));
        assertEquals(new Equation("PLUS(TIMES(1, 2), 3)", 0), new Equation("(1 * 2) + 3", 0));
        assertEquals(new Equation("TIMES(1, PLUS(2, 3))", 0), new Equation("1 * (2 + 3)", 0));
        assertEquals(new Equation("PLUS(1, PLUS(2, 3))", 0), new Equation("1 + 2 + 3", 0));
        assertEquals(new Equation("PLUS(1, PLUS(2, PLUS(3, 4)))", 0), new Equation("1 + 2 + 3 + 4", 0));
        assertEquals(new Equation("PLUS(1, PLUS(2, PLUS(3, 4)))", 0), new Equation("1 + 2 + PLUS(3, 4)", 0));
        assertEquals(new Equation("PLUS(1, TIMES(2, PLUS(3, 4)))", 0), new Equation("1 + 2 * 3 + 4", 0));
        assertEquals(new Equation("POWER(_x, 2)", 0), new Equation("_x ^ 2", 0));
        assertEquals(new Equation("POWER(_x, SIN(2))", 0), new Equation("_x ^ SIN(2)", 0));
        assertEquals(new Equation("POWER(PLUS(1, 2), 2)", 0), new Equation("POWER(1 + 2, 2)", 0));
    }

}