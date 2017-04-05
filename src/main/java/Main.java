import CAS.*;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import Database.DatabaseConnection;
import Database.EquationSubDatabase;
import Database.SubSerializer;

import java.sql.ResultSet;

/**
 * Created by jack on 1/8/2017.
 */
public class Main {
    public static void main(String[] args) {
        DatabaseConnection.makeConnection();
        StructuralSub sub = new StructuralSub("DERIV((_f_EXPRESSION * _g_EXPRESSION), _d) -> PLUS(TIMES(DERIV(_f, _d), _g), TIMES(DERIV(_g, _d), _f))");
        PatternMatcher matcher = new PatternMatcher();
        matcher.patternMatch(new Equation("DERIV(TIMES(_x, _y), _x)"), new Equation("DERIV((_f_EXPRESSION * _g_EXPRESSION), _d)"));
        System.out.println(matcher.getLastMatchExpressions());
    }
}
