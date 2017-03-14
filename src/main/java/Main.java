import CAS.Equation;
import CAS.EquationObjects.MathObject;
import CAS.EquationObjects.MathOperator;
import CAS.PatternMatcher;
import CAS.Simplifier;
import CAS.StructuralSub;
import Database.DatabaseConnection;

/**
 * Created by jack on 1/8/2017.
 */
public class Main {
    public static void main(String[] args){
        DatabaseConnection.makeConnection();
        System.out.println(Simplifier.simplifyByOperator(new Equation("DERIV(POWER(_x, 2))"),new MathObject(MathOperator.DERIVATIVE)));

    }
}
