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
        SimplifierResult data = Simplifier.simplifyWithData(new Equation("DERIV(TIMES(5, _x), _x)"), true);
        System.out.println(data.steps());
    }
}
