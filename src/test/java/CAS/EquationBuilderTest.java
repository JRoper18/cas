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
        Equation test1 = builder.makeEquation("ADD ( 4 , 5 )");
        Tree<MathObject> expectedTree1 = new Tree<>(new MathObject(MathOperator.ADD));
        expectedTree1.addChildWithData(new MathInteger(4));
        expectedTree1.addChildWithData(new MathInteger(5));
        assertEquals(new Equation(expectedTree1), test1);
    }
    @Test
    public void testPreSimplification() throws Exception {
        assertEquals(new Equation("FRACTION ( 1 , 3 )"), new Equation("DIVIDE ( 1 , 3 )"));
        assertNotEquals(new Equation("FRACTION ( 1 , _v1 )"), new Equation("DIVIDE ( 1 , _v1 )"));
    }

    @Test
    public void testInfixToPrefix() throws Exception {
        assertEquals(new Equation("PLUS(1,2)"),new Equation("1 + 2"));
        assertEquals(new Equation("MINUS(1,2)"),new Equation("1 - 2"));
        assertEquals(new Equation("PLUS(1,TIMES(2, 4))"),new Equation("1 + ( 2 * 4 )"));

    }
}