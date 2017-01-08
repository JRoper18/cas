import EquationObjects.MathObjects.*;
import EquationObjects.RationalTempInfoHolder;
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
        assertEquals(new RationalTempInfoHolder(new MathInteger(25), new MathInteger(10)), builder.parseString("2.5"));
        assertEquals(new MathInteger(300), builder.parseString("300"));
    }

    @Test
    public void testTreeCreation() throws Exception {
        Equation test1 = builder.makeEquation("ADD ( 4 , 5 )");
        Tree<MathObject> expectedTree1 = new Tree<>(new MathObject(MathSymbol.ADD));
        expectedTree1.addChildWithData(new MathInteger(4));
        expectedTree1.addChildWithData(new MathInteger(5));
        assertEquals(new Equation(expectedTree1), test1);
    }

    @Test
    public void testPreSimplification() throws Exception {
        assertEquals(new Equation("FRACTION ( 1 , 3 )"), new Equation("DIVIDE ( 1 , 3 )"));
        assertNotEquals(new Equation("FRACTION ( 1 , _v1 )"), new Equation("DIVIDE ( 1 , _v1 )"));
    }
}