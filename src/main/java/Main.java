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

    }
}
