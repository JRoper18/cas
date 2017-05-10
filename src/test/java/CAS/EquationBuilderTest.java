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
        assertEquals(new Equation("FRACTION(1, 2)", 0), new Equation("0.5",0));
        assertEquals(new Equation("FRACTION(-1, 2)", 0), new Equation("-0.5",0));
        assertEquals(new Equation("TIMES(1, FRACTION(1, 2))", 0), new Equation("TIMES(1, 0.5)",0));
    }

    @Test
    public void testUnaryNegative() throws Exception {
        assertEquals(new Tree<MathObject>(new MathInteger(-1)), new Equation("-1", 0).tree);
        Tree<MathObject> test2 = new Tree<>(new MathObject(MathOperator.SINE));
        test2.addChildWithData(new MathInteger(-1));
        assertEquals(test2, new Equation("SIN(-1)", 0).tree);
        Tree<MathObject> test3 = new Tree<>(new MathObject(MathOperator.SUBTRACT));
        test3.addChildWithData(new MathInteger(2));
        test3.addChildWithData(new MathInteger(3));
        assertEquals(test3, new Equation("2-3",0).tree);
        Tree<MathObject> test4 = new Tree<>(new MathObject(MathOperator.MULTIPLY));
        test4.addChildWithData(new MathInteger(2));
        test4.addChildWithData(new MathInteger(-1));
        assertEquals(test4, new Equation("TIMES(2, -1)", 0).tree);
        assertEquals(test4, new Equation("TIMES(2,-1)", 0).tree);
    }
}