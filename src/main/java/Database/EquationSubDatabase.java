package Database;

import CAS.Equation;
import CAS.EquationSub;

import java.util.HashMap;

/**
 * Created by jack on 1/8/2017.
 */
public class EquationSubDatabase {
    public static final HashMap<String, EquationSub> idLookup = new HashMap<>();
    static {
        idLookup.put("ortrueconditions", new EquationSub(new Equation("PATTERN_OR ( OR ( TRUE , FALSE ) , OR ( FALSE , TRUE ) , OR ( TRUE , TRUE ) )"), new Equation("TRUE")));
        idLookup.put("orfalsecondition", new EquationSub(new Equation("OR ( FALSE , FALSE )"), new Equation("FALSE")));
        idLookup.put("andtruecondition", new EquationSub(new Equation("AND ( TRUE , TRUE )"), new Equation("TRUE")));
        idLookup.put("andfalseconditions", new EquationSub(new Equation("PATTERN_OR ( AND ( TRUE , FALSE ) , AND ( FALSE , TRUE ) , AND ( FALSE , FALSE ) )"), new Equation("FALSE")));
        idLookup.put("equalssame", new EquationSub(new Equation("EQUALS ( _v1 , _v1 )"), new Equation ("TRUE")));
        idLookup.put("equalsdifferent", new EquationSub(new Equation("EQUALS ( _v1 , _v2 )"), new Equation ("FALSE")));
    }
}
