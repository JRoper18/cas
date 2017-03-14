import CAS.Equation;
import CAS.PatternMatcher;
import CAS.StructuralSub;
import Database.DatabaseConnection;

/**
 * Created by jack on 1/8/2017.
 */
public class Main {
    public static void main(String[] args){
        DatabaseConnection.makeConnection();
        StructuralSub sub = new StructuralSub(new Equation("DERIVATIVE(POWER(_x, _n))"), new Equation("TIMES(_n, POWER(_x, MINUS(_n, 1)))"));
        Equation test = new Equation("POWER(_x, 2)");
        System.out.println(sub.apply(test));

    }
}
