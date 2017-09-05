import CAS.Equation;
import Database.DatabaseConnection;
import PatternMatching.PatternMatchResult;
import PatternMatching.PatternMatcher;
import Simplification.Simplifier;
import Simplification.SimplifyObjectiveNotDoneException;
import Substitution.StructuralSub;
import Util.Tree;

/**
 * Created by jack on 1/8/2017.
 */
public class Main {
    public static void main(String[] args) {
        DatabaseConnection.makeConnection();


        try{
            System.out.println( Simplifier.pruningRemoveOperator.simplify(new Equation("DERIV(SIN(COS(_x)), _x)")).getPrintableSteps());
        } catch (SimplifyObjectiveNotDoneException ex) {

        }

    }
}
