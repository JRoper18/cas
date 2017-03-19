package Database;

import CAS.Equation;
import CAS.StructuralSub;

/**
 * Created by Jack Roper on 3/17/2017.
 */
public class SubstitutionRuleDatabase {
    public static final String[] rules = {
            "DERIV(POWER(_x_EXPRESSION, _n_EXPRESSION), _d) = TIMES(_n, (_x ^ (_n - 1)), DERIV(_x, _d))",
            "DERIV(_#1, _#2) = 0",
            "DERIV(_#1, _#1) = 1",
            "DERIV((_n_CONSTANT * _f_EXPRESSION), _#1) = TIMES(_n, DERIV(_f, _#1))",
            "DERIV((_n_CONSTANT * _#1), _#1) = _n",
            "DERIV(TIMES(_f_EXPRESSION, _g_EXPRESSION), _#1) = PLUS(TIMES(DERIV(_f, _#1), _g), TIMES((DERIV(_g, _#1), _f)) "
    };
}
