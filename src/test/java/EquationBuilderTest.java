import EquationObjects.MathObjects.*;
import EquationObjects.RationalTempInfoHolder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        Equation test1 = builder.makeUnsimplifiedEquation("ADD ( 4 , 5 )");
        Tree<MathObject> expectedTree1 = new Tree<>(new MathObject(MathSymbol.ADD));
        expectedTree1.addChildWithData(new MathInteger(4));
        expectedTree1.addChildWithData(new MathInteger(5));
        assertEquals(new Equation(expectedTree1), test1);

    }

    @Test
    public void testAutomaticSimplification() throws Exception {
        Equation test2 = new Equation("DIVIDE ( 4 , 1 )");
        Equation expected2 = new Equation("4");
        assertEquals(expected2, test2);

        Equation test3 = new Equation("MINUS ( 4 , 3 )");
        Equation expected3 = new Equation("1");
        assertEquals(expected3, test3);

        Equation test4 = new Equation("ADD ( 4 , 10298 )");
        Equation expected4 = new Equation("10302");
        assertEquals(expected4, test4);

    }
}