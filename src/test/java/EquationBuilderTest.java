import EquationObjects.MathObjects.MathNumberInteger;
import EquationObjects.MathObjects.MathNumberRational;
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
        assertEquals();

    }
}