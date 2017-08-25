import CAS.Equation;
import Database.DatabaseConnection;
import Simplification.Simplifier;
import Simplification.SimplifierObjective;
import Simplification.SimplifierResult;

import java.util.Scanner;

/**
 * Created by jack on 1/8/2017.
 */
public class Main {
    public static void main(String[] args) {
        DatabaseConnection.makeConnection();
        /*
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        SimplifierResult data =Simplifier.simplify(new Equation(input), SimplifierObjective.REMOVE_OPERATOR);
        System.out.println(data.steps());
        */
        System.out.println(new Equation("_x(1, 2)", 0));
    }
}
