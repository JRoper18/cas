package Database;

import CAS.Equation;
import CAS.EquationSub;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by jack on 1/8/2017.
 */
public class EquationSubDatabase { //NOTE: I know, I know, this should be in the actual SQLite database. I WILL do that, but I want to keep these here in case something goes wrong or I want to reference them directly. Once every sub is in the database I can remove this, but UNTIL THEN, it's a good idea to keep these here just in case.
    public static final HashSet<EquationSub> subs = new HashSet<>(Arrays.asList(
            new EquationSub(new Equation("PATTERN_OR ( OR ( TRUE , FALSE ) , OR ( FALSE , TRUE ) , OR ( TRUE , TRUE ) )"), new Equation("TRUE")),
            new EquationSub(new Equation("OR ( FALSE , FALSE )"), new Equation("FALSE")),
            new EquationSub(new Equation("AND ( TRUE , TRUE )"), new Equation("TRUE")),
            new EquationSub(new Equation("PATTERN_OR ( AND ( TRUE , FALSE ) , AND ( FALSE , TRUE ) , AND ( FALSE , FALSE ) )"), new Equation("FALSE")),
            new EquationSub(new Equation("EQUALS ( _v1 , _v1 )"), new Equation ("TRUE")),
            new EquationSub(new Equation("EQUALS ( _v1 , _v2 )"), new Equation ("FALSE"))
    ));
}
