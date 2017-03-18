package Database;

import CAS.Equation;
import CAS.StructuralSub;

/**
 * Created by Jack Roper on 3/17/2017.
 */
public class SubstitutionRuleDatabase {
    public static final String[] rules = {
            "DERIV(POWER(_#1, _n_EXPRESSION), _#1) = _n * (_#1 ^ (_n - 1))",
    };
}
