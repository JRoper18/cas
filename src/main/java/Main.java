import CAS.Equation;
import Database.DatabaseConnection;
import Simplification.Simplifier;
import Simplification.SimplifierObjective;
import Simplification.SimplifierResult;

/**
 * Created by jack on 1/8/2017.
 */
public class Main {
    public static void main(String[] args) {
        DatabaseConnection.makeConnection();
        SimplifierResult data =Simplifier.simplify(new Equation("DERIV(TIMES(2, POWER(_x, 2)), _x)"), SimplifierObjective.REMOVE_OPERATOR);
        System.out.println(data.steps());
    }
}
