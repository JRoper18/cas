import EquationObjects.MathObjects.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jack on 12/30/2016.
 */
public class EquationBuilderTest {
    EquationBuilder builder = new EquationBuilder();

    @Test
    public void testStringTokenizer() throws Exception {
        assertEquals(new MathNumberRational(25, 10), builder.parseString("2.5"));
        assertEquals(new MathNumberInteger(300), builder.parseString("300"));
    }

    @Test
    public void testTreeCreation() throws Exception {
        Equation test1 = builder.makeEquation("ADD ( 4 , 5 )");
        Tree<MathObject> expectedTree1 = new Tree<>(new MathObject(MathSymbol.ADD));
        expectedTree1.addChildWithData(new MathNumberInteger(4));
        expectedTree1.addChildWithData(new MathNumberInteger(5));
        assertEquals(new Equation(expectedTree1), test1);
    }

}