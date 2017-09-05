import CAS.Equation;
import Database.DatabaseConnection;
import Simplification.Simplifier;
import Simplification.SimplifyObjectiveNotDoneException;

/**
 * Created by jack on 1/8/2017.
 */
public class Main {
    public static void main(String[] args) {
        DatabaseConnection.makeConnection();

        try{
            System.out.println(Simplifier.pruningRemoveOperator.simplify(new Equation("DERIV(POWER(PLUS(_x, 1), 5), _x)")).getPrintableSteps());
        } catch (SimplifyObjectiveNotDoneException ex) {

        }
    }
}
